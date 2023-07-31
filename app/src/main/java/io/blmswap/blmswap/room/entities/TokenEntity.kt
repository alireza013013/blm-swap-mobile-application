package io.blmswap.blmswap.room.entities


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class TokenEntity(
    @PrimaryKey(autoGenerate = true)
    val idToken:Long,
    var idTokenList:Long,
    val name: String,
    val symbol : String,
    val address : String,
    val chainId : Int,
    val decimals : Int,
    var logoURI : String?,
    var isFavorite : Boolean,
    var logoBlockChain : Int?,
    var nameBlockChain : String?
)
