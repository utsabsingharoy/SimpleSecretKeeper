// Created by Utsab Singha Roy.

package com.example.simplepasswordkeeper

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var model : DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(materialToolbar)
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(DataViewModel::class.java)

        /*ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(DataViewModel::class.java).let {
                model = it

                it.filepath.observe(this, Observer {
                    PasswordFragment().show(
                        supportFragmentManager.beginTransaction(),
                        "PasswordFragment"
                    )
                })

                it.password.observe(this, Observer {
                    supportFragmentManager.findFragmentByTag("PasswordFragment")?.let { dialogFragment ->
                        supportFragmentManager.beginTransaction().remove(dialogFragment).commit()
                        (dialogFragment as DialogFragment).dismiss()
                    }
                })

                it.decryptedResultReady.observe(this, Observer {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, ViewEditBaseFragment(model.decryptedString))
                        .addToBackStack(null).commit()
                })
            }*/
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
