package com.altintasomer.application.news.model.network

import com.google.gson.annotations.SerializedName

data class Sources (

    @SerializedName("id"          ) var id          : String? = null,
    @SerializedName("name"        ) var name        : String? = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("url"         ) var url         : String? = null,
    @SerializedName("category"    ) var category    : String? = null,
    @SerializedName("language"    ) var language    : String? = null,
    @SerializedName("country"     ) var country     : String? = null

)
