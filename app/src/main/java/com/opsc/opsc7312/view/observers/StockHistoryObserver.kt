package com.opsc.opsc7312.view.observers

import android.util.Log
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.data.model.StockHistory
import com.opsc.opsc7312.view.adapter.StockHistoryAdapter

class StockHistoryObserver(private val adapter: StockHistoryAdapter): Observer<List<StockHistory>> {

    // Invoked when the observed list of goals changes.
    override fun onChanged(value: List<StockHistory>) {
        // Refresh the GoalAdapter with the latest goals.
        adapter.updateGraph(value)

        // Log the number of goals retrieved for debugging purposes.
        Log.d("Stock history", "Stocks retrieved: ${value.size}\n $value")
    }
}