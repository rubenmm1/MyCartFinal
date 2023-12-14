package com.example.mycartfinal.Model.DirectionModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class StartLocationModel(
    @field:Json(name = "lat")
    var lat: Double? = null,

    @field:Json(name = "lng")
    var lng: Double? = null
)