package com.altintasomer.application.news.model

import com.altintasomer.application.news.di.db.MyNewsDao
import com.altintasomer.application.news.di.network.NewsApi
import com.altintasomer.application.news.model.db.ArticlesEntity
import com.altintasomer.application.news.model.network.v2headlines.SelectedSource
import com.altintasomer.application.news.model.network.v2source.ResultSource
import retrofit2.Response
import javax.inject.Inject

class RepoImp @Inject constructor(
    private val api : NewsApi,
    private val myNewsDao: MyNewsDao
): Repo {
    override suspend fun getNewsSources(): Response<ResultSource> = api.getNewsSources()
    override suspend fun getNewsSourceForSelected(source: String): Response<SelectedSource> = api.getNewsSourceForSelected(source = source)
    override suspend fun getMyNews(): List<ArticlesEntity> = myNewsDao.getMyNews()
    override suspend fun insertArticles(articlesEntity: ArticlesEntity): Long = myNewsDao.insertArticles(articlesEntity)
    override suspend fun deleteArticles(articlesEntity: ArticlesEntity) = myNewsDao.deleteArticles(articlesEntity)
    override suspend fun deleteWithUrl(dUrl: String?) = myNewsDao.deleteWithUrl(dUrl)
}