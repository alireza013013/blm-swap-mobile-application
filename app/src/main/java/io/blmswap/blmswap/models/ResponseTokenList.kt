package io.blmswap.blmswap.models

import android.opengl.Visibility
import com.google.gson.annotations.SerializedName
import io.blmswap.blmswap.room.entities.TokenEntity

data class ResponseTokenList(
//    var visibility: Boolean = false,
    @SerializedName("name")
    val name : String,
    @SerializedName("timestamp")
    val timestamp : String,
    @SerializedName("logoURI")
    var logoURI : String,
    @SerializedName("tokens")
    var tokens : List<TokenEntity>,
    @SerializedName("version")
    var version : Version,
)
