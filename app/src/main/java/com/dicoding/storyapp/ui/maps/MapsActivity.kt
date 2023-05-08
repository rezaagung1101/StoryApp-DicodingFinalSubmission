package com.dicoding.storyapp.ui.maps

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.lib.story.Story
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.databinding.CustomCardViewStoryBinding
import com.dicoding.storyapp.ui.detail.DetailActivity
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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val mapsViewModel: MapsViewModel by viewModels()
    val indonesianCoordinate = LatLng(-2.548926, 118.0148634)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mapsViewModel.storyList.observe(this, {
            setStoryList(it)
        })
        mMap.setInfoWindowAdapter(this@MapsActivity)
        mMap.setOnInfoWindowClickListener { marker ->
            val story: Story = marker.tag as Story
            routeToDetailStory(story)
        }
        getMyLastLocation()
        mapsViewModel.loadStoryLocationData(
            this
        )
        mapsViewModel.coordinateLocation.observe(this, {
            CameraUpdateFactory.newLatLngZoom(it, 3f)
        })
    }

    private fun setStoryList(storyList: List<Story>) {
        for (story in storyList) {
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        story.lat ?: 0.0,
                        story.lon ?: 0.0
                    )
                )
            )?.tag = story
        }
    }

    private fun routeToDetailStory(story: Story) {
        val intentToDetail = Intent(this, DetailActivity::class.java)
        intentToDetail.putExtra("NAME", story.name)
        intentToDetail.putExtra("CREATEDAT", Helper.getUploadStoryTime(story.createdAt))
        intentToDetail.putExtra("DESCRIPTION", story.description)
        intentToDetail.putExtra("PHOTOURL", story.photoUrl)
        intentToDetail.putExtra("LATITUDE", story.lat)
        intentToDetail.putExtra("LONGITUDE", story.lon)
        intentToDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intentToDetail)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[permission.ACCESS_COARSE_LOCATION] ?: false -> {
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

    private fun getMyLastLocation() {
        if (checkPermission(permission.ACCESS_FINE_LOCATION) &&
            checkPermission(permission.ACCESS_COARSE_LOCATION)
        ) {
            mMap.isMyLocationEnabled = true
            mapsViewModel.isLoading.observe(this, {
                showLoading(it)
            })
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    mapsViewModel.coordinateLocation.postValue(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                } else {
                    mapsViewModel.coordinateLocation.postValue(indonesianCoordinate)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    permission.ACCESS_FINE_LOCATION,
                    permission.ACCESS_COARSE_LOCATION
                )
            )
        }

    }

    override fun getInfoContents(p0: Marker): View? {
        //do nothing
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val cardView = CustomCardViewStoryBinding.inflate(LayoutInflater.from(this))
        val story: Story = marker.tag as Story
        cardView.tvItemName.text = story.name
        cardView.imgItemPhoto.setImageDrawable(resources.getDrawable(R.drawable.avatar))
        cardView.imgItemStory.setImageBitmap(Helper.bitmapFromURL(this, story.photoUrl))
        cardView.tvDescription.text = story.description
        return cardView.root
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

}