package com.example.mycartfinal.Network

import com.example.mycartfinal.Model.DirectionModel.DirectionResponseModel
import com.example.mycartfinal.Model.GoogleResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitApi {

    @GET
    suspend fun getNearByPlaces(@Url url: String): Response<GoogleResponseModel>

}