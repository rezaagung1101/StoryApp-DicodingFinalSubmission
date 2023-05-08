package com.dicoding.storyapp.ui.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.databinding.ActivityPickLocationBinding
import com.dicoding.storyapp.databinding.CustomInfoLocationBinding
import com.dicoding.storyapp.ui.story.StoryViewModel
import com.dicoding.storyapp.utils.Helper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter  {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityPickLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val storyViewModel: StoryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.btnAddLocation.setOnClickListener {
            if (storyViewModel.isUsingLocation.value == true) {
                val intent = Intent()
                intent.putExtra("USINGLOCATION", storyViewModel.isUsingLocation.value)
                intent.putExtra("LATITUDE", storyViewModel.latitude.value)
                intent.putExtra("LONGITUDE", storyViewModel.longitude.value)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(
                    this@PickLocationActivity,
                    "Using Location False",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mMap.setInfoWindowAdapter(this)
        mMap.setOnInfoWindowClickListener { marker ->
            setLocation(marker.position.latitude, marker.position.longitude)
            marker.hideInfoWindow()
        }
        mMap.setOnMapClickListener {
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.latitude,
                            it.longitude
                        )
                    )
            )?.showInfoWindow()
        }
        mMap.setOnPoiClickListener {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.latLng.latitude,
                            it.latLng.longitude
                        )
                    )
            )?.showInfoWindow()
        }
        getMyLastLocation()
    }

    private fun setLocation(latitude: Double, longitude: Double) {
        storyViewModel.isUsingLocation.postValue(true)
        storyViewModel.latitude.postValue(latitude)
        storyViewModel.longitude.postValue(longitude)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(startLocation)
                .title(resources.getString(R.string.current_location))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    showStartMarker(location)
                    setLocation(location.latitude, location.longitude)
                } else {
                    Toast.makeText(
                        this@PickLocationActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val addressLayout = CustomInfoLocationBinding.inflate(LayoutInflater.from(this))
        addressLayout.tvAddress.text = Helper.parseAddress(
            this,
            marker.position.latitude, marker.position.longitude
        )
        return addressLayout.root
    }
}