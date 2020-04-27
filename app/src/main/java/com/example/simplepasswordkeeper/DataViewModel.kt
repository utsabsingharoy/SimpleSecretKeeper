package com.example.simplepasswordkeeper

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel : ViewModel() {
    var password = MutableLiveData<String>()
    var filepath = MutableLiveData<Uri>()

    //private fun


}