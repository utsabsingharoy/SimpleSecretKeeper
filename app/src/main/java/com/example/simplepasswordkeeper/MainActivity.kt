// Created by Utsab Singha Roy.

package com.example.simplepasswordkeeper

import android.app.Activity
import android.content.Intent
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

        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
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

                it.decryptedResult.observe(this, Observer {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, ViewFragment())
                        .addToBackStack(null).commit()
                })
            }
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
