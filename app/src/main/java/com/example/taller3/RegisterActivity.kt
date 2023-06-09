package com.example.taller3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: FirebaseDatabase
    val auth = FirebaseAuth.getInstance()
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.RegistrarUsuario.setOnClickListener {
            val email = binding.emailedittext.text.toString().trim()
            val nombre = binding.NombreEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user?.uid ?: ""



                        // Store the user's name in the Firestore database
                        val userDocument = firestore.collection("users").document(userId)
                        userDocument.set(mapOf("displayName" to nombre, "email" to email, "lat" to 0, "long" to 0))

                        // Store the user's name in the Firebase database
                        val ref = database.getReference("users/$userId")
                        ref.child("displayName").setValue(nombre)


                        // Go back to the login activity
                        finish()
                    } else {
                        Toast.makeText(
                            baseContext, "Registration failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
