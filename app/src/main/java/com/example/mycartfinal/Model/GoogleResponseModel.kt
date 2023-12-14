package com.example.mycartfinal.Model

import com.squareup.moshi.Json


data class GoogleResponseModel(
    @field:Json(name = "results")
    val googlePlaceModelList: List<GooglePlaceModel>?,
    @field:Json(name = "error_message")
    val error: String?
)