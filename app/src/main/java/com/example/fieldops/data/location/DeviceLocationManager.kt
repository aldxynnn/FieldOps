package com.example.fieldops.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat

class DeviceLocationManager(
    private val context: Context
) {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var locationListener: LocationListener? = null

    fun hasLocationPermission(): Boolean {
        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    fun isLocationAvailable(): Boolean {
        return try {
            locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            ) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        } catch (_: Exception) {
            false
        }
    }

    fun getCurrentLocation(
        onLocationReceived: (Location) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!hasLocationPermission()) {
            onError("Izin lokasi belum diberikan.")
            return
        }

        if (!isLocationAvailable()) {
            onError("GPS atau layanan lokasi sedang tidak aktif.")
            return
        }

        stopListening()

        val listener =
            object : LocationListener {

                override fun onLocationChanged(
                    location: Location
                ) {
                    onLocationReceived(location)
                    stopListening()
                }

                override fun onProviderDisabled(
                    provider: String
                ) {
                    onError(
                        "Layanan lokasi tidak tersedia."
                    )
                    stopListening()
                }
            }

        locationListener = listener

        try {
            val lastKnownLocation =
                getBestLastKnownLocation()

            if (lastKnownLocation != null) {
                onLocationReceived(lastKnownLocation)
                stopListening()
                return
            }

            val provider =
                when {
                    locationManager.isProviderEnabled(
                        LocationManager.GPS_PROVIDER
                    ) -> {
                        LocationManager.GPS_PROVIDER
                    }

                    locationManager.isProviderEnabled(
                        LocationManager.NETWORK_PROVIDER
                    ) -> {
                        LocationManager.NETWORK_PROVIDER
                    }

                    else -> {
                        null
                    }
                }

            if (provider == null) {
                onError(
                    "GPS atau layanan lokasi sedang tidak aktif."
                )
                stopListening()
                return
            }

            locationManager.requestLocationUpdates(
                provider,
                1_000L,
                1f,
                listener,
                Looper.getMainLooper()
            )

        } catch (_: SecurityException) {
            onError(
                "Aplikasi tidak memiliki izin untuk mengambil lokasi."
            )
            stopListening()

        } catch (_: Exception) {
            onError(
                "Lokasi perangkat tidak dapat diperoleh."
            )
            stopListening()
        }
    }

    private fun getBestLastKnownLocation(): Location? {
        var bestLocation: Location? = null

        try {
            val gpsLocation =
                locationManager.getLastKnownLocation(
                    LocationManager.GPS_PROVIDER
                )

            if (gpsLocation != null) {
                bestLocation = gpsLocation
            }

        } catch (_: SecurityException) {
            // Permission sudah diperiksa.
        } catch (_: Exception) {
            // Provider GPS tidak tersedia.
        }

        try {
            val networkLocation =
                locationManager.getLastKnownLocation(
                    LocationManager.NETWORK_PROVIDER
                )

            if (networkLocation != null) {
                if (
                    bestLocation == null ||
                    networkLocation.time > bestLocation.time
                ) {
                    bestLocation = networkLocation
                }
            }

        } catch (_: SecurityException) {
            // Permission sudah diperiksa.
        } catch (_: Exception) {
            // Provider network tidak tersedia.
        }

        return bestLocation
    }

    fun stopListening() {
        val listener = locationListener
            ?: return

        try {
            locationManager.removeUpdates(
                listener
            )
        } catch (_: SecurityException) {
            // Permission berubah saat listener dihentikan.
        } catch (_: Exception) {
            // Listener sudah tidak aktif.
        }

        locationListener = null
    }
}