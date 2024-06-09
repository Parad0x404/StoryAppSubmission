package com.dicoding.picodiploma.loginwithanimation.view.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.addstory.AddStoryViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val apiService = ApiConfig().getApiService(null)
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this, apiService)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()

        viewModel.getSession().observe(this@MapsActivity) { user ->
            addManyMarker(user.token)
        }
    }

    private fun addManyMarker(token: String) {
        lifecycleScope.launch {
            try {
                // dua val di bawah baru
                val iconBitmap = getBitmapFromDrawableResource(R.drawable.marker)
                val resizedBitmap = Bitmap.createScaledBitmap(iconBitmap, 40.dpToPx(), 40.dpToPx(), false)
                val apiService = ApiConfig().getApiService(token)
                val successResponse = apiService.getStoriesWithLocation(1).listStory
                successResponse?.forEach { data ->
                    val latLng = LatLng(data?.lat ?: 0.0, data?.lon ?: 0.0)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(data?.name)
                            .snippet(data?.description)
                            .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))
                    )
                }
                val indonesiaSouthWest = LatLng(-11.0, 95.0)
                val indonesiaNorthEast = LatLng(6.0, 141.0)
                val indonesiaBounds = LatLngBounds(indonesiaSouthWest, indonesiaNorthEast)

                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        indonesiaBounds,
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        10
                    )
                )
            } catch (e: Exception) {
                showToast("$e")
            }
        }
    }

    // ubah jadi bitmap
    private fun getBitmapFromDrawableResource(resourceId: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inMutable = true // Memastikan bitmap dapat diubah
        return BitmapFactory.decodeResource(resources, resourceId, options)
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                showToast(getString(R.string.no_permission_gps))
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, getString(R.string.style_parsing_failed))
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.can_t_find_style_error), exception)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MapsActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MapsActivity"
    }

}