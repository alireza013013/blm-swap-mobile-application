package io.blmswap.blmswap.models

data class BlockChain(
    val name       : String,
    val chainId    : Int,
    val imageSrc   : Int,
    val symbol     : String,
    val scanner    : String,
    val uniqueName : String,
    var isSelect   : Boolean = false
)
