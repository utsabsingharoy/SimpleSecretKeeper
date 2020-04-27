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

    private fun passwordObserver() {
        try {
            getApplication<Application>().contentResolver
                .openInputStream(filepath.value!!)?.buffered()?.readBytes()?.let {
                    decryptedResult.postValue(NativeBridge().decryptValue(password.value!!, it))
                    //Log.e("TAG", decryptedResult.value!!)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
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