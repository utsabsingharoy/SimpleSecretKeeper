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
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.password_fragment.view.*
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

        floating_save_button.setOnClickListener {
            title_spinner.selectedItem.toString().let { current_element  ->
                (1..viewEditViewModel.getEntryCount(current_element)).map {
                    data_holder_layout.findViewWithTag<TextInputLayout>("ti_tag$it")!!.let {
                        Triple(
                            it.hint.toString(),
                            it.findViewById<TextInputEditText>(R.id.ti_box)?.text.toString(),
                            it.endIconMode == TextInputLayout.END_ICON_PASSWORD_TOGGLE
                        )
                    }
                }.let {
                    viewEditViewModel.saveModification(current_element, it)
                }
            }

            /*if(dataViewModel.decryptedResult.value!! == dataViewModel.decryptedData.toPythonString())
                Log.e("TAG", "Same value")
            else
                Log.e("TAG", title_spinner.selectedItem.toString())*/
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
                                            setBiometricUnlock(func)
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

    private fun setBiometricUnlock(successCallback : ()->Unit) {
        if (BiometricManager.from(requireContext()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            requireActivity().applicationContext.let { applicationContext ->
                BiometricPrompt(this, requireActivity().mainExecutor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(applicationContext,
                                "Authentication error: $errString", Toast.LENGTH_SHORT)
                                .show()
                        }
                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult
                        ) {
                            super.onAuthenticationSucceeded(result)
                            successCallback()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(applicationContext,
                                "Authentication failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                ).authenticate(
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Biometric login to view field")
                        .setSubtitle("Log in using your biometric credential")
                        .setNegativeButtonText("Cancel")
                        .build()
                )
            }
        } else
            successCallback()
    }
}