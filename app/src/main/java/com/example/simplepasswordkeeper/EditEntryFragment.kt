package com.example.simplepasswordkeeper

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.simplepasswordkeeper.databinding.EditEntryLayoutBinding
import com.google.android.material.textfield.TextInputEditText

class EditEntryFragment : AddEntryFragment(), AdapterView.OnItemSelectedListener{

    private var editFragmentBinding: EditEntryLayoutBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        editFragmentBinding = EditEntryLayoutBinding.inflate(inflater, container, false)
        return editFragmentBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editFragmentBinding = null
    }

    private fun populateSpinner() {
        viewEditViewModel.getTitles().let {
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }.let {
                editFragmentBinding?.titleSpinner?.adapter = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editFragmentBinding?.titleSpinner?.onItemSelectedListener = this

        addAddButtonListener(editFragmentBinding?.addEntryLayout?.addButtonAddEntry,
            editFragmentBinding?.addEntryLayout?.addEntryLayout)

        editFragmentBinding?.addEntryLayout?.saveButtonAddEntry?.setOnClickListener {
            saveEntry(editFragmentBinding?.addEntryLayout?.addEntryLayout, editFragmentBinding?.addEntryLayout?.titleValue) }

        populateSpinner()
    }
    override fun onSaveButtonClicked(saveData: List<SchemaType>) {
        viewEditViewModel.saveModification(editFragmentBinding?.titleSpinner?.selectedItem.toString(),saveData)
    }

    override fun cleanUpActions() {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        cleanUpView(editFragmentBinding?.addEntryLayout?.addEntryLayout)
        viewEditViewModel.getDataAt(position).mapIndexed { _, triple ->
            if(triple.first == "title") {
                editFragmentBinding?.addEntryLayout?.titleValue?.setText(triple.second)
            } else {
                requireActivity().layoutInflater.inflate(R.layout.single_entry_layout, null).also {
                    it.tag = "add_entry_tag$newItemCount"
                    editFragmentBinding?.addEntryLayout?.addEntryLayout?.addView(it)
                    newItemCount ++
                    it.findViewById<TextInputEditText>(R.id.entry_key)!!.setText(triple.first)
                    it.findViewById<TextInputEditText>(R.id.entry_value)!!.apply {
                        setText(triple.second)
                        if(triple.third)
                            transformationMethod = PasswordTransformationMethod.getInstance()
                    }
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    init {
        editMode = true
    }
}