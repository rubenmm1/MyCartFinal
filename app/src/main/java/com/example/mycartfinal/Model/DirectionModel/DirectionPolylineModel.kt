package com.example.mycartfinal.Model.DirectionModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class DirectionPolylineModel(
    @field:Json(name="points")

    var points: String? = null
)