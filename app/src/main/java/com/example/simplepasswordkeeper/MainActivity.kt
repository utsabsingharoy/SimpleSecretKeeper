// Created by Utsab Singha Roy.

package com.example.simplepasswordkeeper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.io.IOException
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var encrypted_result = byteArrayOf()
    //private var password = ""
    private lateinit var model : DataViewModel
    private lateinit var fileuri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DataViewModel::class.java)
        var passwordObserver = Observer<String> {

            supportFragmentManager.findFragmentByTag("PasswordFragment")?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
            }

            try {
                contentResolver.openInputStream(model.filepath.value!!)?.buffered()?.readBytes()?.let {
                    val text = decryptValue(model.password.value!!, it)
                    display_text.setText(text)
                    Log.e("TAG", text)
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }

        model.password.observe(this,passwordObserver)

        model.filepath.observe(this, Observer {
            Log.e("TAG" , "showing fragment")
            PasswordFragment().show(supportFragmentManager.beginTransaction(), "PasswordFragment")
        })

        decrypt_button.setOnClickListener {
            sendFileActionIntent(Intent.ACTION_OPEN_DOCUMENT, DECRYPT_RESULT)
        }

        /*encrypt_button.setOnClickListener {
            if(!password_field.text.isBlank() && !display_text.text.isBlank()) {
                encrypted_result = encryptString(password_field.text.toString(), display_text.text.toString())
                sendFileActionIntent(Intent.ACTION_CREATE_DOCUMENT, SAVE_ENCRYPTED_RESULT)
            }
            else
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun onPause() {
        super.onPause()
        //password_field.text.clear()
        display_text.text.clear()
    }

    private fun sendFileActionIntent(action : String, requestCode: Int) {
        Log.e(MainActivity::class.java.canonicalName, "action is $action")
        val intent = Intent(action).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("text/plain")
            putExtra(Intent.EXTRA_TITLE, "EncryptedFile")
        }
        startActivityForResult(intent,requestCode)
    }

    private fun saveEncryptionResult(uri: Uri) {
        Log.e(MainActivity::class.java.canonicalName, "writing to :${uri.toString()}")
        try {
            contentResolver.openOutputStream(uri, "w")?.buffered()?.let {
                it.write(encrypted_result)
                it.flush()
                display_text.setText("Encrypted")
                encrypted_result = byteArrayOf()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /*private fun decryptResult(uri : Uri) {
        try {
            contentResolver.openInputStream(uri)?.buffered()?.readBytes()?.let {
                //Log.e("CPPTAG", "got total bytes" + it.size)
                display_text.setText(
                    decryptValue(password, it))
                password = ""
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }*/

    /*private fun decryptResult(uri : Uri) {

    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            intent?.data?.let { uri ->
                when (requestCode) {
                    SAVE_ENCRYPTED_RESULT -> saveEncryptionResult(uri)
                    DECRYPT_RESULT -> { model.filepath.postValue(uri) }//decryptResult(uri)
                    else -> {}
                }
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun encryptString(passWord : String, plainText : String) : ByteArray
    external fun decryptValue(passWord: String, cipher : ByteArray) : String

    companion object {

        init {
            System.loadLibrary("native-lib")
        }

        const val SAVE_ENCRYPTED_RESULT = 1
        const val DECRYPT_RESULT = 2
    }
}
