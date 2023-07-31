package io.blmswap.blmswap.models

data class TokenListDownload(
    var visibility:Boolean,
    val name: String,
    val timestamp: String,
    var logoURI: String,
    var countToken:Int,
    var isImported:Boolean
)
