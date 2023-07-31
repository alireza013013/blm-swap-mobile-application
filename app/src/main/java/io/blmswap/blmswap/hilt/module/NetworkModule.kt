package io.blmswap.blmswap.hilt.module

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.blmswap.blmswap.MainActivity
import io.blmswap.blmswap.repository.Repository
import io.blmswap.blmswap.retrofit.RetrofitApi
import io.blmswap.blmswap.room.AppDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val BASE_URL = "https://tokens.pancakeswap.finance"

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitApi(retrofit: Retrofit) : RetrofitApi{
        return retrofit.create(RetrofitApi::class.java)
    }


    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase =
        Room.databaseBuilder(application,AppDatabase::class.java,"BlmSwapDatabase")
            .build()


    @Provides
    @Singleton
    fun provideRepository(retrofitApi: RetrofitApi,appDatabase: AppDatabase):Repository{
        return Repository(retrofitApi,appDatabase)
    }

}