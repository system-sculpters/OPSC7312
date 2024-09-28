package com.opsc.opsc7312.view.observers

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import com.opsc.opsc7312.view.custom.FilterBottomSheet
import com.opsc.opsc7312.view.custom.SortBottomSheet

// This class was adapted from stackoverflow
// https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
// Kevin Robatel
// https://stackoverflow.com/users/244702/kevin-robatel

///This class observes a list of Transaction objects and updates the UI components
//when the list changes.
class TransactionsObserver(
    private val sortBottomSheet: SortBottomSheet,     // Reference to the SortBottomSheet for sorting transactions
    private val filterBottomSheet: FilterBottomSheet,   // Reference to the FilterBottomSheet for filtering transactions
    private val adapter: TransactionAdapter?,            // Adapter for displaying transactions in a RecyclerView
    private val amount: TextView                         // TextView for displaying the total balance
) : Observer<List<Transaction>> {

    override fun onChanged(value: List<Transaction>) {
        // Update the data in the CategoryListAdapter
        adapter?.updateTransactions(value)
        sortBottomSheet.updateTransactions(value)
        filterBottomSheet.updateTransactions(value)
        updateBalance(value)
        // Update the categories in the associated ActivityFragment

        Log.d("Transaction", "transactions retrieved: ${value.size}\n $value")
    }

    private fun updateBalance(value: List<Transaction>) {
        var totalIncome = 0.0  // Initialize total income
        var totalExpense = 0.0  // Initialize total expense

        // Iterate over each transaction to calculate total income and expenses
        for (transaction in value) {
            if (transaction.type == AppConstants.TRANSACTIONTYPE.INCOME.name) {
                // Add to total income if the transaction type is income
                totalIncome += transaction.amount
            } else {
                // Add to total expenses if the transaction type is expense
                totalExpense += transaction.amount
            }
        }

        // Calculate the total balance
        val totalBalance = totalIncome - totalExpense

        // Update the amount TextView to display the formatted balance
        amount.text = "${AppConstants.formatAmount(totalBalance)} ZAR"
    }
}