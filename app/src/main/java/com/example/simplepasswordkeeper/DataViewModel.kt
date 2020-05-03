package com.example.simplepasswordkeeper

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class DataViewModel(application: Application) : AndroidViewModel(application) {
    var password = MutableLiveData<String>()
    var filepath = MutableLiveData<Uri>()

    var decryptedResultReady = MutableLiveData<Boolean>()

    lateinit var storageAccess: IPersistentStorageAccess

}