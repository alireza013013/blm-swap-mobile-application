package io.blmswap.blmswap.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.blmswap.blmswap.models.ResponseTokenList
import io.blmswap.blmswap.room.entities.TokenList
import java.lang.reflect.Type

class TokenListTypeConverter {

    @TypeConverter
    fun convertToJSON(listToken:TokenList):String?{
        if(listToken == null){
            return null
        }
        val gson = Gson()
        val type : Type = object  : TypeToken<TokenList?>() {}.type
        return gson.toJson(listToken, type)
    }

    @TypeConverter
    fun convertToDataClass(listTokenString :String?): TokenList?{
        if (listTokenString == null){
            return null
        }
        val gson = Gson()
        val type : Type = object  : TypeToken<TokenList?>() {}.type
        return gson.fromJson(listTokenString, type)
    }
}