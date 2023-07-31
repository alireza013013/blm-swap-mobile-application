package io.blmswap.blmswap.models

import com.google.gson.annotations.SerializedName

data class Token(
    var id:Long =0,
    @SerializedName("name")
    val name: String,
    @SerializedName("symbol")
    val symbol : String,
    @SerializedName("address")
    val address : String,
    @SerializedName("chainId")
    val chainId : Int,
    @SerializedName("decimals")
    val decimals : Int,
    @SerializedName("logoURI")
    val logoURI : String,
    var isFavorite : Boolean = false,
    var logoBlockChain : Int,
    var nameBlockChain : String
)
