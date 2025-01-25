package com.example.mobilnibus.storage

import com.example.mobilnibus.model.BusStopModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class BusStopStorageService(private val firestore: FirebaseFirestore){

    val busstops: Flow<List<BusStopModel>> get() = firestore
                .collection(BUSSTOP_COLLECTION)
                .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING)
                .dataObjects()

    suspend fun save(busStopModel: BusStopModel): String {
        val updatedBusStop = busStopModel.copy()
        return firestore.collection(BUSSTOP_COLLECTION).add(updatedBusStop).await().id
    }

    suspend fun delete(busStopId: String) {
        firestore.collection(BUSSTOP_COLLECTION).document(busStopId).delete().await()
    }

    companion object {
        private const val CREATED_AT_FIELD = "createdAt"
        private const val BUSSTOP_COLLECTION = "busstops"
    }
}