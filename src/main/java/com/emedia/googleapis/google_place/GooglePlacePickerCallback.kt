package com.emedia.googleapis.google_place

import com.google.android.libraries.places.api.model.Place

abstract class GooglePlacePickerCallback {
    open fun googlePlacePickerCallback(place: Place) {}
    open fun googleAddressCallback(googleLocation: GoogleLocation) {}
}