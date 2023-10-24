package com.example.simplepasswordkeeper

import android.app.Application
import android.net.Uri

class PersistentStorageFactory(private val storageInitDetails: StorageInitDetails) {
    fun create(/*type*/) : IPersistentStorageAccess? {
        val encryptedFileStorage = EncryptedFileStorage(storageInitDetails.application,
            EncryptedFileAccessDetails(storageInitDetails.filepath, storageInitDetails.password ))
        if(encryptedFileStorage.initialize())
            return encryptedFileStorage
        return null
    }

    data class StorageInitDetails (val application: Application, val password:String, val filepath:Uri)
}