package com.example.simplepasswordkeeper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplepasswordkeeper.databinding.BaseLayoutWithBottomBinding

class ViewEditBaseFragment : Fragment() {

    private lateinit var viewModel: ViewEditViewModel
    private var baseLayoutBottomBinding: BaseLayoutWithBottomBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ViewEditViewModel::class.java]
        viewModel.persistentStorage = ViewModelProvider(requireActivity(),
            ViewModelProvider.NewInstanceFactory())[DataViewModel::class.java].storageAccess
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        baseLayoutBottomBinding = BaseLayoutWithBottomBinding.inflate(inflater, container, false)
        return baseLayoutBottomBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        baseLayoutBottomBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseLayoutBottomBinding?.bottomBar?.setOnNavigationItemSelectedListener {
            /*run {
                when (it.itemId) {
                    R.id.bottom_view -> ViewFragment()
                    R.id.bottom_add -> AddEntryFragment()
                    R.id.bottom_delete ->
                    else -> Fragment()
                }
            }.let {
                childFragmentManager.beginTransaction().replace(R.id.view_edit_fragment_container, it).commit()
            }*/
            run {
                when (it.itemId) {
                    R.id.bottom_view -> childFragmentManager.beginTransaction().replace(R.id.view_edit_fragment_container, ViewFragment()).commit()
                    R.id.bottom_add -> childFragmentManager.beginTransaction().replace(R.id.view_edit_fragment_container, AddEntryFragment()).commit()
                    R.id.bottom_delete -> {
                        BiometricUnlock.setBiometricUnlock(requireActivity()){
                            childFragmentManager.beginTransaction().replace(R.id.view_edit_fragment_container, EditEntryFragment()).commit()
                        }
                    }
                    else -> Fragment()
                }
            }
            true
        }
    }
}