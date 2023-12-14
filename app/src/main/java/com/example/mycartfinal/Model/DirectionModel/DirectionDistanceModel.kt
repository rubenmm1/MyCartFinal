package com.example.mycartfinal.Model.DirectionModel

import com.google.gson.annotations.Expose
import com.squareup.moshi.Json

data class DirectionDistanceModel(
    @field:Json(name = "text")
    var text: String? = null,
    @field:Json(name = "value")
    var value: Int? = null
)