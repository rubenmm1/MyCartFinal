package com.example.mycartfinal.ViewModel

import androidx.lifecycle.ViewModel
import com.example.mycartfinal.Repo.AppRepo

class LocationViewModel : ViewModel() {

    var repo = AppRepo()

    fun getNearByPlace(url: String) = repo.getPlaces(url)



}