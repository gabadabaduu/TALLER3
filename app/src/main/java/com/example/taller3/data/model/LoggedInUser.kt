package com.example.taller3.data.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String
){
    companion object {
        fun fromFirebaseDoc(doc: DocumentSnapshot): LoggedInUser {
            return LoggedInUser(
                doc.getString("userId") ?: "",
                doc.getString("displayName") ?: ""
            )
        }
    }

}
