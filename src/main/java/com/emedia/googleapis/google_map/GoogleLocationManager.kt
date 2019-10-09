package com.emedia.googleapis.google_map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings

// The minimum distance to change Updates in meters
private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = (10 * 1000).toLong() // 10000 meters
// The minimum time between updates in milliseconds
private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 20).toLong() // 20 minute

class GoogleLocationManager(private val mContext: Context) : LocationListener {

    private var callback: GoogleLocationCallback? = null

    /** flag for GPS status  */
    private var canGetLocation = false

    private var location: Location? = null // location

    private var locationManager: LocationManager? = null
    private var bestProvider: String? = null

    init {
        initLocationManager()
    }

    fun setLocationManagerListener(listener: GoogleLocationCallback) {
        this.callback = listener
    }

    private fun initLocationManager() {
        locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager

        getProvider()

        // getting GPS status
        // flag for GPS status
        val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // getting network status
        // flag for network status
        val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGPSEnabled && !isNetworkEnabled) {
            showSettingsAlert()
        } else {
            canGetLocation = true
        }
    }

    /**
     * getLocation and request Location update
     * @return LastKnown Location
     */
    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        // First get location from Network Provider
        /*if (locationManager != null) {*/

        if (bestProvider == null) {
            getProvider()
        }
        location = locationManager?.getLastKnownLocation(bestProvider)

        callback?.onLocationChanged(location)

        return location
    }

    /**
     * get best provider
     */
    private fun getProvider() {
        val mCriteria = Criteria()
        mCriteria.accuracy = Criteria.ACCURACY_MEDIUM
        mCriteria.powerRequirement = Criteria.POWER_LOW
        mCriteria.isSpeedRequired = false
        mCriteria.isAltitudeRequired = false
        mCriteria.isBearingRequired = false
        mCriteria.isCostAllowed = false

        bestProvider = locationManager?.getBestProvider(mCriteria, true)
    }

    /**
     * remove location update
     */

    @SuppressLint("MissingPermission")
    fun startUsinGps() {
        locationManager?.requestLocationUpdates(
            bestProvider,
            MIN_TIME_BW_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
            this
        )
    }

    @SuppressLint("MissingPermission")
    fun stopUsingGPS() {
        callback = null
        locationManager?.removeUpdates(this)
    }

    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    private var dialog: AlertDialog? = null

    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
            dialog.cancel()
        }

        alertDialog.setCancelable(false)
        dialog = alertDialog.create()
        dialog?.show()
    }

    override fun onLocationChanged(location: Location) {
        callback?.onLocationChanged(location)
    }

    override fun onStatusChanged(
        s: String,
        i: Int,
        bundle: Bundle
    ) {

    }

    override fun onProviderEnabled(s: String) {
        dialog?.dismiss()
        canGetLocation = true
        callback?.onProviderEnabled(s)
    }

    override fun onProviderDisabled(s: String) {
        showSettingsAlert()
    }
}