package com.example.fieldops.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkConnectivityManager(
    context: Context
) {

    private val connectivityManager =
        context.applicationContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

    private val _isOnline =
        MutableStateFlow(
            checkInitialConnection()
        )

    val isOnline: StateFlow<Boolean> =
        _isOnline.asStateFlow()

    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(
                network: Network
            ) {
                updateConnectionState()
            }

            override fun onLost(
                network: Network
            ) {
                updateConnectionState()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                updateConnectionState()
            }
        }

    init {
        try {
            connectivityManager.registerDefaultNetworkCallback(
                networkCallback
            )
        } catch (
            exception: Exception
        ) {
            _isOnline.value =
                checkInitialConnection()
        }
    }

    private fun updateConnectionState() {

        _isOnline.value =
            checkInitialConnection()
    }

    private fun checkInitialConnection(): Boolean {

        val network =
            connectivityManager.activeNetwork
                ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(
                network
            )
                ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        ) &&
                capabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
    }

    fun unregister() {

        try {
            connectivityManager.unregisterNetworkCallback(
                networkCallback
            )
        } catch (
            exception: Exception
        ) {
            // Callback sudah tidak terdaftar.
        }
    }

}