package com.example.mobilnibus

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilnibus.viemodels.FormViewModel
import com.example.mobilnibus.location.LocationService
import com.example.mobilnibus.screens.AddBusStopScreen
import com.example.mobilnibus.screens.MapScreen
import com.example.mobilnibus.screens.SettingsScreen
import com.example.mobilnibus.screens.StartScreen
import com.example.mobilnibus.screens.ViewBusStopScreen
import com.example.mobilnibus.storage.BusMarkerStorageService
import com.example.mobilnibus.storage.BusStopStorageService
import com.example.mobilnibus.storage.UserStorageService
import com.example.mobilnibus.viemodels.BusMarkerViewModel
import com.example.mobilnibus.viemodels.BusMarkerViewModelFactory
import com.example.mobilnibus.viemodels.BusStopViewModel
import com.example.mobilnibus.viemodels.BusStopViewModelFactory
import com.example.mobilnibus.viemodels.EditBusStopViewModel
import com.example.mobilnibus.viemodels.UserViewModel
import com.example.mobilnibus.viemodels.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserStorageService((application as MobilniBusApp).db))
    }

    private val busStopViewModel: BusStopViewModel by viewModels {
        BusStopViewModelFactory(BusStopStorageService((application as MobilniBusApp).db))
    }

    private val busMarkerViewModel: BusMarkerViewModel by viewModels {
        BusMarkerViewModelFactory(BusMarkerStorageService((application as MobilniBusApp).db))
    }

    private fun startLocService() {
        Intent(this, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }

    private fun stopLocService() {
        Intent(this, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            startService(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        this.requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )

        auth = Firebase.auth

        setContent {
                Surface(modifier = Modifier.fillMaxSize())
                {
                    MobilniBusApp(auth, this,userViewModel,busStopViewModel,busMarkerViewModel,
                        svcStart = {
                            startLocService()
                        },
                        svcStop = {
                            stopLocService()
                        })
                }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userViewModel.getUser(currentUser.uid)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocService()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("this.moveTaskToBack(true)"))
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        this.moveTaskToBack(true)
    }
}

@Composable
fun MobilniBusApp(
    auth: FirebaseAuth,
    mainActivity: MainActivity,
    userViewModel: UserViewModel,
    busStopViewModel: BusStopViewModel,
    busMarkerViewModel: BusMarkerViewModel,
    svcStart:()->Unit,
    svcStop:()->Unit)
{
    val navController = rememberNavController()
    val formViewModel: FormViewModel = viewModel()
    val editBusStopViewModel: EditBusStopViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screens.StartScreen.name) {

        composable(Screens.StartScreen.name)
        {
            StartScreen(auth,mainActivity,formViewModel,userViewModel,
                startSvc = {svcStart()},
                navigateToMap = {navController.navigate(Screens.MapScreen.name)}
            )
        }

        composable(Screens.MapScreen.name)
        {
            val busStopsList = busStopViewModel.busStops.collectAsState(initial = listOf())
            MapScreen(auth,mainActivity,userViewModel,busStopViewModel,
                navigateToSettings = {navController.navigate(Screens.SettingsScreen.name)},
                navigateToViewBusStop = {navController.navigate(Screens.ViewBusStopScreen.name)},
                onMapLongClick = { latLng ->
                    editBusStopViewModel.setLatLng(latLng.latitude,latLng.longitude)
                    navController.navigate(Screens.AddBusStopScreen.name) },
                busMarkerViewModel=busMarkerViewModel,
                list = busStopsList.value)
        }

        composable(Screens.AddBusStopScreen.name)
        {
            AddBusStopScreen(editBusStopViewModel = editBusStopViewModel,
                busStopViewModel = busStopViewModel,
                navigateToMap = {navController.popBackStack()})
        }

        composable(Screens.ViewBusStopScreen.name)
        {
            ViewBusStopScreen(busStopViewModel = busStopViewModel,
                userViewModel=userViewModel,
                navigateToMap = {navController.popBackStack()})
        }

        composable(Screens.SettingsScreen.name)
        {
            SettingsScreen(auth,
                userViewModel,
                busMarkerViewModel,
                navigateToStart = {
                    navController.popBackStack(Screens.StartScreen.name,inclusive = false)
                },
                navigateBack = {
                    navController.popBackStack()
                },
                startSvc = {svcStart()},
                stopSvc = {svcStop()})
        }
    }
}

enum class Screens{
    StartScreen,
    MapScreen,
    AddBusStopScreen,
    SettingsScreen,
    ViewBusStopScreen
}