package com.altintasomer.application.news.di.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.altintasomer.application.news.model.db.ArticlesEntity

@Database(entities = [ArticlesEntity::class], version = 1, exportSchema = false)
abstract class MyNewsDatabase : RoomDatabase(){
    abstract fun myNewsDao() : MyNewsDao
}