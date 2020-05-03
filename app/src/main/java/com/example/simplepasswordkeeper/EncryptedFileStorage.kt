package com.example.simplepasswordkeeper

import android.app.Application
import android.net.Uri
import android.widget.Toast
import java.io.IOException

typealias EncryptedFileAcessDetails = Pair<Uri, String>

class EncryptedFileStorage(val application: Application, val accessDetail: EncryptedFileAcessDetails) : IPersistentStorageAccess {

    override lateinit var decryptedData: DecryptedData

    private fun readFromPersistentStorage() : Boolean {
        try {
            application.contentResolver
                .openInputStream(accessDetail.first)?.buffered()?.readBytes()?.let {
                    val str = NativeBridge().decryptValue(accessDetail.second, it).trim()
                    if(!JsonUtilities.isValidJson(str))
                        return false
                    decryptedData = DecryptedData(str)
                } ?: return false
        } catch (e : Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun writeToPersistentStorage() : Boolean {
        try {
            application.contentResolver
                .openOutputStream(accessDetail.first, "w")?.buffered()?.let {
                    it.write(NativeBridge().encryptString(accessDetail.second, decryptedData.toPythonString()))
                    it.flush()
                    Toast.makeText(application, "Saved", Toast.LENGTH_SHORT).show()
                }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun initialize(): Boolean {
        return readFromPersistentStorage()
    }
}