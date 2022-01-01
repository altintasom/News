package com.altintasomer.application.news.model.network

import com.google.gson.annotations.SerializedName

data class News(

    @SerializedName("status") var status: String? = null,
    @SerializedName("sources") var sources: ArrayList<Sources> = arrayListOf()

)
