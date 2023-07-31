package io.blmswap.blmswap.tokenSelectModal.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import io.blmswap.blmswap.R
import io.blmswap.blmswap.adapters.BlockChainAdapter
import io.blmswap.blmswap.adapters.CardClickListener
import io.blmswap.blmswap.adapters.TokenAdapter
import io.blmswap.blmswap.databinding.TokenFavoriteFragmentBinding
import io.blmswap.blmswap.models.BlockChain
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.viewModels.TokenFavoriteViewModel

class TokenFavoriteFragment:Fragment(R.layout.token_favorite_fragment), CardClickListener {


    lateinit var tokenFavoriteFragmentBinding: TokenFavoriteFragmentBinding
    lateinit var tokenFavoriteViewModel: TokenFavoriteViewModel
    lateinit var blockChainAdapter : BlockChainAdapter
    lateinit var tokenAdapter: TokenAdapter




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tokenFavoriteFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.token_favorite_fragment,container,false)
        tokenFavoriteViewModel = ViewModelProvider(requireActivity()).get(TokenFavoriteViewModel::class.java)

        tokenFavoriteFragmentBinding.buttonCloseModal.setOnClickListener {
            var result = Bundle()
            parentFragmentManager.setFragmentResult("commandCloseModal",result)
        }

        tokenFavoriteFragmentBinding.buttonBack.setOnClickListener {
            var result = Bundle()
            parentFragmentManager.setFragmentResult("commandBackSelect", result)
        }



        tokenFavoriteViewModel.blockChainSelectedListLiveData.observe(viewLifecycleOwner){
            tokenFavoriteViewModel.filterTokenByBlockChainSelected(it)
        }


        tokenFavoriteViewModel.getAllTokenFavorite()

        tokenFavoriteFragmentBinding.arrowBackRecycle.setOnClickListener {
            scrollBackRecycleView()
        }

        tokenFavoriteFragmentBinding.arrowNextRecycle.setOnClickListener {
            scrollForwardRecycleView()
        }

        tokenFavoriteViewModel.blockChainLiveData.observe(viewLifecycleOwner){ BlockChains ->
            if (BlockChains != null){
                blockChainAdapter = BlockChainAdapter(BlockChains,this)
                tokenFavoriteFragmentBinding.recycleViewBlockChain.layoutManager = StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.HORIZONTAL)
                tokenFavoriteFragmentBinding.recycleViewBlockChain.adapter = blockChainAdapter
            }
        }



        tokenFavoriteViewModel.tokenListLiveDataForShow.observe(viewLifecycleOwner){ Tokens ->
            if (Tokens != null){
                tokenAdapter = TokenAdapter(Tokens, this)
                tokenFavoriteFragmentBinding.recycleViewToken.layoutManager = StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL)
                tokenFavoriteFragmentBinding.recycleViewToken.adapter = tokenAdapter
            }
        }

        setupSearchBox()


        return tokenFavoriteFragmentBinding.root
    }


    private fun scrollBackRecycleView(){
        tokenFavoriteFragmentBinding.recycleViewBlockChain.smoothScrollBy(-50,0)
    }

    private fun scrollForwardRecycleView(){
        tokenFavoriteFragmentBinding.recycleViewBlockChain.smoothScrollBy(50,0)
    }




    override fun clickOnCardBlockChain(blockChain: BlockChain, position: Int) {
        tokenFavoriteViewModel.changeStatusChain(blockChain)
        blockChainAdapter.notifyItemChanged(position)
    }

    override fun clickChangeStatusToken(token: TokenEntity, position: Int) {
        tokenFavoriteViewModel.changeStatusToken(token)
        tokenAdapter.notifyItemChanged(position)
    }

    override fun clickOnCardTokenForSelect(token: TokenEntity) {
        var result = Bundle()
        result.putString("token", Gson().toJson(token))
        parentFragmentManager.setFragmentResult("commandSelectToken",result)
    }

    private fun setupSearchBox() {
        tokenFavoriteFragmentBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
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
        tokenFavoriteViewModel.filterToken(searchText)
    }

}