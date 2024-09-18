package com.opsc.opsc7312.view.observers

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import com.opsc.opsc7312.view.custom.FilterBottomSheet
import com.opsc.opsc7312.view.custom.SortBottomSheet

class TransactionsObserver(
    private val sortBottomSheet: SortBottomSheet,
    private val filterBottomSheet: FilterBottomSheet,
    private val adapter: TransactionAdapter?,
    private val amount: TextView,) :
    Observer<List<Transaction>> {
    // This class was adapted from stackoverflow
    // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
    // Kevin Robatel
    // https://stackoverflow.com/users/244702/kevin-robatel

    // Method called when the observed data changes
    override fun onChanged(value: List<Transaction>) {
        // Update the data in the CategoryListAdapter
        adapter?.updateTransactions(value)
        sortBottomSheet.updateTransactions(value)
        filterBottomSheet.updateTransactions(value)
        updateBalance(value)
        // Update the categories in the associated ActivityFragment

        Log.d("Transaction", "transactions retrieved: ${value.size}\n $value")
    }

    private fun updateBalance(value: List<Transaction>){
        var totalIncome = 0.0
        var totalExpense = 0.0

        for(transaction in value){
            if(transaction.type == AppConstants.TRANSACTIONTYPE.INCOME.name){
                totalIncome += transaction.amount
            } else{
                totalExpense += transaction.amount
            }
        }

        val totalBalance = totalIncome - totalExpense

        amount.text = "${AppConstants.formatAmount(totalBalance)} ZAR"
    }
}