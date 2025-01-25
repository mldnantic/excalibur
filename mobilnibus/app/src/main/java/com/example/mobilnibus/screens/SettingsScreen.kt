package com.example.mobilnibus.screens

import android.util.DisplayMetrics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilnibus.location.LocationService
import com.example.mobilnibus.viemodels.BusMarkerViewModel
import com.example.mobilnibus.viemodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
    auth: FirebaseAuth,
    userViewModel: UserViewModel,
    busMarkerViewModel: BusMarkerViewModel,
    navigateToStart:()->Unit,
    navigateBack:()->Unit,
    startSvc:()->Unit,
    stopSvc:()->Unit)
{
    Surface(color = Color.Black) {
        Surface(color = Color.White)
        {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center)
            {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f))
                {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        ElevatedButton(
                            modifier = Modifier
                                .height(72.dp)
                                .width(72.dp),
                            onClick = {})
                        {
                            Text(text="🆔")
                        }
                        Column {
                            auth.currentUser?.displayName?.let{
                                Text(text = it, fontSize=24.sp, modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(8.dp))
                            }

                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End)
                        {
                            ElevatedButton(
                                modifier = Modifier
                                    .height(72.dp),
                                onClick =
                                {
                                    stopSvc()
                                    if (auth.currentUser != null) {
                                        userViewModel.resetCurrentUser()
                                        auth.signOut()
                                        navigateToStart()
                                    }
                                }
                            )
                            {
                                Text("Log out")
                            }
                        }
                    }
                    if(userViewModel.currentUserModel.role!="admin") {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            if(userViewModel.currentUserModel.role=="bus")
                            {
                                Text(
                                    text = "Bus mode",
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            )
                            {
                                ElevatedButton(onClick = {
                                    startSvc()
                                })
                                {
                                    Text("Track location")
                                }
                                ElevatedButton(onClick = {
                                    stopSvc()
                                    if(userViewModel.currentUserModel.role=="bus")
                                    {
                                            busMarkerViewModel.deleteBusMarker(userViewModel.currentUserModel.id)
                                    }
                                })
                                {
                                    Text("Stop tracking")
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(8.dp)
                            )
                            {
                                Text(
                                    text = "First name:", fontSize = 16.sp,
                                    modifier = Modifier.padding(0.dp, 8.dp)
                                )
                                Text(
                                    text = userViewModel.currentUserModel.firstName,
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = "Last name:", fontSize = 16.sp,
                                    modifier = Modifier.padding(0.dp, 8.dp)
                                )
                                Text(
                                    text = userViewModel.currentUserModel.lastName,
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = "Phone:", fontSize = 16.sp,
                                    modifier = Modifier.padding(0.dp, 8.dp)
                                )
                                Text(text = userViewModel.currentUserModel.phone,
                                    fontSize = 24.sp)
                            }
                        }
                    }
                    else
                    {
                        Column()
                        {
                            Text(text = "Admin mode",
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Monospace)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(0.dp, 8.dp))
                {
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp, 0.dp),
                        onClick = {
                            navigateBack()
                        })
                    {
                        Text("◀️",fontSize=24.sp)
                    }
                }

            }
        }
    }
}