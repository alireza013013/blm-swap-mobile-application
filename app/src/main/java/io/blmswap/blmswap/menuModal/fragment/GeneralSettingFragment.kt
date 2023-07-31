package io.blmswap.blmswap.menuModal.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import io.blmswap.blmswap.R
import io.blmswap.blmswap.adapters.BlockChainAdapter
import io.blmswap.blmswap.adapters.TokenAdapter
import io.blmswap.blmswap.databinding.GeneralSettingFragmentBinding
import io.blmswap.blmswap.viewModels.MainViewModel
import io.blmswap.blmswap.viewModels.TokenSelectViewModel

class GeneralSettingFragment:Fragment(R.layout.general_setting_fragment) {

    lateinit var generalSettingFragmentBinding: GeneralSettingFragmentBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        generalSettingFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.general_setting_fragment,container,false)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        setupBlurBackGround()

        generalSettingFragmentBinding.buttonClose.setOnClickListener {
            var result = Bundle()
            parentFragmentManager.setFragmentResult("commandCloseModal",result)
        }

        generalSettingFragmentBinding.addressWallet.setOnClickListener {
            var result = Bundle()
            parentFragmentManager.setFragmentResult("commandOpenAccount",result)
        }

        mainViewModel.addressWalletLiveData.observe(viewLifecycleOwner){
            if (it !=null){
                generalSettingFragmentBinding.imageChainWallet.visibility = View.VISIBLE
                generalSettingFragmentBinding.addressWallet.visibility = View.VISIBLE
                generalSettingFragmentBinding.addressWallet.text = it.take(10)+"..."+it.takeLast(8)
            }else{
                generalSettingFragmentBinding.imageChainWallet.visibility = View.GONE
                generalSettingFragmentBinding.addressWallet.visibility = View.GONE
            }
        }


        return generalSettingFragmentBinding.root
    }

    private fun setupBlurBackGround(){
        val blurView: BlurView = generalSettingFragmentBinding.blurView
        val decorView = activity?.window?.decorView
        val rootView = decorView?.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView?.background
        rootView?.let {
            blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
            blurView.clipToOutline = true
            blurView.setupWith(it)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(activity))
                .setBlurRadius(23f)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(false)
        }
    }
}