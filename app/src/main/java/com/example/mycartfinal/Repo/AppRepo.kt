package com.example.mycartfinal.Repo

import android.util.Log
import com.example.mycartfinal.Network.RetroFitClient
import com.example.mycartfinal.Utility.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class AppRepo {


    fun getPlaces(url: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))
        val response = RetroFitClient.retrofitApi.getNearByPlaces(url = url)

        Log.d("TAG", "getPlaces:  $response ")
        if (response.body()?.googlePlaceModelList?.size!! > 0) {
            Log.d(
                "TAG",
                "getPlaces:  Success called ${response.body()?.googlePlaceModelList?.size}"
            )

            emit(State.success(response.body()!!))
        } else {
            Log.d("TAG", "getPlaces:  failed called")
            emit(State.failed(response.body()!!.error!!))
        }


    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)








}