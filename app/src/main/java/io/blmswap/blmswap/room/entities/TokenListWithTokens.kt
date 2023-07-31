package io.blmswap.blmswap.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import io.blmswap.blmswap.models.Token

data class TokenListWithTokens(
    @Embedded
    val tokenList : TokenList,
    @Relation(
        parentColumn = "idTokenList",
        entityColumn = "idTokenList",
    )
    val tokens : List<TokenEntity>
)