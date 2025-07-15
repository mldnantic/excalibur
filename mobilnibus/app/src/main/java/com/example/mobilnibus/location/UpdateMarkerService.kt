package com.example.mobilnibus.location

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.mobilnibus.MobilniBusApp
import com.example.mobilnibus.storage.BusMarkerStorageService
import com.example.mobilnibus.viemodels.BusMarkerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class UpdateMarkerService: Service() {
    private lateinit var app: MobilniBusApp
    private lateinit var repo: BusMarkerStorageService
    private lateinit var job: Job
    private var shouldWriteLogs = false;

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

    private fun initRepository() {
        repo = BusMarkerStorageService(app.db)
    }

    private fun start() {
        initRepository()
        shouldWriteLogs = true
        job = doWork()
    }

    private fun stop() {
        shouldWriteLogs = false
        job.cancel()
    }

    private fun doWork() : Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (shouldWriteLogs) {
                delay(2000L)
                repo.save("123","34B",LocationService.latitude,LocationService.longitude)
            }
        }
    }
}