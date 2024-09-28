package com.opsc.opsc7312.view.observers

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.view.adapter.TransactionAdapter

// This class was adapted from stackoverflow
// https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
// Kevin Robatel
// https://stackoverflow.com/users/244702/kevin-robatel

// This class observes changes to a list of Transactions  and updates the UI accordingly.
class HomeTransactionsObserver(
    private val adapter: TransactionAdapter?,       // Adapter for displaying the list of transactions
    private val amount: TextView,                   // TextView to display the total balance
    private val incomeAmount: TextView,             // TextView to display the total income amount
    private val expenseAmount: TextView              // TextView to display the total expense amount
) : Observer<List<Transaction>> {

    // Method // Invoked when the observed list of transactions changes.
    override fun onChanged(value: List<Transaction>) {
        // Refresh the TransactionAdapter with the latest transactions.
        adapter?.updateTransactions(value)

        // Call the method to calculate and update the amounts displayed in the TextViews.
        setUpData(value)

        // Log the number of transactions retrieved for debugging purposes.
        Log.d("Transaction", "transactions retrieved: ${value.size}\n $value")
    }

    // Calculates and updates the income, expense, and balance amounts based on the provided list of transactions.
    private fun setUpData(transactionList: List<Transaction>) {
        var totalIncome = 0.0  // Initialize total for income amounts.
        var totalExpense = 0.0  // Initialize total for expense amounts.

        // Create separate lists to hold income and expense transactions.
        val income: ArrayList<Transaction> = arrayListOf()
        val expenses: ArrayList<Transaction> = arrayListOf()

        // Iterate through each transaction to categorize them as income or expense.
        for (transaction in transactionList) {
            if (transaction.type == "INCOME") {
                income.add(transaction)           // Add to income list.
                totalIncome += transaction.amount  // Accumulate total income.
            } else {
                expenses.add(transaction)         // Add to expenses list.
                totalExpense += transaction.amount  // Accumulate total expenses.
            }
        }

        // Calculate the overall balance by subtracting total expenses from total income.
        val totalBalance: Double = totalIncome - totalExpense

        // Update the TextViews with the formatted amounts reflecting balance, income, and expenses.
        "${AppConstants.formatAmount(totalBalance)} ZAR".also { amount.text = it }
        "${AppConstants.formatAmount(totalIncome)} ZAR".also { incomeAmount.text = it }
        "${AppConstants.formatAmount(totalExpense)} ZAR".also { expenseAmount.text = it }
    }
}