package com.example.taller3.data.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize



@Parcelize
data class LoggedInUser(
    val userId: String,
    val displayName: String,
    val email: String,
    val lat: Double,
    val long: Double,
    val profilePictureUrl: String,
    val status: Boolean
) : Parcelable {
    companion object {
        fun fromFirebaseDoc(doc: DocumentSnapshot): LoggedInUser {
            val user = LoggedInUser(
                doc.id,
                doc.getString("displayName") ?: "",
                doc.getString("email") ?: "",
                doc.getDouble("lat") ?: 0.0,
                doc.getDouble("long") ?: 0.0,
                doc.getString("profilePictureUrl") ?: "",
                doc.getBoolean("status") ?: false
            )
            Log.i("LoggedInUser", "Created user from Firebase document: $user")
            return user
        }
    }
}

