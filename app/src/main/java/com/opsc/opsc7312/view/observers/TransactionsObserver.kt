package com.opsc.opsc7312.view.observers

import android.util.Log
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.view.adapter.TransactionAdapter

class TransactionsObserver(private val adapter: TransactionAdapter?,) :
    Observer<List<Transaction>> {
    // This class was adapted from stackoverflow
    // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
    // Kevin Robatel
    // https://stackoverflow.com/users/244702/kevin-robatel

    // Method called when the observed data changes
    override fun onChanged(value: List<Transaction>) {
        // Update the data in the CategoryListAdapter
        adapter?.updateTransactions(value)
        setUpData(value)

        // Update the categories in the associated ActivityFragment

        Log.d("Transaction", "transactions retrieved: ${value.size}\n $value")
    }

    private fun setUpData(transactionList: List<Transaction>){

    }
}