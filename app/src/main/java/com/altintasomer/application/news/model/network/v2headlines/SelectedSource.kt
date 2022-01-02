package com.altintasomer.application.news.model.network.v2headlines

import com.google.gson.annotations.SerializedName

data class SelectedSource (

    @SerializedName("status"       ) var status       : String?             = null,
    @SerializedName("totalResults" ) var totalResults : Int?                = null,
    @SerializedName("articles"     ) var articles     : ArrayList<Articles> = arrayListOf()

)
