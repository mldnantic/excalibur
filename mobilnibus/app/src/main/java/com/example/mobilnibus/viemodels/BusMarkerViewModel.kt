package com.example.mobilnibus.viemodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobilnibus.model.BusMarkerModel
import com.example.mobilnibus.storage.BusMarkerStorageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BusMarkerViewModel(private val storageService: BusMarkerStorageService): ViewModel() {

    var busMarker: BusMarkerModel by mutableStateOf(BusMarkerModel())
        private set

    val busMarkers: Flow<List<BusMarkerModel>> = storageService.busmarkers

    fun setCurrentBusMarker(busMarkerModel: BusMarkerModel){
        busMarker=busMarkerModel
    }

    fun resetCurrentBusMarker()
    {
        busMarker=BusMarkerModel()
    }

    fun addOrUpdateBusMarker(busId:String, linija:String, lat:Double, lng:Double)
    {
        viewModelScope.launch {
            storageService.save(busId,linija,lat,lng)
        }
    }

    fun getBusMarker(busId: String): Boolean
    {
        var exists = false
        viewModelScope.launch {
            exists = storageService.get(busId)
        }
        return exists
    }

    fun updateBusMarker(busId:String,lat:Double,lng: Double)
    {
        viewModelScope.launch {
            storageService.update(busId,lat,lng)
        }
    }
    fun deleteBusMarker(id:String)
    {
        viewModelScope.launch {
            storageService.delete(id)
        }
    }

}

class BusMarkerViewModelFactory(private val storageService: BusMarkerStorageService): ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(BusMarkerViewModel::class.java)){
            return BusMarkerViewModel(storageService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}