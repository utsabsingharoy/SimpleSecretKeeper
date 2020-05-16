package com.example.simplepasswordkeeper

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
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

        listOf (
            Pair({title_value.text.toString().isBlank()}, "Title can't be Blank") ,
            Pair({ viewEditViewModel.getTitles().contains(title_value.text.toString())},"Duplicate Title")
        ).firstOrNull{it.first()}?.run {
            Toast.makeText(requireContext(), this.second, Toast.LENGTH_SHORT).show()
            return
        }

        (0 until newItemCount).map {
            add_entry_layout.findViewWithTag<LinearLayout>("add_entry_tag$it")!!.let {
                val key =
                    it.findViewById<TextInputEditText>(R.id.entry_key)!!.text.toString()
                val value =
                    it.findViewById<TextInputEditText>(R.id.entry_value)!!.text.toString()
                val hidden =
                    it.findViewById<TextInputEditText>(R.id.entry_value)!!.transformationMethod is PasswordTransformationMethod
                Triple(key, value, hidden)
            }
        }.takeIf { list -> list.none {
            it.first.isBlank() && !it.second.isBlank()} }?.apply { this

            (listOf(Triple("title", title_value.text.toString(), false)) + this).let {
                viewEditViewModel.addAndSaveNewEntry(it)
            }.also {
                (0 until newItemCount).map {
                    "add_entry_tag$it"
                }.mapNotNull {
                    add_entry_layout.findViewWithTag<LinearLayout>(it)
                }.forEach {
                    add_entry_layout.removeView(it)
                }
                title_value.setText("")
                newItemCount = 0
            }

        }?:run{
            Toast.makeText(requireContext(), "Keys can't be Blank", Toast.LENGTH_SHORT).show()
        }
    }
}