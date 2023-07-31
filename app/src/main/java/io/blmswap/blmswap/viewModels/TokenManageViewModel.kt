package io.blmswap.blmswap.viewModels

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.blmswap.blmswap.R
import io.blmswap.blmswap.models.BlockChain
import io.blmswap.blmswap.models.ResponseTokenList
import io.blmswap.blmswap.models.TokenListDownload
import io.blmswap.blmswap.repository.Repository
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.room.entities.TokenList
import io.blmswap.blmswap.room.entities.TokenListWithTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class TokenManageViewModel  @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private var _blockChainLiveData = MutableLiveData<List<BlockChain>>()
    val blockChainLiveData : LiveData<List<BlockChain>>
        get() = _blockChainLiveData

    private var _tokenListLiveData = MutableLiveData<List<TokenList>>()
    val tokenListLiveData : LiveData<List<TokenList>>
        get() = _tokenListLiveData


    private var _tokenListDownloadForDatabaseLiveData = MutableLiveData<TokenList?>()
    val tokenListDownloadForDatabaseLiveData : LiveData<TokenList?>
        get() = _tokenListDownloadForDatabaseLiveData

    private var _tokensDownloadForDatabaseLiveData = MutableLiveData<MutableList<TokenEntity>?>()
    val tokensDownloadForDatabaseLiveData : LiveData<MutableList<TokenEntity>?>
        get() = _tokensDownloadForDatabaseLiveData

    private var _tokenListDownloadLiveData = MutableLiveData<TokenListDownload?>()
    val tokenListDownloadLiveData : LiveData<TokenListDownload?>
        get() = _tokenListDownloadLiveData

    init {
        setInformationBlockChain()
    }



    fun getTokenListFromDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllTokenListFromDatabase().asFlow().collect(){
                _tokenListLiveData.postValue(it)
            }
        }
    }

    fun changeVisibilityTokenList(tokenList: TokenList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeVisibilityTokenList(tokenList)
        }
    }

    fun validationURL(url : String){
        var editedUrl :String = url
        var regexETS = Regex("^(([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.)+)eth(\\/.*)?\$")
        if (uriToHttps(url) != ""){
          sendRequestToGetInformation(url)
        }else if (regexETS.matches(url)){
            if (!url.contains("https://")){
                editedUrl = "https://"+ editedUrl
            }
            if(!url.contains(".link")){
                editedUrl = editedUrl + ".link"
            }
            sendRequestToGetInformation(editedUrl)
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


    fun insetNewTokenListToDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("token in manage",_tokensDownloadForDatabaseLiveData.value.toString())
            repository.insertTokenListWithToken(_tokenListDownloadForDatabaseLiveData.value!!,tokensDownloadForDatabaseLiveData.value!!)
            _tokenListDownloadLiveData.postValue(null)
            _tokensDownloadForDatabaseLiveData.postValue(null)
            _tokenListDownloadForDatabaseLiveData.postValue(null)
        }
    }

    fun deleteInformationDownloadFromMemory(){
        _tokenListDownloadLiveData.postValue(null)
        _tokensDownloadForDatabaseLiveData.postValue(null)
        _tokenListDownloadForDatabaseLiveData.postValue(null)
    }

    fun deleteTokenListFromDatabase(tokenList: TokenList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTokenListFromDatabase(tokenList)
        }
    }

    private fun sendRequestToGetInformation(url: String){
        viewModelScope.launch(Dispatchers.IO) {
            val client = repository.getTokenListFromApi(url)
            client.enqueue(object : retrofit2.Callback<ResponseTokenList> {
                override fun onResponse(
                    call: Call<ResponseTokenList>,
                    response: Response<ResponseTokenList>
                ) {
                    _tokenListDownloadLiveData.postValue(null)
                    _tokensDownloadForDatabaseLiveData.postValue(null)
                    _tokenListDownloadForDatabaseLiveData.postValue(null)
                    if (response.isSuccessful){
                        Log.e("Marahel","1")
                        response.body()?.logoURI = uriToHttps(response.body()?.logoURI.toString())

                        var listTokens : MutableList<TokenEntity> = mutableListOf()
                        //add information blockchain for each token
                        Log.e("Marahel","2")
                        response.body()?.tokens?.forEach{ token ->
                            token.isFavorite = false
                            Log.e("check",uriToHttps(token.logoURI.toString()))
                            token.logoURI = uriToHttps(token.logoURI.toString())
                            _blockChainLiveData.value?.forEach { blockChain ->
                                if (token.chainId == blockChain.chainId){
                                    token.logoBlockChain = blockChain.imageSrc
                                    token.nameBlockChain = blockChain.name
                                }
                            }
                            listTokens.add(token)
                        }


                        Log.e("Marahel","3")
                        val tokenList = response.body()
                            ?.let { item -> TokenList(0,true,item.name,item.timestamp,item.logoURI,item.tokens.size
                                ,item.version.major,item.version.major,item.version.patch) }


                        Log.e("Marahel","4")
                        val tokenListDownload = response.body()
                            ?.let { item -> TokenListDownload(true,item.name,item.timestamp,item.logoURI,item.tokens.size,false
                            ) }


                        Log.e("Marahel","5")
                        _tokenListLiveData.value?.forEach {
                            if (it.name == tokenListDownload?.name){
                                tokenListDownload.isImported = true
                            }
                        }


                        Log.e("Marahel","6")
                        _tokenListDownloadLiveData.postValue(tokenListDownload)
                        _tokenListDownloadForDatabaseLiveData.postValue(tokenList)
                        _tokensDownloadForDatabaseLiveData.postValue(listTokens)
                        Log.e("Marahel","7")
                        Log.e("new",response.body()?.tokens.toString())
                        Log.e("new",listTokens.toString())
                        Log.e("new",_tokensDownloadForDatabaseLiveData.value.toString())


                    }
                }

                override fun onFailure(call: Call<ResponseTokenList>, t: Throwable) {
                    _tokenListDownloadLiveData.postValue(null)
                    Log.e("fail request",""+t.message)
                }

            })
        }

    }
}