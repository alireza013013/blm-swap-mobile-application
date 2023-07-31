package io.blmswap.blmswap.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.blmswap.blmswap.models.Token
import io.blmswap.blmswap.room.entities.TokenList
import java.lang.reflect.Type

class TokenTypeConverter {

    @TypeConverter
    fun convertToJSON(tokens: List<Token>):String?{
        if(tokens == null){
            return null
        }
        val gson = Gson()
        val type : Type = object  : TypeToken<List<Token?>>() {}.type
        return gson.toJson(tokens, type)
    }

    @TypeConverter
    fun convertToDataClass(tokenString :String?): List<Token?>{
        if (tokenString == null){
            return listOf(null)
        }
        val gson = Gson()
        val type : Type = object  : TypeToken<List<Token?>>() {}.type
        return gson.fromJson(tokenString, type)
    }
}