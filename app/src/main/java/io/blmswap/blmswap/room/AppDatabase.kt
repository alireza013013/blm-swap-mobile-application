package io.blmswap.blmswap.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.blmswap.blmswap.models.Token
import io.blmswap.blmswap.room.entities.TokenEntity
import io.blmswap.blmswap.room.entities.TokenList


@Database(entities = [TokenList::class,TokenEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun roomDao() : RoomDao
}