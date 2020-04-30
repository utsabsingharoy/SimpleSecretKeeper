package com.example.simplepasswordkeeper

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.base_fragment.*

class BaseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.base_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        decrypt_button.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.sendFileActionIntent(
                Intent.ACTION_OPEN_DOCUMENT,
                MainActivity.DECRYPT_RESULT
            )
        }
        encrypt_button.setOnClickListener {
            Toast.makeText(requireContext(), "Not Implemented", Toast.LENGTH_SHORT).show()
        }
        change_password.setOnClickListener {
            Toast.makeText(requireContext(), "Not Implemented", Toast.LENGTH_SHORT).show()
        }
    }
}