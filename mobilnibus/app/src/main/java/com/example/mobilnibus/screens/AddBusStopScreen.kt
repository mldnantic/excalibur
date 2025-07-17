package com.example.mobilnibus.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilnibus.viemodels.BusStopViewModel
import com.example.mobilnibus.viemodels.EditBusStopViewModel

@SuppressLint("DefaultLocale")
@Composable
fun AddBusStopScreen(editBusStopViewModel: EditBusStopViewModel = viewModel(),
                 busStopViewModel: BusStopViewModel,
                 navigateToMap: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val modifier = Modifier.padding(8.dp)
        OutlinedTextField(
            modifier = modifier,
            value = editBusStopViewModel.name,
            onValueChange = { editBusStopViewModel.name = it },
            label = { Text("Name") })
        Row {
            Text(
                text = "Latitude:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = String.format("%.6f", editBusStopViewModel.lat),
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row {
            Text(
                text = "Longitude:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = String.format("%.6f", editBusStopViewModel.lng),
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row {
            Button(modifier = modifier,
                onClick = {
                    navigateToMap()
                }) {
                Text(text = "Back")
            }
            Button(modifier = modifier, onClick = {
                busStopViewModel.addBusStop(
                    editBusStopViewModel.name,
                    editBusStopViewModel.lat,
                    editBusStopViewModel.lng
                )
                editBusStopViewModel.reset()
                navigateToMap()
            }) {
                Text(text = "Add Bus stop")
            }
        }
    }
}
