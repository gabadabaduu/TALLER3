package com.example.taller3.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot

data class LoggedInUser(
    val userId: String,
    val displayName: String,
    val email: String,
    val lat: Double,
    val long: Double,
    val profilePictureUrl: String,
    val status: Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(displayName)
        parcel.writeString(email)
        parcel.writeDouble(lat)
        parcel.writeDouble(long)
        parcel.writeString(profilePictureUrl)
        parcel.writeByte(if (status) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoggedInUser> {
        override fun createFromParcel(parcel: Parcel): LoggedInUser {
            return LoggedInUser(parcel)
        }

        override fun newArray(size: Int): Array<LoggedInUser?> {
            return arrayOfNulls(size)
        }

        fun fromFirebaseDoc(doc: DocumentSnapshot): LoggedInUser {
            return LoggedInUser(
                doc.getString("userId") ?: "",
                doc.getString("displayName") ?: "",
                doc.getString("email") ?: "",
                doc.getDouble("lat") ?: 0.0,
                doc.getDouble("long") ?: 0.0,
                doc.getString("profilePictureUrl") ?: "",
                doc.getBoolean("status") ?: false
            )
        }
    }
}

