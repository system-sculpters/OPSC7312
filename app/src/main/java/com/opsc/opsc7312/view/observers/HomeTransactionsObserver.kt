package com.opsc.opsc7312.view.observers

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.view.adapter.TransactionAdapter

class HomeTransactionsObserver(
    private val adapter: TransactionAdapter?,
    private val amount: TextView,
    private val incomeAmount: TextView,
    private val expenseAmount: TextView
): Observer<List<Transaction>> {
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
        var totalIncome = 0.0
        var totalExpense = 0.0
        val income: ArrayList<Transaction> = arrayListOf()
        val expenses: ArrayList<Transaction> = arrayListOf()

        for (transaction in transactionList) {
            if(transaction.type == "Income"){
                income.add(transaction)
                totalIncome += transaction.amount
            } else{
                expenses.add(transaction)
                totalExpense += transaction.amount
            }
        }

        val totalBalance: Double = totalIncome - totalExpense

        "$totalBalance ZAR".also { amount.text = it }
        "$totalIncome ZAR".also { incomeAmount.text = it }
        "$totalExpense ZAR".also { expenseAmount.text = it }
    }
}