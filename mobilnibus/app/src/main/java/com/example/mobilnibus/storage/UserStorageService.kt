package com.example.mobilnibus.storage

import com.example.mobilnibus.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class UserStorageService(private val firestore: FirebaseFirestore){

    suspend fun getUser(uid: String): UserModel {
        return firestore.collection(USER_COLLECTION).whereEqualTo("uid", uid)
            .get().await().first().toObject<UserModel>()
    }

    suspend fun save(userModel: UserModel): String {
        val updatedUser = userModel.copy()
        return firestore.collection(USER_COLLECTION).add(updatedUser).await().id
    }

    companion object {
        private const val CREATED_AT_FIELD = "createdAt"
        private const val USER_COLLECTION = "users"
    }
}