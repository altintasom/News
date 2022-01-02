package com.altintasomer.application.news.di.db

import androidx.room.*
import com.altintasomer.application.news.model.db.ArticlesEntity

@Dao
interface MyNewsDao {
    @Query("select * from articles")
    suspend fun getMyNews() : List<ArticlesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articlesEntity: ArticlesEntity) : Long

    @Delete
    suspend fun deleteArticles(articlesEntity: ArticlesEntity)

    @Query("DELETE FROM articles WHERE url = :dUrl")
    suspend fun deleteWithUrl(dUrl  : String?)
}