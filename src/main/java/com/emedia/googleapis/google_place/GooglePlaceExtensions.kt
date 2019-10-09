package com.emedia.googleapis.google_place

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 156

fun Activity.startPlacePicker() {
    val intent = Autocomplete.IntentBuilder(
        AutocompleteActivityMode.FULLSCREEN,
        Place.Field.values().toMutableList()
    )
        .build(this)
    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
}

fun Fragment.startPlacePicker() {
    val intent = Autocomplete.IntentBuilder(
        AutocompleteActivityMode.FULLSCREEN,
        Place.Field.values().toMutableList()
    )
        .build(context!!)
    this.startActivityForResult(
        intent,
        PLACE_AUTOCOMPLETE_REQUEST_CODE
    )
}

fun Context.getLocationFromResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
): GoogleLocation? {
    if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
            if (place?.latLng != null && place.latLng?.latitude != null && place.latLng?.longitude != null) {
                return GoogleLocation(
                    place.address,
                    place.latLng?.latitude,
                    place.latLng?.longitude
                )
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = data?.let { Autocomplete.getStatusFromIntent(it) }
            //status?.statusMessage?.showToast(this)
        }
    }
    return null
}
