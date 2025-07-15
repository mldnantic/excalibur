package com.example.mobilnibus.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilnibus.MainActivity
import com.example.mobilnibus.R
import com.example.mobilnibus.location.LocationService
import com.example.mobilnibus.model.BusStopModel
import com.example.mobilnibus.viemodels.BusMarkerViewModel
import com.example.mobilnibus.viemodels.BusStopViewModel
import com.example.mobilnibus.viemodels.UserViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    auth: FirebaseAuth, mainActivity: MainActivity,
    userViewModel: UserViewModel,
    busStopViewModel: BusStopViewModel,
    navigateToSettings: () -> Unit,
    navigateToViewBusStop: () -> Unit,
    onMapLongClick: (LatLng) -> Unit,
    busMarkerViewModel: BusMarkerViewModel,
    list: List<BusStopModel>,
    startMarkerService: () -> Unit,
    stopMarkerService: () -> Unit
)
{
    val busMarkersList = busMarkerViewModel.busMarkers.collectAsState(initial = listOf())

    val nis = LatLng(43.321445, 21.896104)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nis, 15f)
    }
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                tiltGesturesEnabled = false,
                compassEnabled = false,
                rotationGesturesEnabled = false,
                myLocationButtonEnabled = false,
            )
        )
    }
    val nisBounds = LatLngBounds(
        LatLng(43.2923021, 21.8534963),  // SW bounds
        LatLng(43.3624341, 21.9818614) // NE bounds
    )
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL,
            isMyLocationEnabled = LocationService.isActive,
            latLngBoundsForCameraTarget = nisBounds,
            minZoomPreference = 12.0f))
    }

    Surface(color = Color.Black) {
        Surface(
            color = Color.White,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.9f),
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    uiSettings = uiSettings,
                    contentPadding = PaddingValues(8.dp),
                    onMapLongClick = {latLng->
                        if(userViewModel.currentUserModel.role=="admin")
                        {
                            Toast.makeText(mainActivity, "Adding bus stop...", Toast.LENGTH_SHORT).show()
                            onMapLongClick(latLng)
                        }
                    }
                )
                {
                    list.forEach {
                        val busStop = it
                        Marker(
                            state = MarkerState(position = LatLng(it.lat,it.lng)),
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.bus_stop),
                            onClick ={
                                busStopViewModel.setCurrentBusStop(busStop)
                                navigateToViewBusStop()
                                return@Marker false
                            }
                        )
                    }
                    busMarkersList.value.forEach{
                        val busMarker = it
                        Marker(
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.icons8_bus_32),
                            title = busMarker.linija,
                            state = MarkerState(position = LatLng(busMarker.lat,busMarker.lng))
                            )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ElevatedButton(
                        onClick = { navigateToSettings() },
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(2.dp, 0.dp)
                    )
                    {
                        Text(text = "⚙️", fontSize = 24.sp)
                    }
                    ElevatedButton(
                        onClick = {
                            if(LocationService.isActive) {
                                  cameraPositionState.position = CameraPosition.
                                  fromLatLngZoom(LatLng(LocationService.latitude,LocationService.longitude),15f)
                                if(userViewModel.currentUserModel.role=="bus")
                                {

                                    //startMarkerService()

                                    busMarkerViewModel.addOrUpdateBusMarker(
                                        userViewModel.currentUserModel.id,
                                        userViewModel.currentUserModel.firstName,
                                        LocationService.latitude,
                                        LocationService.longitude
                                    )
                                }
                            }
                            else {
                                Toast.makeText(mainActivity, "Location service unavailable", Toast.LENGTH_SHORT).show()
                            } },
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(2.dp, 0.dp)
                    )
                    {
                        Text(text = "📌", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}