package com.example.simplepasswordkeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        view_edit_button.setOnClickListener {

            ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
                .get(DataViewModel::class.java).let {model ->
                    val supportFragmentManager = requireActivity().supportFragmentManager

                    model.filepath.observe(this, Observer {
                        PasswordFragment().show(
                            supportFragmentManager.beginTransaction(),
                            "PasswordFragment"
                        )
                    })

                    model.password.observe(this, Observer {
                        supportFragmentManager.findFragmentByTag("PasswordFragment")?.let { dialogFragment ->
                            supportFragmentManager.beginTransaction().remove(dialogFragment).commit()
                            (dialogFragment as DialogFragment).dismiss()
                        }
                    })

                    model.decryptedResultReady.observe(this, Observer {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_container, ViewEditBaseFragment(model.decryptedString))
                            .addToBackStack("ViewEditBaseFragment").commit()
                    })
                }
            (activity as MainActivity).let {
                it.sendFileActionIntentOpenDocument()
            }
        }
        new_file_button.setOnClickListener {
            Toast.makeText(requireContext(), "Not Implemented", Toast.LENGTH_SHORT).show()
        }
        change_password.setOnClickListener {
            Toast.makeText(requireContext(), "Not Implemented", Toast.LENGTH_SHORT).show()
        }
    }
}