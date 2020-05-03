// Created by Utsab Singha Roy.

package com.example.simplepasswordkeeper

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var model : DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(materialToolbar)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(DataViewModel::class.java)
        model.password.observe(this, Observer {
            PersistentStorageFactory(
                PersistentStorageFactory.StorageInitDetails(application, model.password.value!!, model.filepath.value!!))
                .create()?.let {
                    model.storageAccess = it
                    model.decryptedResultReady.postValue(true)
                }?: {MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("Storage Initialization failed. Check password or file")
                        .setPositiveButton("Ok", null)
                        .show()}()
        })
    }

    fun sendFileActionIntentOpenDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "EncryptedFile")
        }
        startActivityForResult(intent,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            intent?.data?.let { model.filepath.setValue(it) }
        }
    }
}
