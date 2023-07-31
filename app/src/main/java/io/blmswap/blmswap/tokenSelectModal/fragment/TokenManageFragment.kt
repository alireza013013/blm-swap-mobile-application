package io.blmswap.blmswap.tokenSelectModal.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.blmswap.blmswap.R
import io.blmswap.blmswap.adapters.BlockChainAdapter
import io.blmswap.blmswap.adapters.TokenAdapter
import io.blmswap.blmswap.adapters.TokenListAdapter
import io.blmswap.blmswap.adapters.TokenListClickListener
import io.blmswap.blmswap.databinding.TokenManageFragmentBinding
import io.blmswap.blmswap.databinding.TokenSelectFragmentBinding
import io.blmswap.blmswap.room.entities.TokenList
import io.blmswap.blmswap.viewModels.TokenManageViewModel
import io.blmswap.blmswap.viewModels.TokenSelectViewModel

class TokenManageFragment: Fragment(R.layout.token_manage_fragment),TokenListClickListener {

    lateinit var tokenManageFragmentBinding: TokenManageFragmentBinding
    lateinit var tokenManageViewModel: TokenManageViewModel
    lateinit var tokenListAdapter: TokenListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tokenManageFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.token_manage_fragment,container,false)
        tokenManageViewModel = ViewModelProvider(requireActivity()).get(TokenManageViewModel::class.java)


        tokenManageFragmentBinding.buttonBack.setOnClickListener {
            var result = Bundle()
            parentFragmentManager.setFragmentResult("commandBackSelect", result)
        }


        setupDownloadList()


        tokenManageViewModel.getTokenListFromDatabase()
        tokenManageViewModel.deleteInformationDownloadFromMemory()

        tokenManageViewModel.tokenListLiveData.observe(viewLifecycleOwner){
            if (it != null){
                tokenListAdapter = TokenListAdapter(it,this)
                tokenManageFragmentBinding.recycleViewTokenList.layoutManager = StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL)
                tokenManageFragmentBinding.recycleViewTokenList.adapter = tokenListAdapter
            }
        }

        tokenManageFragmentBinding.buttonImport.setOnClickListener {
            tokenManageViewModel.insetNewTokenListToDatabase()
            tokenManageFragmentBinding.mainConstraintListImport.visibility = View.GONE
        }

        tokenManageViewModel.tokenListDownloadLiveData.observe(viewLifecycleOwner){
            if (it != null){
                Log.e("Tag",it.toString())
                tokenManageFragmentBinding.mainConstraintListImport.visibility = View.VISIBLE
                Glide.with(tokenManageFragmentBinding.root.context)
                    .load(it.logoURI)
                    .thumbnail(Glide.with(tokenManageFragmentBinding.root.context).load(R.drawable.ic_circle_question))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(tokenManageFragmentBinding.imageListToken)
                tokenManageFragmentBinding.nameTokenList.text = it.name
                tokenManageFragmentBinding.textCountToken.text = it.countToken.toString() + "Token"
                if (it.isImported){
                    tokenManageFragmentBinding.buttonImported.visibility = View.VISIBLE
                }else{
                    tokenManageFragmentBinding.buttonImport.visibility = View.VISIBLE
                }
            }else{
                tokenManageFragmentBinding.mainConstraintListImport.visibility = View.GONE
            }
        }



        return tokenManageFragmentBinding.root
    }

    override fun changeStatusVisibilityListToken(tokenList: TokenList, position: Int) {
        tokenManageViewModel.changeVisibilityTokenList(tokenList)
    }

    override fun deleteListTokenFromDatabase(tokenList: TokenList) {
        tokenManageViewModel.deleteTokenListFromDatabase(tokenList)
    }

    private fun setupDownloadList(){
        tokenManageFragmentBinding.editTextDownloadList.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                tokenManageViewModel.validationURL(p0.toString())
            }
        })
    }


}