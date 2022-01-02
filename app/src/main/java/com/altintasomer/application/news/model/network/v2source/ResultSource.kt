package com.altintasomer.application.news.model.network.v2source

import com.google.gson.annotations.SerializedName

data class ResultSource(

    @SerializedName("status") var status: String? = null,
    @SerializedName("sources") var sources: ArrayList<Sources> = arrayListOf()

)
