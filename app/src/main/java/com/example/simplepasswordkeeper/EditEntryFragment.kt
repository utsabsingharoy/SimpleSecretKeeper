package com.example.simplepasswordkeeper

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.add_entry_fragment.*
import kotlinx.android.synthetic.main.edit_entry_layout.title_spinner

class EditEntryFragment : AddEntryFragment(), AdapterView.OnItemSelectedListener{

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.edit_entry_layout, container, false)
    }

    private fun populateSpinner() {
        viewEditViewModel.getTitles().let {
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }.let {
                title_spinner.adapter = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title_spinner.onItemSelectedListener = this
        populateSpinner()
    }
    override fun onSaveButtonClicked(saveData: List<SchemaType>) {
        viewEditViewModel.saveModification(title_spinner.selectedItem.toString(),saveData)
    }

    override fun cleanUpActions() {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        cleanUpView()
        viewEditViewModel.getDataAt(position).mapIndexed { index, triple ->
            if(triple.first == "title") {
                title_value.setText(triple.second)
            } else {
                requireActivity().layoutInflater.inflate(R.layout.single_entry_layout, null).also {
                    it.tag = "add_entry_tag$newItemCount"
                    add_entry_layout.addView(it)
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