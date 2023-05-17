package com.example.taller3

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.taller3.data.model.LoggedInUser

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.taller3.databinding.ActivityMapsBinding
import com.example.taller3.ui.login.LoginActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import org.json.JSONArray
import org.json.JSONObject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    // Variable to hold user data
    private var user: LoggedInUser? = null

    val auth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user data from intent
        user = intent.getParcelableExtra("user")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if(user != null)
        {
            startListeningForUserUpdates(user!!.userId)
        }

        checkLocationEnabled()
    }
    private fun checkLocationEnabled() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!locationEnabled) {
            // Location services are disabled, show a dialog to the user
            showLocationEnableDialog()
        } else {
            // Location services are enabled, get the last known location
            getLastKnownLocation()
        }
    }

    private fun showLocationEnableDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Services Disabled")
            .setMessage("Please enable location services to use this app.")
            .setPositiveButton("Enable") { _, _ ->
                // Open the system settings to enable location services
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle the situation when the user cancels the dialog
                // You can show a message or perform any necessary action
            }
            .show()
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker for the user
        if (user != null) {
            val userLatLng = LatLng(user!!.lat, user!!.long)
            val markerOptions = MarkerOptions()
                .position(userLatLng)
                .title(user!!.displayName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
        }

        getLastKnownLocation()
        addMarkersFromJson()
    }




    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        // Location permission has been granted, proceed with getting the last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("Mi ubicación"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                saveUserLocationInFirestore(auth.currentUser!!.uid, location)

            } else {
                // Handle the situation when the location is null (e.g., the user has disabled location services)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkLocationEnabled()
    }

    private fun readJsonFile(): String {
        val inputStream = assets.open("locations.json")
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun addMarkersFromJson() {
        val jsonString = readJsonFile()
        val jsonObject = JSONObject(jsonString)
        val locationsObject = jsonObject.getJSONObject("locations")

        val keys = locationsObject.keys()

        while (keys.hasNext()) {
            val key = keys.next()
            val locationObject = locationsObject.getJSONObject(key)
            val lat = locationObject.getDouble("latitude")
            val lng = locationObject.getDouble("longitude")
            val name = locationObject.getString("name")
            val position = LatLng(lat, lng)

            mMap.addMarker(MarkerOptions().position(position).title(name))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted, get the last known location
                getLastKnownLocation()
            } else {
                // Location permission has been denied, show a message to the user or redirect to app settings
            }
        }
    }

    private fun showLocationPermissionDeniedDialog() {
        // You can customize this dialog to show an explanation or redirect the user to app settings
        AlertDialog.Builder(this)
            .setTitle("Location Permission Denied")
            .setMessage("This app requires location permission to function properly.")
            .setPositiveButton("App Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun startListeningForUserUpdates(userId: String) {
        val docRef = db.collection("users").document(userId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")
                val lat = snapshot.getDouble("lat") ?: return@addSnapshotListener
                val long = snapshot.getDouble("long") ?: return@addSnapshotListener
                val displayName = snapshot.getString("displayName") ?: return@addSnapshotListener
                updateMarkerForUser(userId, LatLng(lat, long), displayName)
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }


    private fun updateMarkerForUser(userId: String, latLng: LatLng, displayName: String) {

    }



    private fun saveUserLocationInFirestore(userId: String, location: Location) {
        val userDoc = db.collection("users").document(userId)
        userDoc.update("lat", location.latitude, "long", location.longitude)
            .addOnSuccessListener { Log.d(TAG, "User location successfully updated!") }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating user location", e)
            }
    }

}