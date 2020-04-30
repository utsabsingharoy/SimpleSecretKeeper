package com.example.simplepasswordkeeper

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.io.IOException

class DataViewModel(application: Application) : AndroidViewModel(application) {
    var password = MutableLiveData<String>()
    var filepath = MutableLiveData<Uri>()
    var decryptedResult = MutableLiveData<String>()
    var modifiedData = MutableLiveData<String>()
    lateinit var decryptedData : DecryptedData

    private fun passwordObserver() {
        try {
            getApplication<Application>().contentResolver
                .openInputStream(filepath.value!!)?.buffered()?.readBytes()?.let {
                    val rawJson = NativeBridge().decryptValue(password.value!!, it).trim()
                    decryptedData = DecryptedData(rawJson)
                    decryptedResult.postValue(rawJson)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    private fun saveResults() {
        //Log.e("TAG" ,"OLD Data\n" + decryptedResult.value)
        //Log.e("TAG" , "mod ds data\n " + decryptedData.toPythonString())
        //Log.e("TAG" ,"new Data\n" + modifiedData.value)

        if ( decryptedResult.value != modifiedData.value) {
            Log.e("TAG", " writing to file")
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