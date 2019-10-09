package com.emedia.googleapis.google_map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

interface GoogleMapCallback {

    fun setOnMarker(marker: LatLng) {

    }

    fun dragMarker(marker: Marker) {

    }

    fun mapOnReadyCallback(googleMap: GoogleMap) {

    }

    fun setOnMarkerClickCallback(marker: Marker) {

    }

    fun onClickMapClick() {

    }

    fun onCameraMove() {

    }

    fun onClickPoli(latLngList: List<LatLng>) {

    }
}