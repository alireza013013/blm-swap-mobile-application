package io.blmswap.blmswap.room.entities

import androidx.room.*
import com.google.gson.annotations.SerializedName
import io.blmswap.blmswap.models.ResponseTokenList
import io.blmswap.blmswap.models.Token
import io.blmswap.blmswap.room.TokenTypeConverter

@Entity(tableName = "TokenListTable")
data class TokenList(
    @PrimaryKey(autoGenerate = true)
    val idTokenList:Long,
    var visibility:Boolean,
    val name: String,
    val timestamp: String,
    var logoURI: String,
    var countToken:Int,
    var versionMajor : Int,
    var versionMinor : Int,
    var versionPatch : Int,
)