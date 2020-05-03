package com.example.simplepasswordkeeper

import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import java.text.FieldPosition

class ViewEditViewModel : ViewModel() {

    //lateinit var decryptedData : DecryptedData
    lateinit var persistentStorage : IPersistentStorageAccess

    fun getDataAt(position: Int) : List<SchemaType> {
        return persistentStorage.decryptedData[position]
    }

    fun getTitles() : List<String> {
        return persistentStorage.decryptedData
            .map { it.find{pair->pair.first == "title"}!!.second}
    }

    fun getEntryCount(currentElement : String) : Int {
        return persistentStorage.decryptedData.getEntryCount(currentElement)
    }

    fun saveModification(currentSelection:String, data : List<SchemaType>) {
        persistentStorage.run {
            decryptedData.modifyEntries(currentSelection, data)
            writeToPersistentStorage()
        }
    }
}