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
import com.example.simplepasswordkeeper.databinding.BaseFragmentBinding
class BaseFragment : Fragment() {
    private var baseFragmentBinding: BaseFragmentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        baseFragmentBinding = BaseFragmentBinding.inflate(inflater, container, false)
        return baseFragmentBinding?.root
        //return inflater.inflate(R.layout.base_fragment, container, false) <<- Delete it
    }

    override fun onDestroyView() {
        super.onDestroyView()
        baseFragmentBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseFragmentBinding?.viewEditButton?.setOnClickListener {

            ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application))[DataViewModel::class.java].let { model ->
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
                            .replace(R.id.main_fragment_container, ViewEditBaseFragment())
                            .addToBackStack("ViewEditBaseFragment").commit()
                    })
                }
            (activity as MainActivity).sendFileActionIntentOpenDocument()
        }
        baseFragmentBinding?.newFileButton?.setOnClickListener {
            Toast.makeText(requireContext(), "Not Implemented", Toast.LENGTH_SHORT).show()
        }
        baseFragmentBinding?.changePassword?.setOnClickListener {
            Toast.makeText(requireContext(), "Not Implemented", Toast.LENGTH_SHORT).show()
        }
    }
}
