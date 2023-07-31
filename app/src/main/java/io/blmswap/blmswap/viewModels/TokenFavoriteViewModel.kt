package io.blmswap.blmswap.viewModels

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.blmswap.blmswap.R
import io.blmswap.blmswap.models.BlockChain
import io.blmswap.blmswap.models.Token
import io.blmswap.blmswap.repository.Repository
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.room.entities.TokenList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TokenFavoriteViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private var _blockChainLiveData = MutableLiveData<List<BlockChain>>()
    val blockChainLiveData : LiveData<List<BlockChain>>
        get() = _blockChainLiveData

    private var _blockChainSelectedListLiveData = MutableLiveData<List<BlockChain>>()
    val blockChainSelectedListLiveData : LiveData<List<BlockChain>>
        get() = _blockChainSelectedListLiveData


    private var _responseTokenListLiveData = MutableLiveData<MutableList<TokenList>>()
    val  responseTokenListLivaData : LiveData<MutableList<TokenList>>
        get() = _responseTokenListLiveData

    private var _tokenListLiveData = MutableLiveData<List<TokenEntity>>()
    val tokenListLivaData : LiveData<List<TokenEntity>>
        get() = _tokenListLiveData

    private var _tokenListLiveDataForShow = MutableLiveData<List<TokenEntity>>()
    val tokenListLiveDataForShow : LiveData<List<TokenEntity>>
        get() = _tokenListLiveDataForShow



    init {
        setInformationBlockChain()
    }


    private fun setInformationBlockChain(){
        val bnbBlockChain = BlockChain("Binance SmartChain",56, R.drawable.ic_bnb,"BNB","https://bscscan.com","BINANCE_SMART_CHAIN")
        val ethereumBlockChain = BlockChain("Ethereum",1, R.drawable.ic_eth_contrast,"ETH","https://etherscan.io","ETHEREUM")
        val polygonBlockChain = BlockChain("Polygon",137, R.drawable.ic_polygon,"MATIC","https://polygonscan.com","POLYGON")
        val arbitrumBlockChain = BlockChain("Arbitrum One",42161, R.drawable.ic_arbitrum,"ETH","https://arbiscan.io","ARBITRUM")
        val auroraBlockChain = BlockChain("Aurora",1313161554, R.drawable.ic_aurora,"ETH","https://aurorascan.dev","AURORA")
        val avalancheBlockChain = BlockChain("Avalanche C-Chain",43114, R.drawable.ic_avalanche,"AVAX","https://snowtrace.io","AVALANCHE")
        val fantomBlockChain = BlockChain("Fantom Opera",250, R.drawable.ic_fantom,"FTM","https://ftmscan.com","FANTOM")
        val harmonyBlockChain = BlockChain("Harmony",1666600000, R.drawable.ic_harmony,"ONE","https://explorer.harmony.one","HARMONY")

        val listBlockChain : ArrayList<BlockChain> = ArrayList()
        listBlockChain.add(bnbBlockChain)
        listBlockChain.add(ethereumBlockChain)
        listBlockChain.add(polygonBlockChain)
        listBlockChain.add(arbitrumBlockChain)
        listBlockChain.add(auroraBlockChain)
        listBlockChain.add(avalancheBlockChain)
        listBlockChain.add(fantomBlockChain)
        listBlockChain.add(harmonyBlockChain)

        _blockChainLiveData.postValue(listBlockChain)
    }


    fun changeStatusChain(blockChain: BlockChain){
        _blockChainLiveData.value?.forEach { it ->
            if (it.uniqueName == blockChain.uniqueName){
                it.isSelect = !it.isSelect
                updateListBlockChainSelect()
            }
        }
    }

    fun getAllTokenFavorite(){
        viewModelScope.launch (Dispatchers.IO){
            if (_tokenListLiveDataForShow.value != null){
                Log.e("tag","omad memory")
            }else{
                repository.getTokenFavorite().asFlow().collect(){
                    _tokenListLiveData.postValue(it as MutableList<TokenEntity>?)
                    _tokenListLiveDataForShow.postValue(it as MutableList<TokenEntity>?)
                }
            }
        }
    }


    fun changeStatusToken(token: TokenEntity){
        viewModelScope.launch(Dispatchers.IO) {
            token.isFavorite = !token.isFavorite
            repository.updateToken(token.idToken,token.isFavorite)
        }
    }


    private fun updateListBlockChainSelect(){
        var listBlockChainSelected:MutableList<BlockChain>  = mutableListOf()
        _blockChainLiveData.value?.forEach { it ->
            if (it.isSelect){
                listBlockChainSelected.add(it)
            }
        }
        _blockChainSelectedListLiveData.postValue(listBlockChainSelected)
    }


    fun filterTokenByBlockChainSelected(listBlockChain: List<BlockChain>){
        if (listBlockChain.isEmpty()){
            var listToken : MutableList<TokenEntity> = mutableListOf()
            _tokenListLiveData.value?.forEach {
                listToken.add(it)
            }
            listToken = listToken.distinctBy {
                it.address
            } as MutableList<TokenEntity>
            _tokenListLiveDataForShow.postValue(listToken)
        }else{
            var listToken : MutableList<TokenEntity> = mutableListOf()
            _tokenListLiveData.value?.forEach {
                    listBlockChain.forEach { blockChain ->
                        if (it.chainId == blockChain.chainId){
                            listToken.add(it)
                        }
                    }

            }
            listToken = listToken.distinctBy {
                it.address
            } as MutableList<TokenEntity>
            _tokenListLiveDataForShow.postValue(listToken)
        }

    }


    fun filterToken(searchText : String){
        var listToken : MutableList<TokenEntity> = mutableListOf()
        _tokenListLiveData.value?.forEach {
            if (it.name.lowercase().contains(searchText.lowercase()) || it.symbol.lowercase().contains(searchText.lowercase()) || it.address.lowercase() == searchText.lowercase()){
                listToken.add(it)
            }
        }
        listToken = listToken.distinctBy {
            it.address
        } as MutableList<TokenEntity>
        _tokenListLiveDataForShow.postValue(listToken)
    }

}