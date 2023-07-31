package io.blmswap.blmswap.room

import androidx.lifecycle.LiveData
import androidx.room.*
import io.blmswap.blmswap.models.ResponseTokenList
import io.blmswap.blmswap.models.Token
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.room.entities.TokenList
import io.blmswap.blmswap.room.entities.TokenListWithTokens
import kotlinx.coroutines.flow.Flow


@Dao
interface RoomDao {

    @Query("Select EXISTS(Select * from TokenListTable)")
    fun isTokenListExists() :Boolean

    @Query("Select * from TokenListTable")
    fun getAllTokenList():LiveData<List<TokenList>>


    @Insert
    fun insertTokenList(tokenList: TokenList):Long


    @Insert
    fun insetTokens(list : List<TokenEntity>)


    @Transaction
    fun insertTokenListWithTokens(tokenList: TokenList,tokens:List<TokenEntity>){
        val listId = insertTokenList(tokenList)

        tokens.forEach {
            it.idTokenList = listId
        }
        insetTokens(tokens)
    }


    @Transaction
    @Query("SELECT * FROM TokenListTable WHERE visibility")
    fun getTokenListWithTokens():LiveData<List<TokenListWithTokens>>

    @Transaction
    @Query("SELECT * FROM tokenentity WHERE isFavorite")
    fun getTokenFavorite():LiveData<List<TokenEntity>>

    @Delete
    fun deleteTokenList(tokenList: TokenList)


    @Transaction
    @Query("UPDATE tokenentity set isFavorite = :newValue WHERE idToken = :id")
    fun updateToken(id :Long,newValue : Boolean)


    @Update
    fun updateTokenList(tokenList: TokenList)



}

