package com.example.simplepasswordkeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.base_layout_with_bottom.*

class ViewEditBaseFragment(var decryptedString : String) : Fragment() {

    private lateinit var viewModel: ViewEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ViewEditViewModel::class.java)
        viewModel.decryptedData = DecryptedData(decryptedString)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.base_layout_with_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottom_bar.setOnNavigationItemSelectedListener {
            run {
                when (it.itemId) {
                    R.id.bottom_view -> ViewFragment()
                    R.id.bottom_add -> AddEntryFragment()
                    else -> Fragment()
                }
            }.let {
                childFragmentManager.beginTransaction().replace(R.id.view_edit_fragment_container, it).commit()
            }
            true
        }
    }
}