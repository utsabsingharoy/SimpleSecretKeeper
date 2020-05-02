package com.example.simplepasswordkeeper

interface IPersistentStorageAccess {
    fun readFromPersistentStorage() : String
    fun writeToPersistentStorage(data: String)
}