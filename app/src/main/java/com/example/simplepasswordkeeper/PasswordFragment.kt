package com.example.simplepasswordkeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.password_fragment.*

class PasswordFragment : DialogFragment () {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.password_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OK_button.setOnClickListener {
            if (password_field.text?.isBlank()?:false)
                Toast.makeText(this.context, "Enter password", Toast.LENGTH_SHORT).show()
            else {
                activity?.let { ViewModelProvider(it, ViewModelProvider.NewInstanceFactory()).get(DataViewModel::class.java)}!!
                    .password.postValue(password_field.text.toString())
            }
        }
    }
}