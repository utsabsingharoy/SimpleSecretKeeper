package com.example.simplepasswordkeeper

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.textfield.TextInputEditText
import com.example.simplepasswordkeeper.databinding.AddEntryFragmentBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class AddEntryFragment : Fragment(), ISavableActions{

    protected var newItemCount = 0
    protected lateinit var  viewEditViewModel : ViewEditViewModel
    protected var editMode = false
    private var addEntryBinding: AddEntryFragmentBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewEditViewModel =
            ViewModelProvider(parentFragment as ViewModelStoreOwner, ViewModelProvider.NewInstanceFactory())[ViewEditViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        addEntryBinding = AddEntryFragmentBinding.inflate(inflater, container, false)
        return addEntryBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addEntryBinding = null
    }

    protected fun addAddButtonListener(addButton: FloatingActionButton?, layout: LinearLayout?) {
       addButton?.setOnClickListener {
            requireActivity().layoutInflater.inflate(R.layout.single_entry_layout, null).also {
                it.tag = "add_entry_tag$newItemCount"
                layout?.addView(it)
                newItemCount ++
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*addEntryBinding?.addButtonAddEntry?.setOnClickListener {
            requireActivity().layoutInflater.inflate(R.layout.single_entry_layout, null).also {
                it.tag = "add_entry_tag$newItemCount"
                addEntryBinding?.addEntryLayout?.addView(it)
                newItemCount ++
            }
        }*/
        addAddButtonListener(addEntryBinding?.addButtonAddEntry, addEntryBinding?.addEntryLayout)
        addEntryBinding?.saveButtonAddEntry?.setOnClickListener {
            saveEntry(addEntryBinding?.addEntryLayout, addEntryBinding?.titleValue) }
    }

    protected fun saveEntry(dataLayout: LinearLayout?, titleUi : TextInputEditText?) {
        if(newItemCount == 0)
            return

        listOf(
            Pair({  titleUi?.text.toString()?.isBlank()!! }, "Title can't be Blank"),
            Pair({ !editMode && viewEditViewModel.getTitles().contains( titleUi!!.text.toString()) },
                "Duplicate Title")
        ).firstOrNull { it.first() }?.run {
            Toast.makeText(requireContext(), this.second, Toast.LENGTH_SHORT).show()
            return
        }

        (0 until newItemCount).map {
            dataLayout?.findViewWithTag<LinearLayout>("add_entry_tag$it")!!.let {
                val key =
                    it.findViewById<TextInputEditText>(R.id.entry_key)!!.text.toString()
                val entryValue = it.findViewById<TextInputEditText>(R.id.entry_value)
                val value =
                    entryValue!!.text.toString()
                val hidden =
                    entryValue.transformationMethod is PasswordTransformationMethod
                Triple(key, value, hidden)
            }
        }.takeIf { list -> list.none {
            it.first.isBlank() && it.second.isNotBlank()} }?.apply {
            BiometricUnlock.setBiometricUnlock(requireActivity()) {
                (listOf(Triple("title",  titleUi?.text.toString(), false)) + this).filter {
                    !(it.first.isBlank() && it.second.isBlank())
                }.let {
                    onSaveButtonClicked(it)
                }.also {
                    cleanUpActions()
                }
            }
        }?:run{
            Toast.makeText(requireContext(), "Keys can't be Blank", Toast.LENGTH_SHORT).show()
        }
    }

    protected fun cleanUpView(linearLayout: LinearLayout?) {
        (0 until newItemCount).map {
            "add_entry_tag$it"
        }.mapNotNull {
            linearLayout?.findViewWithTag<LinearLayout>(it)
        }.forEach {
            linearLayout?.removeView(it)
        }
        addEntryBinding?.titleValue?.setText("")
        newItemCount = 0
    }
    override fun cleanUpActions() {
        cleanUpView(addEntryBinding?.addEntryLayout)
    }

    override fun onSaveButtonClicked(saveData : List<SchemaType>) {
        viewEditViewModel.addAndSaveNewEntry(saveData)
    }
}