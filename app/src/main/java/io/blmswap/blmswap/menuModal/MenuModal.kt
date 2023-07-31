package io.blmswap.blmswap.menuModal

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import io.blmswap.blmswap.R
import io.blmswap.blmswap.databinding.MenuModalBinding
import io.blmswap.blmswap.menuModal.fragment.AccountFragment
import io.blmswap.blmswap.menuModal.fragment.GeneralSettingFragment
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.tokenSelectModal.fragment.TokenFavoriteFragment
import io.blmswap.blmswap.tokenSelectModal.fragment.TokenManageFragment
import io.blmswap.blmswap.tokenSelectModal.fragment.TokenSelectFragment
import io.blmswap.blmswap.viewModels.MainViewModel

class MenuModal:DialogFragment() {
    lateinit var menuModalBinding: MenuModalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuModalBinding = DataBindingUtil.inflate(inflater,
            R.layout.menu_modal,container,false)




        replaceFragment(GeneralSettingFragment())

        childFragmentManager.setFragmentResultListener("commandCloseModal",this) { requestKey, bundle ->
            dismiss()
        }

        childFragmentManager.setFragmentResultListener("commandOpenAccount",this) { requestKey, bundle ->
            replaceFragment(AccountFragment())
        }



        return menuModalBinding.root
    }
    override fun onStart() {
        super.onStart()
        val modal = dialog
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
//        val height = (resources.displayMetrics.heightPixels * 0.90).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        if (modal != null) {
            modal.window!!.setLayout(
                width,
                height
            )
            modal.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            modal.window!!.setGravity(Gravity.TOP)
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = childFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(menuModalBinding.fragmentContainerMenu.id,fragment)
        fragmentTransaction?.commit()
    }
}
