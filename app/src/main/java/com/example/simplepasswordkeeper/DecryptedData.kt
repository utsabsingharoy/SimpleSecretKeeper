package com.example.simplepasswordkeeper

import android.util.Log
import org.json.JSONArray

typealias SchemaType = Triple<String,String,Boolean>

class DecryptedData(jsonString: String) : Iterable<MutableList<SchemaType>> {
    //Data schema [title, value, isHidden]
    private var decryptedData : MutableList<MutableList<SchemaType>>

    private fun findEntry(title: String) : MutableList<SchemaType>? {
        return decryptedData.find { kvTriple ->
            kvTriple.find { it.first == "title"}?.second == title
        }
    }

    fun modifyEntries(oldTitle : String, entries : List<SchemaType>) {
        Log.d("TAG", "old\n" + findEntry(oldTitle).toString())
        Log.d("TAG", "New\n" + entries.toString())
        findEntry(oldTitle)?.let { tripleList ->
            tripleList.forEachIndexed { index, triple ->
                if(triple.first == entries[index].first &&
                    ((triple.second != entries[index].second) || (triple.third != entries[index].third)))
                    tripleList[index] = Triple(entries[index].first, entries[index].second, entries[index].third)

            }
        }
    }

    fun addField(oldTitle : String, key : String, value : String, hidden : Boolean) {
        findEntry(oldTitle)?.add(Triple(key, value, hidden))
    }

    fun addEntry(newEntry : List<SchemaType>) {
        decryptedData.add(newEntry.toMutableList())
    }

    fun getEntryCount(title : String) : Int {
        return findEntry(title)?.size?:0
    }

    override fun iterator(): Iterator<MutableList<Triple<String,String,Boolean>>> = decryptedData.iterator()

    operator fun get(i : Int) = decryptedData[i]

    private fun<T> List<T>.applyToAllButLast(f : (T) -> T ) : List<T> {
        return this.dropLast(1).map { f(it) } + listOf(this.last())
    }

    fun toPythonString() : String {
        return  decryptedData.map {
                    it.map { triple->
                        "        \"" + triple.first + "\": [\n" +
                        "            \"" + triple.second + "\",\n" +
                        "            " + triple.third.toString() + "\n" +
                        "        ]"
                    }.applyToAllButLast { item -> item + "," }
                        .fold("") { acc, s -> acc + s + "\n" }
                }.map {
                    "    {\n" + it + "    }"
                }.applyToAllButLast {
                    it + ","
                }.fold("") {acc, s ->
                    acc + s + "\n"
                }.run {
                    "[\n" + this + "]"
                }
    }

    init {
        JSONArray(jsonString).let { jsonArray ->
            JsonUtilities.JsonArrayIterable(jsonArray).map {
                var obj = mutableListOf<Triple<String, String,Boolean>>()
                for (name in it.keys()) {
                    val pairArray = it.getJSONArray(name)
                    obj.add(Triple(name, pairArray.getString(0), pairArray.getBoolean(1)))
                }
                obj
            }.toMutableList()
        }.also {
            decryptedData = it
        }/*.forEach {
            Log.e("TAG", it.toString())
        }*/
    }
}