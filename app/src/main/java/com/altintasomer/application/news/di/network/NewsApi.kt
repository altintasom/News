package com.altintasomer.application.news.di.network

import com.altintasomer.application.news.model.network.v2headlines.SelectedSource
import com.altintasomer.application.news.model.network.v2source.ResultSource
import com.altintasomer.application.news.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/sources")
    suspend fun getNewsSources(@Query("language")language: String? = "en",@Query("apiKey") apiKey : String? = API_KEY) : Response<ResultSource>


    @GET("v2/top-headlines")
    suspend fun getNewsSourceForSelected(@Query("sources") source : String,@Query("apiKey") apiKey : String? = API_KEY) : Response<SelectedSource>

}