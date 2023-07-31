package io.blmswap.blmswap.tokenSelectModal.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.blmswap.blmswap.R
import io.blmswap.blmswap.adapters.BlockChainAdapter
import io.blmswap.blmswap.adapters.CardClickListener
import io.blmswap.blmswap.adapters.TokenAdapter
import io.blmswap.blmswap.databinding.TokenSelectFragmentBinding
import io.blmswap.blmswap.models.BlockChain
import io.blmswap.blmswap.models.Token
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.viewModels.TokenSelectViewModel



class TokenSelectFragment:Fragment(R.layout.token_select_fragment),CardClickListener {

    lateinit var tokenSelectFragmentBinding: TokenSelectFragmentBinding
    lateinit var tokenSelectViewModel: TokenSelectViewModel
    lateinit var blockChainAdapter : BlockChainAdapter
    lateinit var tokenAdapter: TokenAdapter
    var oldTokenListSize = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tokenSelectFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.token_select_fragment,container,false)
        tokenSelectViewModel = ViewModelProvider(requireActivity()).get(TokenSelectViewModel::class.java)





        tokenSelectFragmentBinding.buttonCloseModal.setOnClickListener {
            var result = Bundle()
            parentFragmentManager.setFragmentResult("commandCloseModal",result)
        }

        tokenSelectFragmentBinding.buttonFavorite.setOnClickListener {
            var result = Bundle()
            parentFragmentManager.setFragmentResult("commandSectionFavorite", result)
        }

        tokenSelectFragmentBinding.buttonManageToken.setOnClickListener {
            var result = Bundle()
            parentFragmentManager.setFragmentResult("commandSectionManageToken", result)
        }

        tokenSelectViewModel.blockChainSelectedListLiveData.observe(viewLifecycleOwner){
            tokenSelectViewModel.filterTokenByBlockChainSelected(it)
        }

        tokenSelectFragmentBinding.arrowBackRecycle.setOnClickListener {
            scrollBackRecycleView()
        }

        tokenSelectFragmentBinding.arrowNextRecycle.setOnClickListener {
            scrollForwardRecycleView()
        }

        tokenSelectViewModel.blockChainLiveData.observe(viewLifecycleOwner){ BlockChains ->
            if (BlockChains != null){
                blockChainAdapter = BlockChainAdapter(BlockChains,this)
                tokenSelectFragmentBinding.recycleViewBlockChain.layoutManager = StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.HORIZONTAL)
                tokenSelectFragmentBinding.recycleViewBlockChain.adapter = blockChainAdapter
            }
        }


        tokenSelectViewModel.getInformationFromApi()



        tokenSelectViewModel.tokenListWithTokensLiveData.observe(viewLifecycleOwner){
            if (it != null){
                tokenSelectViewModel.setInformationToken()
            }
        }

        tokenSelectViewModel.tokenListLivaData.observe(viewLifecycleOwner){ Tokens ->
            if (Tokens != null){
                if (oldTokenListSize != Tokens.size){
                    tokenAdapter = TokenAdapter(Tokens, this)
                    tokenSelectFragmentBinding.recycleViewToken.layoutManager = StaggeredGridLayoutManager(1,
                        StaggeredGridLayoutManager.VERTICAL)
                    tokenSelectFragmentBinding.recycleViewToken.adapter = tokenAdapter
                    oldTokenListSize = Tokens.size
                }
            }
        }


        setupSearchBox()


        return tokenSelectFragmentBinding.root
    }


    override fun clickOnCardBlockChain(blockChain: BlockChain,position: Int){
        tokenSelectViewModel.changeStatusChain(blockChain)
        blockChainAdapter.notifyItemChanged(position)
    }

    override fun clickChangeStatusToken(token: TokenEntity, position: Int) {
        tokenSelectViewModel.changeStatusToken(token)
        tokenAdapter.notifyItemChanged(position)
    }

    override fun clickOnCardTokenForSelect(token: TokenEntity) {
        var result = Bundle()
        result.putString("token",Gson().toJson(token))
        parentFragmentManager.setFragmentResult("commandSelectToken",result)
    }

    private fun scrollBackRecycleView(){
        tokenSelectFragmentBinding.recycleViewBlockChain.smoothScrollBy(-50,0)
    }

    private fun scrollForwardRecycleView(){
        tokenSelectFragmentBinding.recycleViewBlockChain.smoothScrollBy(50,0)
    }

    private fun setupSearchBox() {
        tokenSelectFragmentBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                filterToken(p0.toString())
            }

        })
    }

    private fun filterToken(searchText: String) {
        tokenSelectViewModel.filterToken(searchText)
    }

}