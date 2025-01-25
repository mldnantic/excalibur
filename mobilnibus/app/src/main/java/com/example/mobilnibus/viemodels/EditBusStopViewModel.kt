package com.example.mobilnibus.viemodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class EditBusStopViewModel: ViewModel() {
    var name: String by mutableStateOf("")

    var lat: Double by mutableStateOf(0.0)
        private set
    var lng: Double by mutableStateOf(0.0)
        private set
    fun setLatLng(latitude: Double,longitude: Double)
    {
        lat=latitude
        lng=longitude
    }
    fun reset() {
        name = ""
        lat = 0.0
        lng = 0.0
    }
}