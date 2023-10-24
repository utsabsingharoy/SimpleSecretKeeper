package com.example.simplepasswordkeeper

import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.simplepasswordkeeper.databinding.ViewLayoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ViewFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var  viewEditViewModel : ViewEditViewModel
    private var viewLayoutBinding: ViewLayoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewEditViewModel = ViewModelProvider(parentFragment as ViewModelStoreOwner,
            ViewModelProvider.NewInstanceFactory())[ViewEditViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewLayoutBinding = ViewLayoutBinding.inflate(inflater, container, false)
        return viewLayoutBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLayoutBinding = null
    }

    private fun populateSpinner() {
        viewEditViewModel.getTitles().let { it ->
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }.let {
                viewLayoutBinding?.titleSpinner?.adapter = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLayoutBinding?.titleSpinner?.onItemSelectedListener = this
        populateSpinner()

        viewLayoutBinding?.floatingDeleteButton?.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Alert")
                .setMessage("Delete ${ viewLayoutBinding?.titleSpinner?.selectedItem}?")
                .setPositiveButton(R.string.delete) {_, _ ->
                    viewLayoutBinding?.titleSpinner?.selectedItem.toString().let {title->
                        (viewEditViewModel.getTitles().indexOf(title)
                            .takeIf {index-> index != viewEditViewModel.getTitles().size - 1}?:0)
                            .also {viewEditViewModel.deleteEntry(title)}
                            .let { nextPos ->
                                populateSpinner()
                                if(nextPos >  -1)
                                    viewLayoutBinding?.titleSpinner?.setSelection(nextPos)
                            }
                    }
                }
                .setNegativeButton("Cancel") {_, _ ->  }
                .show()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewLayoutBinding?.dataHolderLayout?.removeAllViews()

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
            viewLayoutBinding?.dataHolderLayout?.addView(it)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}