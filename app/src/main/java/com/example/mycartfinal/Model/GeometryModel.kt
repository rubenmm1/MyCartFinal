package com.example.mycartfinal.Model


import com.squareup.moshi.Json

data class GeometryModel(
    @field:Json(name = "location")
    val location: LocationModel?
)
