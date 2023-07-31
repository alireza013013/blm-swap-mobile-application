package io.blmswap.blmswap.tokenSelectModal

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import io.blmswap.blmswap.R
import io.blmswap.blmswap.databinding.TokenSelectModalBinding
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.tokenSelectModal.fragment.TokenFavoriteFragment
import io.blmswap.blmswap.tokenSelectModal.fragment.TokenManageFragment
import io.blmswap.blmswap.tokenSelectModal.fragment.TokenSelectFragment
import io.blmswap.blmswap.viewModels.MainViewModel

class TokenSelectModal(private val numberToken:String) : DialogFragment() {
    lateinit var tokenSelectModalBinding: TokenSelectModalBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tokenSelectModalBinding = DataBindingUtil.inflate(inflater,R.layout.token_select_modal,container,false)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)



        replaceFragment(TokenSelectFragment())






        childFragmentManager.setFragmentResultListener("commandCloseModal",this) { requestKey, bundle ->
           dismiss()
        }
        childFragmentManager.setFragmentResultListener("commandBackSelect",this) { requestKey, bundle ->
            replaceFragment(TokenSelectFragment())
        }
        childFragmentManager.setFragmentResultListener("commandSectionManageToken",this) { requestKey, bundle ->
            replaceFragment(TokenManageFragment())
        }

        childFragmentManager.setFragmentResultListener("commandSelectToken",this) { requestKey, bundle ->
            var token = Gson().fromJson(bundle.getString("token").toString(),TokenEntity::class.java)
            mainViewModel.setTokenSelect(numberToken,token)
            dismiss()
        }


        childFragmentManager.setFragmentResultListener("commandSectionFavorite",this){ requestKey, bundle ->
            replaceFragment(TokenFavoriteFragment())
        }

        return tokenSelectModalBinding.root
    }
    override fun onStart() {
        super.onStart()
        val modal = dialog
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.90).toInt()
        if (modal != null) {
            modal.window!!.setLayout(
                width,
                height
            )
            modal.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = childFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(tokenSelectModalBinding.fragmentContainer.id,fragment)
        fragmentTransaction?.commit()
    }
}