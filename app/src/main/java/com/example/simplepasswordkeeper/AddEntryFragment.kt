package com.example.simplepasswordkeeper

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.add_entry_fragment.*

class AddEntryFragment : Fragment() {

    private var newItemCount = 0
    private lateinit var  viewEditViewModel : ViewEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewEditViewModel = ViewModelProvider(parentFragment as ViewModelStoreOwner, ViewModelProvider.NewInstanceFactory()).get(ViewEditViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        //ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
        //    .get(DataViewModel::class.java).backPressed.observe(requireActivity(), Observer {  backPressedAction()})
        return inflater.inflate(R.layout.add_entry_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_button_add_entry.setOnClickListener {
            requireActivity().layoutInflater.inflate(R.layout.single_entry_layout, null).also {
                it.tag = "add_entry_tag$newItemCount"
                add_entry_layout.addView(it)
                newItemCount ++
            }
        }
        save_button_add_entry.setOnClickListener { saveEntry() }
    }

    private fun saveEntry() {
        if(newItemCount == 0)
            return
        if(title_value.text.toString().isBlank())
            return
        val list : MutableList<Triple<String,String,Boolean>> =
        mutableListOf(Triple("title", title_value.text.toString(), false )).let {
            it.addAll(
                    (0 until newItemCount - 1).map {
                        add_entry_layout.findViewWithTag<LinearLayout>("add_entry_tag$it")!!.let {
                            val key =
                                it.findViewById<TextInputEditText>(R.id.entry_key)!!.text.toString()
                            val value =
                                it.findViewById<TextInputEditText>(R.id.entry_value)!!.text.toString()
                            val hidden =
                                it.findViewById<TextInputEditText>(R.id.entry_value)!!.transformationMethod is PasswordTransformationMethod

                            Triple(key, value, hidden)
                        }
                    }
            )
            it
        }

        Log.e("TAG1", list.toString())

    }
}