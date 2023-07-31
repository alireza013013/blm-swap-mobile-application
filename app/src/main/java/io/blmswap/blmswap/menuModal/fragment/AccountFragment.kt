package io.blmswap.blmswap.menuModal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.blmswap.blmswap.R
import io.blmswap.blmswap.databinding.AccountFragmentBinding
import io.blmswap.blmswap.viewModels.MainViewModel

class AccountFragment:Fragment(R.layout.account_fragment) {

    lateinit var accountFragmentBinding : AccountFragmentBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.account_fragment,container,false)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)



        mainViewModel.addressWalletLiveData.observe(viewLifecycleOwner){
            if (it !=null){

            }
        }


        return accountFragmentBinding.root
    }
}