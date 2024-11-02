package com.opsc.opsc7312.view.observers

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.view.adapter.GoalAdapter
import com.opsc.opsc7312.view.adapter.StockAdapter
import com.opsc.opsc7312.view.fragment.StocksFragment

class StockObserver (
    private val adapter: StockAdapter,
    private val fragment: StocksFragment
) : Observer<List<Stock>> {

    // Invoked when the observed list of goals changes.
    override fun onChanged(value: List<Stock>) {
        // Refresh the GoalAdapter with the latest goals.
        adapter.updateStocks(value)

        // Call the method to update the displayed progress of the goals.
        fragment.updateStocks(value)

        // Log the number of goals retrieved for debugging purposes.
        Log.d("Category", "Stocks retrieved: ${value.size}\n $value")
    }
}