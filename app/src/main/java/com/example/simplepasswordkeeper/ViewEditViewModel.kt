package com.example.simplepasswordkeeper

import androidx.lifecycle.ViewModel

class ViewEditViewModel : ViewModel() {

    lateinit var persistentStorage : IPersistentStorageAccess

    fun getDataAt(position: Int) : List<SchemaType> {
        return persistentStorage.decryptedData[position]
    }

    fun getTitles() : List<String> {
        return persistentStorage.decryptedData.getTitles()
    }

    fun getEntryCount(currentElement : String) : Int {
        return persistentStorage.decryptedData.getEntryCount(currentElement)
    }

    fun saveModification(currentSelection:String, data : List<SchemaType>) {
        persistentStorage.run {
            decryptedData.modifyEntrie(currentSelection, data)
            writeToPersistentStorage()
        }
    }

    fun addAndSaveNewEntry(newEntry : List<SchemaType>) {
        persistentStorage.run {
            decryptedData.addEntry(newEntry)
            writeToPersistentStorage()
        }
    }

    fun deleteEntry(title : String) {
        persistentStorage.run {
            decryptedData.deleteEntry(title)
            writeToPersistentStorage()
        }

    }
}