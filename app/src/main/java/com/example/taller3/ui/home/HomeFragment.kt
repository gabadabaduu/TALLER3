package com.example.taller3.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taller3.MapsActivity
import com.example.taller3.data.model.LoggedInUser
import com.example.taller3.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val userList = mutableListOf<LoggedInUser>()

    private val db = Firebase.firestore

    private lateinit var loggedInUserId: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("HomeFragment", "Fetching users...")

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        /* Mock Data
        var userList = mutableListOf<LoggedInUser>()
        val mockUser = LoggedInUser("oZFEZbmVj6ZKPiBcVoEmxKfFw4R2", "John Doe", "John Doe", 4.6483,-74.0698, "https://media.istockphoto.com/id/529981123/photo/snuffling-dog.jpg?s=612x612&w=0&k=20&c=CN7cZIy6_f8xEVF17EJMEwmh00_InQblMcdIeSLXpAw=", true)
        userList.add(mockUser)*/

        Log.i("HomeFragment", "Fetching users...")
        // Initialize Adapter
        val userAdapter = UserAdapter(userList, object: UserAdapter.OnItemClickListener {
            override fun onItemClick(user: LoggedInUser) {
                Log.i("HomeFragment", "User clicked: $user")
                val intent = Intent(activity, MapsActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
            }
        })

        // Bind Adapter to RecyclerView
        recyclerView.adapter = userAdapter

        loggedInUserId = Firebase.auth.currentUser?.uid.toString()

        // Fetch user data
        fetchUsers(loggedInUserId!!)
            { userList ->
            // Initialize Adapter
            val userAdapter = UserAdapter(userList, object: UserAdapter.OnItemClickListener {
                override fun onItemClick(user: LoggedInUser) {
                    val intent = Intent(activity, MapsActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                }
            })

            // Bind Adapter to RecyclerView
            recyclerView.adapter = userAdapter
        }


        return root
    }

    private fun fetchUsers(loggedInUserId: String, onUsersFetched: (List<LoggedInUser>) -> Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.mapNotNull { document ->
                    LoggedInUser.fromFirebaseDoc(document)
                }.filter { user ->
                    user.userId != loggedInUserId
                }
                onUsersFetched(users)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}