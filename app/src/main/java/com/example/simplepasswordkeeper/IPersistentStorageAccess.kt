package com.example.simplepasswordkeeper

interface IPersistentStorageAccess {
    fun writeToPersistentStorage() : Boolean
    fun initialize() : Boolean
    var decryptedData : DecryptedData
}