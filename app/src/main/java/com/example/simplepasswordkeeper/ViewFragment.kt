package com.example.simplepasswordkeeper


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.view_layout.*

class ViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.view_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory()).get(DataViewModel::class.java)
        display_text.setText(
            model.decryptedResult.value
        )

        floating_save_button.setOnClickListener {
            model.modifiedData.postValue(display_text.text.toString())
        }
    }
}