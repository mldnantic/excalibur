package com.example.mobilnibus.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobilnibus.model.BusStopModel
import com.example.mobilnibus.viemodels.BusStopViewModel
import com.example.mobilnibus.viemodels.UserViewModel

@Composable
fun ViewBusStopScreen(
    busStopViewModel: BusStopViewModel,
    userViewModel: UserViewModel,
    navigateToMap: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        BusStopCard(busStop = busStopViewModel.busStop)
        Row(
            modifier = Modifier.fillMaxWidth().padding(6.dp,8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(modifier = Modifier.padding(12.dp),
                onClick = {
                    busStopViewModel.resetCurrentBusStop()
                    navigateToMap()
                }) {
                Text(text = "Back")
            }
            if(userViewModel.currentUserModel.role=="admin")
            {
                Button(modifier = Modifier.padding(12.dp),
                    onClick = {
                        busStopViewModel.deleteBusStop(busStopViewModel.busStop.id)
                        navigateToMap()
                    }) {
                    Text(text = "Delete Bus stop")
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun BusStopCard(busStop: BusStopModel) {
    ElevatedCard(modifier = Modifier
        .padding(12.dp)
        .fillMaxWidth()) {
        Column() {
            Text(
                modifier = Modifier.padding(6.dp),
                text = busStop.name,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
            Row(modifier = Modifier.padding(6.dp)) {
                Text(text = "Latitude: ")
                Text(text = String.format("%.6f", busStop.lat))
            }
            Row(modifier = Modifier.padding(6.dp)) {
                Text(text = "Longitude: ")
                Text(text = String.format("%.6f", busStop.lng))

            }
        }

    }
}