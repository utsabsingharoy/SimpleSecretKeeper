// Created by Utsab Singha Roy.

package com.example.simplepasswordkeeper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider


class MainActivity : AppCompatActivity() {

    private lateinit var model : DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(DataViewModel::class.java)

        model.password.observe(this, Observer {
            supportFragmentManager.findFragmentByTag("PasswordFragment")?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
            }
        })

        model.filepath.observe(this, Observer {
            PasswordFragment().show(supportFragmentManager.beginTransaction(), "PasswordFragment")
        })

        model.decryptedResult.observe(this, Observer {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_container, ViewFragment())
                .addToBackStack(null).commit()
        })
    }

    fun sendFileActionIntent(action : String, requestCode: Int) {
        Log.e(MainActivity::class.java.canonicalName, "action is $action")
        val intent = Intent(action).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("text/plain")
            putExtra(Intent.EXTRA_TITLE, "EncryptedFile")
        }
        startActivityForResult(intent,requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            intent?.data?.let { uri ->
                when (requestCode) {
                    DECRYPT_RESULT -> { model.filepath.postValue(uri) }//decryptResult(uri)
                    else -> {}
                }
            }
        }
    }

    companion object {
        const val DECRYPT_RESULT = 2
    }
}
