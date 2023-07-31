package io.blmswap.blmswap.adapters

import io.blmswap.blmswap.models.BlockChain
import io.blmswap.blmswap.models.Token
import io.blmswap.blmswap.room.entities.TokenEntity

interface CardClickListener {
    fun clickOnCardBlockChain(chain : BlockChain,position : Int)

    fun clickChangeStatusToken(token : TokenEntity, position: Int)

    fun clickOnCardTokenForSelect(token: TokenEntity)
}