package com.altintasomer.application.news.model

import com.altintasomer.application.news.model.db.ArticlesEntity
import com.altintasomer.application.news.model.network.v2headlines.SelectedSource
import com.altintasomer.application.news.model.network.v2source.ResultSource
import retrofit2.Response

interface Repo {

    suspend fun getNewsSources() :  Response<ResultSource>

    suspend fun getNewsSourceForSelected(source : String) : Response<SelectedSource>

    suspend fun getMyNews() : List<ArticlesEntity>

    suspend fun insertArticles(articlesEntity: ArticlesEntity) : Long

    suspend fun deleteArticles(articlesEntity: ArticlesEntity)

    suspend fun deleteWithUrl(dUrl : String?)
}