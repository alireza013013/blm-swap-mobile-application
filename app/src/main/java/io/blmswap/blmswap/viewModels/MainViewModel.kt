package io.blmswap.blmswap.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.blmswap.blmswap.models.BlockChain
import io.blmswap.blmswap.repository.Repository
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.room.entities.TokenList
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel()  {

    private var _firstTokenSelectLiveData = MutableLiveData<TokenEntity>()
    val firstTokenSelectLiveData : LiveData<TokenEntity>
        get() = _firstTokenSelectLiveData


    private var _secondTokenSelectLiveData = MutableLiveData<TokenEntity>()
    val  secondTokenSelectLiveData : LiveData<TokenEntity>
        get() = _secondTokenSelectLiveData



    private var _addressWalletLiveData = MutableLiveData<String?>()
    val addressWalletLiveData : LiveData<String?>
       get() = _addressWalletLiveData


    fun setTokenSelect(numberToken:String,tokenEntity: TokenEntity){
        if (numberToken == "FirstToken"){
            _firstTokenSelectLiveData.postValue(tokenEntity)
        }else if ( numberToken == "SecondToken"){
            _secondTokenSelectLiveData.postValue(tokenEntity)
        }
    }


    fun reverseToken(){
        if (_secondTokenSelectLiveData.value != null && _firstTokenSelectLiveData.value != null){
            var firstToken = _firstTokenSelectLiveData.value
            var secondToken = _secondTokenSelectLiveData.value
            _firstTokenSelectLiveData.postValue(secondToken!!)
            _secondTokenSelectLiveData.postValue(firstToken!!)
        }
    }


    fun setAddressAccount(address : String){
        _addressWalletLiveData.postValue(address)
    }

    fun disconnectWallet(){
        _addressWalletLiveData.postValue(null)
    }


}