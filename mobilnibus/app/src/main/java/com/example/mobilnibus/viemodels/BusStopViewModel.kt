package com.example.mobilnibus.viemodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobilnibus.model.BusStopModel
import com.example.mobilnibus.storage.BusStopStorageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BusStopViewModel(private val storageService: BusStopStorageService): ViewModel() {

    var busStop: BusStopModel by mutableStateOf(BusStopModel())
        private set

    val busStops: Flow<List<BusStopModel>> = storageService.busstops

    fun setCurrentBusStop(busStopModel: BusStopModel){
        busStop=busStopModel
    }

    fun resetCurrentBusStop()
    {
        busStop=BusStopModel()
    }

    fun addBusStop(name:String,lat:Double,lng:Double)
    {
            val b = BusStopModel(name=name,lat=lat,lng=lng)
            viewModelScope.launch {
                storageService.save(b)
            }
    }

    fun deleteBusStop(id:String)
    {
        viewModelScope.launch {
            storageService.delete(id)
        }
    }

}

class BusStopViewModelFactory(private val storageService:BusStopStorageService): ViewModelProvider.Factory{
    override fun <T:ViewModel> create(modelClass:Class<T>):T{
        if(modelClass.isAssignableFrom(BusStopViewModel::class.java)){
            return BusStopViewModel(storageService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}