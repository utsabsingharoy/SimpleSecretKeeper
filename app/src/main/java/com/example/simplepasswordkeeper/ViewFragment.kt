package com.example.simplepasswordkeeper

import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.ui.material.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.view_layout.*

class ViewFragment : Fragment(), AdapterView.OnItemSelectedListener {

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
        return inflater.inflate(R.layout.view_layout, container, false)
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

        floating_delete_button.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Alert")
                .setMessage("Delete ${title_spinner.selectedItem}?")
                .setPositiveButton(R.string.delete) {_, _ ->
                    title_spinner.selectedItem.toString().let {title->
                        (viewEditViewModel.getTitles().indexOf(title)
                            .takeIf {index-> index != viewEditViewModel.getTitles().size - 1}?:0)
                            .also {viewEditViewModel.deleteEntry(title)}
                            .let { nextPos ->
                                populateSpinner()
                                if(nextPos >  -1)
                                    title_spinner.setSelection(nextPos)
                            }
                    }
                }
                .setNegativeButton("Cancel") {_, _ ->  }
                .show()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        data_holder_layout.removeAllViews()

        viewEditViewModel.getDataAt(position).mapIndexed { index, triple ->
            requireActivity().let {
                layoutInflater.inflate(R.layout.outlined_textbox, null).let {
                    it?.findViewById<TextInputLayout>(R.id.ti_layout)?.apply {
                        hint = triple.first
                        tag = "ti_tag" + (index+1).toString()
                        if (triple.third) {
                            //setEndIconDrawable(R.drawable.ic_visibility_black)
                            endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                            setEndIconOnClickListener {
                                this.findViewById<TextInputEditText>(R.id.ti_box).let {
                                    if( it.transformationMethod is PasswordTransformationMethod) {
                                        val func = {
                                            it.transformationMethod =
                                                HideReturnsTransformationMethod.getInstance()
                                            this.setEndIconDrawable(R.drawable.ic_visibility_off_black)
                                            this.isEndIconVisible = true
                                        }
                                        BiometricUnlock.setBiometricUnlock(requireActivity(), func)
                                    }
                                    else {
                                        it.transformationMethod =
                                            PasswordTransformationMethod.getInstance()
                                        this.setEndIconDrawable(R.drawable.ic_visibility_black)
                                        this.isEndIconVisible = true
                                    }
                                }
                            }
                        }
                    }
                    it?.findViewById<TextInputEditText>(R.id.ti_box)?.apply {
                        setText(triple.second)
                        isEnabled = false
                        if(triple.third)
                            inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    }
                    it
                }
            }
        }.forEach {
            data_holder_layout.addView(it)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}