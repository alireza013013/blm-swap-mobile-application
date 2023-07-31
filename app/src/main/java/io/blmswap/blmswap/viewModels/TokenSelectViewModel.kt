package io.blmswap.blmswap.viewModels

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.blmswap.blmswap.R
import io.blmswap.blmswap.models.BlockChain
import io.blmswap.blmswap.models.DefaultListToken
import io.blmswap.blmswap.models.ResponseTokenList
import io.blmswap.blmswap.repository.Repository
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.room.entities.TokenList
import io.blmswap.blmswap.room.entities.TokenListWithTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Matcher
import javax.inject.Inject


@HiltViewModel
class TokenSelectViewModel @Inject constructor(
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


    private var _tokenListWithTokensLiveData = MutableLiveData<List<TokenListWithTokens>>()
    val tokenListWithTokensLiveData : LiveData<List<TokenListWithTokens>>
        get() = _tokenListWithTokensLiveData




    init {
        setInformationBlockChain()
    }

    fun getInformationFromApi(){
        val firstItemList = DefaultListToken("https://tokens.pancakeswap.finance/pancakeswap-top-100.json",true)
        val secondItemList = DefaultListToken("https://tokens.pancakeswap.finance/cmc.json",true)
        val thirdItemList = DefaultListToken("https://tokens.pancakeswap.finance/coingecko.json",true)
        val fourthItemList = DefaultListToken("https://wispy-bird-88a7.uniswap.workers.dev/?url=http://t2crtokens.eth.link",false)
        val fifthItemList = DefaultListToken("https://tokens.1inch.eth.link",false)
        val listApiGetToken: List<DefaultListToken> = listOf(firstItemList,secondItemList, thirdItemList,fourthItemList,fifthItemList)

        var defaultList :MutableList<TokenList> = mutableListOf()



        viewModelScope.launch(Dispatchers.IO) {
            if (_responseTokenListLiveData.value != null){
                Log.e("tag","omad memory")
            }else{
                if (repository.isTokenListExistsInDatabase()){
                    Log.e("Tag","omad bare database")
                repository.getTokenListWithTokens().asFlow().collect(){
                    _tokenListWithTokensLiveData.postValue(it as MutableList<TokenListWithTokens>?)
                   var tokens : MutableList<TokenEntity> = mutableListOf()
                    var listToken : MutableList<TokenList> = mutableListOf()
                    it.forEach { list ->
                        listToken.add(list.tokenList)
                        list.tokens.forEach { token->
//                            Log.e("if check",token.logoURI.toString())
//                            if (token.logoURI!!.contains("https")){
//                                Log.e("tttt","if")
                                tokens.add(token)
//                            }
                        }
                    }
                    tokens = tokens.distinctBy {
                        it.address
                    } as MutableList<TokenEntity>
                    _responseTokenListLiveData.postValue(listToken)
                    _tokenListLiveData.postValue(tokens)
                }

                }else{
                    Log.e("TAG","omad bare api")
                    listApiGetToken.forEach {
                        viewModelScope.launch(Dispatchers.IO) {
                            val client = repository.getTokenListFromApi(it.url)
                            client.enqueue(object : retrofit2.Callback<ResponseTokenList> {
                                override fun onResponse(
                                    call: Call<ResponseTokenList>,
                                    response: Response<ResponseTokenList>
                                ) {
                                    if (response.isSuccessful){

                                        response.body()?.logoURI = uriToHttps(response.body()?.logoURI.toString())

                                        //add information blockchain for each token
                                        response.body()?.tokens?.forEach{ token ->
                                            token.isFavorite = false
                                            token.logoURI = uriToHttps(token.logoURI.toString())
                                            _blockChainLiveData.value?.forEach { blockChain ->
                                                if (token.chainId == blockChain.chainId){
                                                    token.logoBlockChain = blockChain.imageSrc
                                                    token.nameBlockChain = blockChain.name
                                                }
                                            }
                                        }




                                        val tokenList = response.body()
                                            ?.let { item -> TokenList(0,it.visibility,item.name,item.timestamp,item.logoURI,item.tokens.size
                                            ,item.version.major,item.version.minor,item.version.patch
                                            ) }

                                        //add tokenList object to room database
                                        viewModelScope.launch (Dispatchers.IO){
                                            if (tokenList != null) {
                                                repository.insertTokenListWithToken(tokenList,
                                                    response.body()!!.tokens)
                                            }
                                        }


                                        if (tokenList != null) {
                                            defaultList.add(tokenList)
                                        }
                                        if (defaultList.size == listApiGetToken.size){
                                            _responseTokenListLiveData.postValue(defaultList)
                                            viewModelScope.launch(Dispatchers.IO) {
                                                repository.getTokenListWithTokens().asFlow().collect() {
                                                    _tokenListWithTokensLiveData.postValue(it as MutableList<TokenListWithTokens>?)
                                                }
                                            }
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<ResponseTokenList>, t: Throwable) {
                                    Log.d("fail request",""+t.message)
                                }

                            })
                        }

                    }
                }
            }
        }
    }






    fun uriToHttps(url : String):String{
        if (url != null){
            var protocolSplit = url.split(":")
            if (protocolSplit != null && protocolSplit.isNotEmpty()){
                var protocol = protocolSplit[0].lowercase()
                if (protocol == "data"){
                    return url
                }else if (protocol == "https"){
                    return url
                }else if (protocol == "http"){
                    return "https${url.substring(4)}"
                }else if (protocol == "ipfs"){
                    val regex = Regex("ipfs://")
                    var nameIPFS = url.replaceFirst(regex,"")
                    return "https://cloudflare-ipfs.com/ipfs/${nameIPFS}/"
                }else if (protocol == "ipns"){
                    val regex = Regex("ipns://")
                    var nameIPNS = url.replaceFirst(regex,"")
                    return "https://cloudflare-ipfs.com/ipns/${nameIPNS}/"
                }else if (protocol == "ar"){
                    val regex = Regex("ar://")
                    val tx = url.replaceFirst(regex,"")
                    return "https://arweave.net/${tx}"
                }
                return ""
            }
        }
        return ""
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


    fun setInformationToken(){
        var listToken : MutableList<TokenEntity> = mutableListOf()
        _tokenListWithTokensLiveData.value?.forEach {
            it.tokens.forEach { token->
                listToken.add(token)
            }
        }
        listToken = listToken.distinctBy {
            it.address
        } as MutableList<TokenEntity>
        _tokenListLiveData.postValue(listToken)
    }

    fun filterToken(searchText : String){
        var listToken : MutableList<TokenEntity> = mutableListOf()
        _tokenListWithTokensLiveData.value?.forEach { item ->
            item.tokens.forEach {
                if (it.name.lowercase().contains(searchText.lowercase()) || it.symbol.lowercase().contains(searchText.lowercase()) || it.address.lowercase() == searchText.lowercase()){
                    listToken.add(it)
                }
            }
        }
        listToken = listToken.distinctBy {
            it.address
        } as MutableList<TokenEntity>
        _tokenListLiveData.postValue(listToken)
    }

    fun filterTokenByBlockChainSelected(listBlockChain: List<BlockChain>){
            if (listBlockChain.isEmpty()){
                var listToken : MutableList<TokenEntity> = mutableListOf()
                _tokenListWithTokensLiveData.value?.forEach {
                    it.tokens.forEach { token ->
                        listToken.add(token)
                    }
                }
                listToken = listToken.distinctBy {
                    it.address
                } as MutableList<TokenEntity>
                _tokenListLiveData.postValue(listToken)
            }else{
                var listToken : MutableList<TokenEntity> = mutableListOf()
                _tokenListWithTokensLiveData.value?.forEach {
                    it.tokens.forEach { token ->
                        listBlockChain.forEach { blockChain ->
                            if (token.chainId == blockChain.chainId){
                                    listToken.add(token)
                            }
                        }
                    }
                }
                listToken = listToken.distinctBy {
                    it.address
                } as MutableList<TokenEntity>
                _tokenListLiveData.postValue(listToken)
            }

    }
}