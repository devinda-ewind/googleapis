package com.emedia.googleapis.google_map

import android.location.Location

interface GoogleLocationCallback {

    fun onLocationChanged(location: Location?)

    fun onProviderEnabled(s: String?)
}