package io.blmswap.blmswap


import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ViewModelScoped
import io.blmswap.blmswap.databinding.ActivityMainBinding
import io.blmswap.blmswap.menuModal.MenuModal
import io.blmswap.blmswap.tokenSelectModal.TokenSelectModal
import io.blmswap.blmswap.viewModels.MainViewModel
import io.blmswap.blmswap.viewModels.TokenSelectViewModel
import io.blmswap.blmswap.walletConnect.WalletConnect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.walletconnect.Session
import org.walletconnect.nullOnThrow
import java.util.regex.Matcher
import java.util.regex.Pattern


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Session.Callback {

    lateinit var mainBinding: ActivityMainBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        initialSetupWalletConnect()

        mainViewModel.firstTokenSelectLiveData.observe(this){
            mainBinding.nameFirstTokenSelect.text = it.symbol
            Glide.with(mainBinding.root.context)
                .load(it.logoURI)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mainBinding.imageFirstToken)
        }

        mainViewModel.secondTokenSelectLiveData.observe(this){
            mainBinding.nameSecondTokenSelect.text = it.symbol
            Glide.with(mainBinding.root.context)
                .load(it.logoURI)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mainBinding.imageSecondToken)
        }



        createGradientText(mainBinding.nameApplication)


        mainBinding.nameFirstTokenSelect.setOnClickListener {
            openSelectModal("FirstToken")
        }


        mainBinding.nameSecondTokenSelect.setOnClickListener {
            openSelectModal("SecondToken")
        }


        mainBinding.imageReverse.setOnClickListener {
            mainViewModel.reverseToken()
        }


        mainBinding.layoutMenu.setOnClickListener {
            openMenuModal()
        }

        mainBinding.textConnectWallet.setOnClickListener {
            WalletConnect.resetSession(this.cacheDir.canonicalPath)
            WalletConnect.session.addCallback(this)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(WalletConnect.config.toWCUri())
            startActivity(i)
        }


    }



    private fun openMenuModal(){
        var modal = MenuModal()
        modal.show(supportFragmentManager,"")
    }


    private fun openSelectModal(numberToken : String){
        var modal = TokenSelectModal(numberToken)
        modal.show(supportFragmentManager,"")
    }


    private fun createGradientText(textView: TextView) {
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, width, textView.textSize, intArrayOf(
                Color.parseColor("#4CC9F0"),
                Color.parseColor("#3A0CA3"),
                Color.parseColor("#F72585"),
            ), null, Shader.TileMode.REPEAT
        )

        textView.paint.shader = textShader
    }


    private fun initialSetupWalletConnect(){
        val session = nullOnThrow { WalletConnect.session } ?: return
        session.addCallback(this)
        sessionApproved()
    }

    private fun sessionApproved() {
        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.setAddressAccount(WalletConnect.session.approvedAccounts()?.get(0).toString())
            Log.e("tag session approved",WalletConnect.session.approvedAccounts()?.get(0).toString())
        }
    }

    override fun onMethodCall(call: Session.MethodCall) {

    }

    override fun onStatus(status: Session.Status) {
        when(status) {
            Session.Status.Approved -> sessionApproved()
            Session.Status.Closed -> sessionClosed()
            Session.Status.Connected,
            Session.Status.Disconnected,
            is Session.Status.Error -> {
                // Do Stuff
            }
        }
    }
    private fun sessionClosed() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("tag session approved","disconnect")
            mainViewModel.disconnectWallet()
        }
    }

}