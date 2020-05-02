package com.example.simplepasswordkeeper

import android.app.Application
import android.net.Uri
import android.widget.Toast
import java.io.IOException

typealias EncryptedFileAcessDetails = Pair<Uri, String>

class EncryptedFileStorage(val application: Application, val accessDetail: EncryptedFileAcessDetails) : IPersistentStorageAccess {

    override fun readFromPersistentStorage(): String {
        var str = ""
        try {
            str = application.contentResolver
                .openInputStream(accessDetail.first)?.buffered()?.readBytes()?.let {
                    NativeBridge().decryptValue(accessDetail.second, it).trim()
                } ?: ""
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return str
    }

    override fun writeToPersistentStorage(data: String) {
        try {
            application.contentResolver
                .openOutputStream(accessDetail.first, "w")?.buffered()?.let {
                    it.write(NativeBridge().encryptString(accessDetail.second, data))
                    it.flush()
                    Toast.makeText(application, "Saved", Toast.LENGTH_SHORT).show()
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}