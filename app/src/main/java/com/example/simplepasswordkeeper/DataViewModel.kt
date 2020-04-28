package com.example.simplepasswordkeeper

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class DataViewModel(application: Application) : AndroidViewModel(application) {
    var password = MutableLiveData<String>()
    var filepath = MutableLiveData<Uri>()
    var decryptedResult = MutableLiveData<String>()
    var modifiedData = MutableLiveData<String>()
    var decryptedData = mutableListOf(mutableMapOf<String, String>())

    private fun passwordObserver() {
        try {
            getApplication<Application>().contentResolver
                .openInputStream(filepath.value!!)?.buffered()?.readBytes()?.let {
                    val rawJson = NativeBridge().decryptValue(password.value!!, it)
                    processJson(rawJson)
                    decryptedResult.postValue(rawJson)
                    //Log.e("TAG", decryptedResult.value!!)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }



    private class JsonArrayIterable(val jsonArray: JSONArray) : Iterable<JSONObject> {
        override fun iterator(): Iterator<JSONObject> {
            return JsonArrayIterator(jsonArray)
        }

        private class JsonArrayIterator(val jsonArray: JSONArray) : Iterator<JSONObject> {
            private  var i = 0
            override fun hasNext() : Boolean{
                return i < jsonArray.length()
            }
            override fun next() : JSONObject {
                val obj = jsonArray.getJSONObject(i);
                i++
                return obj
            }
        }
    }

    private fun processJson(rawJson : String) : String {
        rawJson.let { JSONArray(rawJson) }.let { jsonArray ->
            JsonArrayIterable(jsonArray).map {
                var obj = mutableMapOf<String, String>()
                for (name in it.keys())
                    obj.put(name, it.getString(name))
                obj
            }.toMutableList()
        }.also {
            decryptedData = it
        }.forEach {
            Log.e("TAG", it.toString())
        }

        return decryptedData.toString()
    }

    private fun saveResults() {
        if ( decryptedResult.value != modifiedData.value) {
            try {
                getApplication<Application>().contentResolver
                    .openOutputStream(filepath.value!!, "w")?.buffered()?.let {
                        it.write(NativeBridge().encryptString(password.value!!, modifiedData.value!!))
                        it.flush()
                        Toast.makeText(getApplication(), "Saved", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    init {
        password.observeForever { passwordObserver() }
        modifiedData.observeForever {saveResults() }
    }
}