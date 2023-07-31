package io.blmswap.blmswap.adapters

import io.blmswap.blmswap.room.entities.TokenList

interface TokenListClickListener {
    fun changeStatusVisibilityListToken(tokenList: TokenList,position : Int)

    fun deleteListTokenFromDatabase(tokenList: TokenList)
}