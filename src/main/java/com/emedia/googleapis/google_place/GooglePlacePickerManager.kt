package com.emedia.googleapis.google_place

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.RuntimeRemoteException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.io.IOException

class GooglePlacePickerManager(private val mContext: Context) {

    private lateinit var mGeoDataClient: PlacesClient
    private lateinit var mAutoCompleteAdapter: GooglePlacePickerAdapter

    private var place: Place? = null
    private var pickerCallback: GooglePlacePickerCallback? = null
    var latLngBounds: LatLngBounds = BOUNDS_GREATER_SYDNEY

    fun initialPlace(
        searchView: AutoCompleteTextView,
        callback: GooglePlacePickerCallback
    ) {
        this.pickerCallback = callback
        mGeoDataClient = Places.createClient(mContext)
        mAutoCompleteAdapter = GooglePlacePickerAdapter(mContext, mGeoDataClient, latLngBounds)
        searchView.setAdapter<GooglePlacePickerAdapter>(mAutoCompleteAdapter)

        searchView.setOnItemClickListener { parent, view, position, id ->
            val item = mAutoCompleteAdapter.getItem(position)
            val placeID = item?.placeId
            placeID?.let { getSearchingPlace(it) }
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Create LatLng Boundary
     */
    fun GenLatLongBounds(center: LatLng): LatLngBounds {
        val radiusDegrees = 1.0
        val northEast = LatLng(center.latitude + radiusDegrees, center.longitude + radiusDegrees)
        val southWest = LatLng(center.latitude - radiusDegrees, center.longitude - radiusDegrees)
        return LatLngBounds.builder().include(northEast).include(southWest).build()
    }

    /**
     * Get Address from Latlng
     */
    fun initialGeoCode(latLng: LatLng) {
        val location = GoogleLocation()
        location.latitude = latLng.latitude
        location.longtude = latLng.longitude
        val coder = Geocoder(mContext)
        val addresses: List<Address>?
        try {
            addresses = coder.getFromLocation(latLng.latitude, latLng.longitude, 3)
            if (!addresses.isNullOrEmpty()) {
                for (address in addresses) {
                    if (address.getAddressLine(0) != null) {
                        location.address = address.getAddressLine(0)
                        pickerCallback?.googleAddressCallback(location)
                        break
                    }
                }
            } else {
                pickerCallback?.googleAddressCallback(location)
            }
        } catch (ex: IOException) {
            pickerCallback?.googleAddressCallback(location)
        }

    }

    fun publishAddress(place: Place) {
        val location = GoogleLocation()
        location.latitude = place.latLng?.latitude
        location.longtude = place.latLng?.longitude
        location.address = place.address
        pickerCallback?.googleAddressCallback(location)
    }

    /**
     * Get Place from address
     */
    fun getSearchingPlace(placeId: String) {

        val fetchPlaceRequest = FetchPlaceRequest.newInstance(
            placeId, Place.Field.values().toList()
        )

        val placeResult = mGeoDataClient.fetchPlace(fetchPlaceRequest)
        placeResult.addOnCompleteListener { task ->
            try {
                val places = task.result

                // Get the Place object from the buffer.
                if (places == null) {
                    Toast.makeText(
                        mContext, "Please try again, Couldn't find a location",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnCompleteListener
                }

                place = places.place

                if (place != null) {
                    pickerCallback!!.googlePlacePickerCallback(place!!)
                }
            } catch (e: RuntimeRemoteException) {
                // Request did not complete successfully
                Toast.makeText(
                    mContext,
                    "Please try again, Couldn't find a location",
                    Toast.LENGTH_SHORT
                )
                    .show()
                //showExpandMessageError(btnFavourites, "Please try again, Couldn't find a location");
                return@addOnCompleteListener
            }
        }
    }

    companion object {

        val BOUNDS_GREATER_SYDNEY = LatLngBounds(
            LatLng(-34.041458, 150.790100), LatLng(-33.682247, 151.383362)
        )
    }
}