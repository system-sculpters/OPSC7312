package com.opsc.opsc7312.view.custom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FilterTransactionDialogBinding
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.view.adapter.TransactionAdapter

class FilterBottomSheet : BottomSheetDialogFragment() {
    //This class was adapted from medium
    //https://medium.com/@kosta.palash/using-bottomsheetdialogfragment-with-material-design-guideline-f9814c39b9fc
    //Palash Kosta
    //https://medium.com/@kosta.palash
    // Binding instance for the filter dialog layout
    private var _binding: FilterTransactionDialogBinding? = null
    private val binding get() = _binding!!

    // Adapter for displaying transactions in a list, initialized with a callback (currently empty)
    private var adapter = TransactionAdapter { }

    // List to hold transaction data
    private var transactions: MutableList<Transaction> = mutableListOf()

    // Inflate the view for the bottom sheet dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for the filter bottom sheet dialog
        _binding = FilterTransactionDialogBinding.inflate(inflater, container, false)

        // Set up click listeners for filter options

        // This radio button setOnClickListener was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        binding.allFilter.setOnClickListener { onRadioButtonClicked(it) }
        binding.incomeFilter.setOnClickListener { onRadioButtonClicked(it) }
        binding.expenseFilter.setOnClickListener { onRadioButtonClicked(it) }

        // Return the root view of the binding
        return binding.root
    }

    // Called after the view is created; can be overridden for additional setup
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the apply filter button functionality
        binding.applyFilter.setOnClickListener {
            // Perform the filter action here before dismissing the bottom sheet
            dismiss() // Close the bottom sheet
        }
    }

    // Clean up the binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to prevent memory leaks
    }

    // Set the adapter for the transaction list
    fun setAdapter(transactionAdapter: TransactionAdapter) {
        adapter = transactionAdapter
    }

    // Update the list of transactions with new data
    fun updateTransactions(data: List<Transaction>) {
        transactions.clear() // Clear existing transactions
        transactions.addAll(data) // Add new transaction data
    }

    // Handle radio button clicks for filtering options
    fun onRadioButtonClicked(view: View) {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user

        // Check if the clicked radio button is selected
        val isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.allFilter -> {
                // If "All" filter is selected, filter for all transactions
                if (isSelected) {
                    filter(AppConstants.Filter_TYPE.ALL) // Call filter method with ALL type
                    dismiss() // Dismiss the bottom sheet
                }
            }

            R.id.incomeFilter -> {
                // If "Income" filter is selected, filter for income transactions
                if (isSelected) {
                    filter(AppConstants.Filter_TYPE.INCOME) // Call filter method with INCOME type
                    dismiss()
                }
            }

            R.id.expenseFilter -> {
                // If "Expense" filter is selected, filter for expense transactions
                if (isSelected) {
                    filter(AppConstants.Filter_TYPE.EXPENSE) // Call filter method with EXPENSE type
                    dismiss()
                }
            }
        }
    }

    // Filter transactions based on the selected filter type
    private fun filter(filterBy: AppConstants.Filter_TYPE) {
        when (filterBy) {
            AppConstants.Filter_TYPE.ALL -> {
                // If filtering for all, update the adapter with the complete list of transactions
                adapter.updateTransactions(transactions.toList())
            }

            AppConstants.Filter_TYPE.INCOME -> {
                // Filter transactions for income and update the adapter
                adapter.updateTransactions(filerData(AppConstants.Filter_TYPE.INCOME))
            }

            AppConstants.Filter_TYPE.EXPENSE -> {
                // Filter transactions for expenses and update the adapter
                adapter.updateTransactions(filerData(AppConstants.Filter_TYPE.EXPENSE))
            }
        }
    }

    // Filter the transactions based on the specified type
    private fun filerData(filterBy: AppConstants.Filter_TYPE): List<Transaction> {
        val filteredList = mutableListOf<Transaction>() // Create a list to hold filtered transactions
        for (transaction in transactions) {
            // Log the type of transaction and the filter being applied for debugging
            Log.d("transaction filer", "type: ${transaction.type} == filterBy.name: ${filterBy.name}")
            // If the transaction type matches the filter type, add it to the filtered list
            if (transaction.type == filterBy.name) {
                filteredList.add(transaction)
            }
        }
        // Return the filtered list as an immutable list
        return filteredList.toList()
    }
}
