package com.jabg.modulo6p2.data.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import androidx.lifecycle.LiveData

class NetworkConnection(
    private val context: Context
) : LiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    private val networkConnectionCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }
    }

    private var isCallbackRegistered = false

    override fun onActive() {
        super.onActive()
        updateNetworkConnection()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!isCallbackRegistered) {
                connectivityManager.registerDefaultNetworkCallback(networkConnectionCallback)
                isCallbackRegistered = true
            }
        } else {

            context.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    override fun onInactive() {
        super.onInactive()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (isCallbackRegistered) {
                connectivityManager.unregisterNetworkCallback(networkConnectionCallback)
                isCallbackRegistered = false
            }
        } else {

            context.unregisterReceiver(networkReceiver)
        }
    }

    private fun updateNetworkConnection() {
        val networkConnection: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(networkConnection?.isConnected == true)
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateNetworkConnection()
        }
    }
}