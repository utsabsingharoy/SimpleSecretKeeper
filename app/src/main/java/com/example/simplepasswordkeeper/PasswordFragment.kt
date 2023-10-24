package com.example.simplepasswordkeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplepasswordkeeper.databinding.PasswordFragmentBinding
class PasswordFragment : DialogFragment () {

    private var passwordFragmentBinding: PasswordFragmentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        passwordFragmentBinding = PasswordFragmentBinding.inflate(inflater, container, false)
        return passwordFragmentBinding?.root
        //return inflater.inflate(R.layout.password_fragment, container, false) <<--Delete this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        passwordFragmentBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passwordFragmentBinding?.OKButton?.setOnClickListener {
            if (passwordFragmentBinding?.passwordField?.text?.isBlank() == true)
                Toast.makeText(this.context, "Enter password", Toast.LENGTH_SHORT).show()
            else {
                activity?.let {
                    ViewModelProvider(it, ViewModelProvider.NewInstanceFactory())[DataViewModel::class.java] }!!
                    .password.postValue(passwordFragmentBinding?.passwordField?.text.toString())
            }
        }
    }
}