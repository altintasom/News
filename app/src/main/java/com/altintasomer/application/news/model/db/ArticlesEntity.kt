package com.altintasomer.application.news.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "articles"
)
data class ArticlesEntity(
    @PrimaryKey(autoGenerate = true)
    var uid : Long = 0L,

    @ColumnInfo(name = "source_id") var sourceId : String? = null,
    @ColumnInfo(name = "source_name")  var sourceName : String? = null,
    @ColumnInfo(name = "title") var title : String? = null,
    @ColumnInfo(name = "url")  var url : String? = null,
)
