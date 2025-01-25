package com.example.mobilnibus.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserModel (
    @DocumentId val id: String = "",
    @ServerTimestamp val createdAt: Date = Date(),
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val role: String = "user"
)