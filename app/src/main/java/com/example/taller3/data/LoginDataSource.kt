package com.example.taller3.data

import com.example.taller3.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException

class LoginDataSource {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser("oZFEZbmVj6ZKPiBcVoEmxKfFw4R2", "John Doe", "John Doe", 4.6483, -74.0698, "https://media.istockphoto.com/id/529981123/photo/snuffling-dog.jpg?s=612x612&w=0&k=20&c=CN7cZIy6_f8xEVF17EJMEwmh00_InQblMcdIeSLXpAw=", true)
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        auth.signOut()
    }
}
