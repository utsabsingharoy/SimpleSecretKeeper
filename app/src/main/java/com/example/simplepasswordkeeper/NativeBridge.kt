package com.example.simplepasswordkeeper

class NativeBridge {

    external fun decryptValue(passWord: String, cipher : ByteArray) : String
    external fun encryptString(passWord : String, plainText : String) : ByteArray

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}