package com.example.simplepasswordkeeper

interface ISavableActions {
    fun onSaveButtonClicked(saveData : List<SchemaType>)
    fun cleanUpActions()
}