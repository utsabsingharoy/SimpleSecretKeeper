package com.example.simplepasswordkeeper

import android.app.Application
import android.net.Uri
import android.util.JsonReader
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class DataViewModel(application: Application) : AndroidViewModel(application) {
    var password = MutableLiveData<String>()
    var filepath = MutableLiveData<Uri>()

    var decryptedString = ""
    var decryptedResultReady = MutableLiveData<Boolean>()
    var modifiedString = MutableLiveData<String>()

    lateinit var storageAccess: IPersistentStorageAccess

    private fun readStorage() {
        storageAccess = EncryptedFileStorage(getApplication<Application>(), EncryptedFileAcessDetails(filepath.value!!, password.value!!))
        storageAccess.readFromPersistentStorage().let { rawJson ->
            if(JsonUtilities.isValidJson(rawJson)) {
                decryptedString = rawJson
                decryptedResultReady.setValue(true)
            }
            else {
                Log.e("TAG", "invalid JSON")
            }
        }
    }

    private fun saveResults() {
        if ( decryptedString != modifiedString.value) {
            storageAccess.writeToPersistentStorage(modifiedString.value!!)
            decryptedString = modifiedString.value?.let {it}?:""
        }
    }

    init {
        password.observeForever { readStorage() }
        modifiedString.observeForever { saveResults() }
    }
}