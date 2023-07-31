package io.blmswap.blmswap.retrofit

import io.blmswap.blmswap.models.ResponseTokenList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitApi {
    @GET
    fun getTokenFromApi(@Url url:String): Call<ResponseTokenList>
}