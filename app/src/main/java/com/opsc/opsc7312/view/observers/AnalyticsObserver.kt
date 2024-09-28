package com.opsc.opsc7312.view.observers

import android.util.Log
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import com.opsc.opsc7312.view.adapter.AnalyticsAdapter
import com.opsc.opsc7312.view.fragment.AnalyticsFragment


// This class was adapted from stackoverflow
// https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
// Kevin Robatel
// https://stackoverflow.com/users/244702/kevin-robatel

// This class observes changes to a list of Analytics and updates the UI accordingly.
class AnalyticsObserver(
    private val fragment: AnalyticsFragment,
    private val adapter: AnalyticsAdapter
): Observer<AnalyticsResponse> {

    // Method called when the observed data changes
    override fun onChanged(value: AnalyticsResponse) {
        // Update the data in the CategoryListAdapter
        adapter.updateGraph(value)

        // Update the data in AnalyticsFragment class
        fragment.updateAnalyticData(value)

        Log.d("Analytics", "Analytics retrieved: $value")
    }
}