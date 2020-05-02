package com.example.simplepasswordkeeper

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonUtilities {

    class JsonArrayIterable(val jsonArray: JSONArray) : Iterable<JSONObject> {
        override fun iterator(): Iterator<JSONObject> = JsonArrayIterator(jsonArray)

        private class JsonArrayIterator(val jsonArray: JSONArray) : Iterator<JSONObject> {
            private  var i = 0
            override fun hasNext() : Boolean{
                return i < jsonArray.length()
            }
            override fun next() : JSONObject {
                val obj = jsonArray.getJSONObject(i)
                i++
                return obj
            }
        }
    }

    companion object {
        fun isValidJson(jsonString : String) : Boolean {
            try {
                JSONArray(jsonString)
            }
            catch (e : JSONException) {
                return false
            }
            return true
        }
    }
}