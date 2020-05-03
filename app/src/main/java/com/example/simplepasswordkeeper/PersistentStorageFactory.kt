package com.example.simplepasswordkeeper

import android.app.Application
import android.net.Uri

class PersistentStorageFactory(val storageInitDetails: StorageInitDetails) {
    fun create(/*type*/) : IPersistentStorageAccess? {
        val encryptedFileStorage = EncryptedFileStorage(storageInitDetails.application,
            EncryptedFileAcessDetails(storageInitDetails.filepath, storageInitDetails.password ))
        if(encryptedFileStorage.initialize())
            return encryptedFileStorage
        return null
    }

    data class StorageInitDetails (val application: Application, val password:String, val filepath:Uri)
}