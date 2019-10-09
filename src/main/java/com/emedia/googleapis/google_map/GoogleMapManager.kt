package com.emedia.googleapis.google_map

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.emedia.googleapis.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*

class GoogleMapManager : OnMapReadyCallback {

    companion object {
        const val DEFAULT_ZOOM = 12
    }

    private lateinit var mMapView: MapView
    private lateinit var mGoogleMap: GoogleMap
    private var addMarker = false
    private var setMarkerClick = false
    private var mGoogleMapCallback: GoogleMapCallback? = null

    fun setMapCallback(googleMapCallback: GoogleMapCallback) {
        this.mGoogleMapCallback = googleMapCallback
    }

    fun initializeMap(mapView: MapView) {
        mMapView = mapView
        mMapView.getMapAsync(this)
    }

    /**
     * Enable add marks when click on map
     *
     * @param addMarkListener //true or false
     */
    fun setAddMarkerWithClick(addMarkListener: Boolean) {
        addMarker = addMarkListener
    }

    /**
     * set marker onClick listener
     */
    fun setOnClickMarker(setMarkerClick: Boolean) {
        this.setMarkerClick = setMarkerClick
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.mGoogleMap = googleMap
        setCamera(LatLng(-37.8136, 144.9631), DEFAULT_ZOOM)
        mGoogleMap.uiSettings.isMyLocationButtonEnabled = false
        setMapClick(addMarker)

        if (setMarkerClick) {
            mGoogleMap.setOnMarkerClickListener { marker ->
                mGoogleMapCallback?.setOnMarkerClickCallback(marker)
                false
            }
        }

        mGoogleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {

            }

            override fun onMarkerDrag(marker: Marker) {

            }

            override fun onMarkerDragEnd(marker: Marker) {
                mGoogleMapCallback?.dragMarker(marker)
            }
        })

        mGoogleMap.setOnCameraIdleListener { mGoogleMapCallback?.onCameraMove() }
        mGoogleMapCallback?.mapOnReadyCallback(mGoogleMap)
    }

    private fun setMapClick(addMarker: Boolean) {
        mGoogleMap.setOnMapClickListener { latLng ->
            if (addMarker) {
                mGoogleMap.clear()
                setMarkers(latLng, R.drawable.pin, true)
                mGoogleMapCallback?.setOnMarker(latLng)
            } else {
                mGoogleMapCallback?.onClickMapClick()
            }
        }
    }

    /**
     * set camera position
     */
    fun setCamera(
        latLng: LatLng,
        zoom: Int
    ) {

        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(zoom.toFloat())
            .build()

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun setCameraMove(latLng: LatLng, zoom: Int) {

        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(zoom.toFloat())
            .build()

        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun setCameraMove(latLngList: List<LatLng>) {
        val latLngBounds = createBounds(latLngList)
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200))
    }

    fun createBounds(latLngList: List<LatLng>): LatLngBounds {

        val builder = LatLngBounds.Builder()

        for (latLng in latLngList) {
            builder.include(latLng)
        }

        return builder.build()
    }

    /**
     * set pin on Map
     */
    fun setMarkers(
        marker: LatLng,
        drawableMarker: Int,
        isDraggable: Boolean,
        name: String?,
        address: String?,
        count: Int
    ) {
        val markerOptions = MarkerOptions().apply {
            position(marker)
            icon(BitmapDescriptorFactory.fromResource(drawableMarker))
            name?.let { title(it) }
            address?.let { snippet(it) }
            draggable(isDraggable)
            zIndex(count.toFloat())
        }
        mGoogleMap.addMarker(markerOptions)
    }

    fun setMarkers(
        marker: LatLng,
        drawableMarker: Int,
        isDraggable: Boolean,
        tagObj: Any? = null
    ) {
        val markerOptions = MarkerOptions().apply {
            position(marker)
            icon(BitmapDescriptorFactory.fromResource(drawableMarker))
            draggable(isDraggable)
        }
        mGoogleMap.addMarker(markerOptions).apply {
            tag = tagObj
        }
    }

    /**
     * @ Specific
     * set pin as bitmap on map
     */
    fun setMarkers(
        marker: LatLng,
        bitmap: Bitmap
    ) {
        val markerOptions = MarkerOptions()
        markerOptions.position(marker)
        //markerOptions.zIndex(count.toFloat())
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        mGoogleMap.addMarker(markerOptions)
    }

    fun addPolyline(
        marker: LatLng,
        latLngList: List<LatLng>
    ) {

        if (latLngList.size <= 1) {
            return
        }

        val polygonOptions = PolylineOptions()
            .color(Color.BLACK)
            .width(2.0.toFloat())
        polygonOptions.add(marker)
        val prePoint = latLngList[latLngList.size - 2]
        polygonOptions.add(prePoint)
        mGoogleMap.addPolyline(polygonOptions)
    }

    fun addPolyline(
        latLngList: List<LatLng>
    ) {
        if (latLngList.size <= 1) {
            return
        }

        for ((count, latLng) in latLngList.withIndex()) {

            //setMarkers(latLng,R.drawable.ic_pin, true, count)
            if (count == 0) {
                continue
            }
            val polygonOptions = PolylineOptions()
                .color(Color.BLACK)
                .width(2.0.toFloat())
            polygonOptions.add(latLng)
            val prePoint = latLngList[count - 1]
            polygonOptions.add(prePoint)

            mGoogleMap.addPolyline(polygonOptions)
        }
    }

    fun addPolygon(
        latLngList: List<LatLng>,
        @ColorInt strokeColor: Int,
        @ColorInt fillColor: Int,
        @DrawableRes pin: Int,
        bitmap: Bitmap?
    ) {
        val rectOptions = PolygonOptions()
        rectOptions.addAll(latLngList)
        rectOptions.strokeColor(strokeColor)
        rectOptions.fillColor(fillColor)
        rectOptions.clickable(true)
        rectOptions.strokeWidth(3.0.toFloat())
        mGoogleMap.addPolygon(rectOptions)
        for ((count, latLng) in latLngList.withIndex()) {
            setMarkers(latLng, pin, true, null, null, count)
        }
        if (bitmap != null)
            setMarkers(GenLatLongBounds(latLngList).center, bitmap)
    }

    fun setClickOnPoli() {
        mGoogleMap.setOnPolygonClickListener { polygon ->
            polygon.fillColor = Color.RED
            mGoogleMapCallback?.onClickPoli(polygon.points)
        }

        mGoogleMap.setOnPolylineClickListener { polyline ->
            mGoogleMapCallback?.onClickPoli(polyline.points)
        }
    }

    fun GenLatLongBounds(center: LatLng): LatLngBounds {
        val radiusDegrees = 1.0
        val northEast = LatLng(center.latitude + radiusDegrees, center.longitude + radiusDegrees)
        val southWest = LatLng(center.latitude - radiusDegrees, center.longitude - radiusDegrees)
        return LatLngBounds.builder()
            .include(northEast)
            .include(southWest)
            .build()
    }

    fun GenLatLongBounds(latLngList: List<LatLng>): LatLngBounds {
        val latLngBounds = LatLngBounds.builder()
        for (latLng in latLngList) {
            latLngBounds.include(latLng)
        }
        return latLngBounds.build()
    }

}