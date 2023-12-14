package com.example.mycartfinal.Model.DirectionModel

import com.squareup.moshi.Json

data class DirectionResponseModel(
    @field:Json(name = "routes")
    var directionRouteModels: List<DirectionRouteModel>? = null,

    @field:Json(name = "error_message")
    val error: String? = null
)