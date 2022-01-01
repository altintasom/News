package com.altintasomer.application.news.model

import com.altintasomer.application.news.model.network.News
import retrofit2.Response

interface Repo {

    suspend fun getNewsSources() :  Response<News>
}