package com.emedia.googleapis.google_place

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.concurrent.TimeUnit

class GooglePlacePickerAdapter(
    context: Context,
    private val mGeoDataClient: PlacesClient,
    val bounds: LatLngBounds
) : ArrayAdapter<AutocompletePrediction>(
    context,
    android.R.layout.simple_expandable_list_item_2,
    android.R.id.text1
), Filterable {

    companion object {
        private const val TAG = "PlaceAutocompleteAdapter"
        private val STYLE_BOLD: CharacterStyle = StyleSpan(Typeface.BOLD)
    }

    /**
     * Current results returned by this adapter.
     */
    private var mResultList: ArrayList<AutocompletePrediction> = arrayListOf()

    /**
     * The bounds used for Places Geo Data autocomplete API requests.
     */
    private var mBounds: LatLngBounds? = null


    /**
     * Initializes with a resource for text rows and autocomplete query bounds.
     *
     * @see ArrayAdapter#ArrayAdapter(Context, int)
     */

    /**
     * Sets the bounds for all subsequent queries.
     */
    fun setBounds(bounds: LatLngBounds) {
        mBounds = bounds
    }

    override fun getCount(): Int {
        return mResultList.size
    }

    override fun getItem(position: Int): AutocompletePrediction? {
        return mResultList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = super.getView(position, convertView, parent)

        // Sets the primary and secondary text for a row.
        // Note that getPrimaryText() and getSecondaryText() return a CharSequence that may contain
        // styling based on the given CharacterStyle.

        val item = getItem(position)

        val textView1 = row.findViewById<TextView>(android.R.id.text1)
        val textView2 = row.findViewById<TextView>(android.R.id.text2)
        textView1.textSize = 12f
        textView2.textSize = 9f
        textView1.setTextColor(Color.BLACK)
        textView2.setTextColor(Color.BLACK)
        textView1.text = item?.getPrimaryText(STYLE_BOLD)
        textView2.text = item?.getSecondaryText(STYLE_BOLD)

        return row
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()

                // We need a separate list to store the results, since
                // this is run asynchronously.
                var filterData = ArrayList<AutocompletePrediction>()

                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    filterData = ArrayList(getAutocomplete(constraint))
                }

                results.values = filterData
                results.count = filterData.size

                return results
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    mResultList = results.values as (ArrayList<AutocompletePrediction>)
                    notifyDataSetChanged()
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return if (resultValue is AutocompletePrediction) {
                    resultValue.getFullText(null)
                } else {
                    super.convertResultToString(resultValue)
                }
            }
        }
    }

    /**
     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
     * Results are returned as frozen AutocompletePrediction objects, ready to be cached.
     * Returns an empty list if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.
     *
     * @param constraint Autocomplete query string
     * @return Results from the autocomplete API or null if the query was not successful.
     * /** @see GeoDataClient#getAutocompletePredictions(String, LatLngBounds, AutocompleteFilter)
     * @see
    **/**/
    private fun getAutocomplete(constraint: CharSequence): List<AutocompletePrediction> {

        //Log.i(TAG, "Starting autocomplete query for: " + constraint);

        // Submit the query to the autocomplete API and retrieve a PendingResult that will
        // contain the results when the query completes.

        val requestBuilder =
            FindAutocompletePredictionsRequest.builder()
                .setQuery(constraint.toString())

        val results = mGeoDataClient.findAutocompletePredictions(requestBuilder.build())

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return try {
            val autocompletePredictions = results.result
            // Freeze the results immutable representation that can be stored safely.
            autocompletePredictions?.autocompletePredictions ?: emptyList()
        } catch (e: RuntimeExecutionException) {
            // If the query did not complete successfully return null
            //            Toast.makeText(getContext(), "Error contacting API: " + e.toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Network connection not available.", Toast.LENGTH_SHORT)
                .show()
            //Log.e(TAG, "Error getting autocomplete prediction API call", e);
            emptyList()
        }
    }
}