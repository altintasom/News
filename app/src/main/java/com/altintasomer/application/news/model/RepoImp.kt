package com.altintasomer.application.news.model

import com.altintasomer.application.news.di.network.NewsApi
import com.altintasomer.application.news.model.network.News
import retrofit2.Response
import javax.inject.Inject

class RepoImp @Inject constructor(
    private val api : NewsApi
): Repo {
    override suspend fun getNewsSources(): Response<News> = api.getNewsSources()
}