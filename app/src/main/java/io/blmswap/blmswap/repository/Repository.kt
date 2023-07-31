package io.blmswap.blmswap.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import io.blmswap.blmswap.models.ResponseTokenList
import io.blmswap.blmswap.models.Token
import io.blmswap.blmswap.retrofit.RetrofitApi
import io.blmswap.blmswap.room.AppDatabase
import io.blmswap.blmswap.room.entities.*
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

class Repository @Inject constructor (private val retrofitApi: RetrofitApi, private val appDatabase: AppDatabase) {

    fun getTokenListFromApi(url :String) = retrofitApi.getTokenFromApi(url)


    private val roomDao = appDatabase.roomDao()

    fun getAllTokenListFromDatabase(): LiveData<List<TokenList>> {
        return roomDao.getAllTokenList()
    }

    fun getTokenListWithTokens():LiveData<List<TokenListWithTokens>>{
        return roomDao.getTokenListWithTokens()
    }

    fun getTokenFavorite():LiveData<List<TokenEntity>>{
        return roomDao.getTokenFavorite()
    }


    fun isTokenListExistsInDatabase() : Boolean{
        return roomDao.isTokenListExists()
    }




    fun insertTokenListWithToken(tokenList: TokenList, tokens: List<TokenEntity>){
        roomDao.insertTokenListWithTokens(tokenList, tokens)
    }



    fun updateToken(id :Long,newValue : Boolean){
        roomDao.updateToken(id,newValue)
    }

    fun changeVisibilityTokenList(tokenList: TokenList){
        roomDao.updateTokenList(tokenList)
    }


    fun deleteTokenListFromDatabase(tokenList: TokenList){
        roomDao.deleteTokenList(tokenList)
    }

}


