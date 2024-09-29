package com.opsc.opsc7312.view.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.SortTransactionBottomDialogBinding
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.view.adapter.TransactionAdapter

class SortBottomSheet : BottomSheetDialogFragment() {
    //This class was adapted from medium
    //https://medium.com/@kosta.palash/using-bottomsheetdialogfragment-with-material-design-guideline-f9814c39b9fc
    //Palash Kosta
    //https://medium.com/@kosta.palash

    // Binding instance for the bottom dialog layout
    private var _binding: SortTransactionBottomDialogBinding? = null
    private val binding get() = _binding!!

    // Adapter for displaying transactions in a list
    private lateinit var adapter: TransactionAdapter

    // List to hold transaction data
    private var transactions: MutableList<Transaction> = mutableListOf()

    // Inflate the view for the bottom sheet dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for the bottom sheet dialog
        _binding = SortTransactionBottomDialogBinding.inflate(inflater, container, false)

        // Set up click listeners for sorting options

        // This radio button setOnClickListener was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user

        binding.sortByNameAscending.setOnClickListener { onRadioButtonClicked(it) }
        binding.sortByNameDescending.setOnClickListener { onRadioButtonClicked(it) }
        binding.sortByDateAscending.setOnClickListener { onRadioButtonClicked(it) }
        binding.sortByDateDescending.setOnClickListener { onRadioButtonClicked(it) }
        binding.sortByHigehestAmount.setOnClickListener { onRadioButtonClicked(it) }
        binding.sortByLowestAmount.setOnClickListener { onRadioButtonClicked(it) }

        // Return the root view of the binding
        return binding.root
    }

    // Handle radio button clicks for sorting options
    fun onRadioButtonClicked(view: View) {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user

        // Check if the clicked radio button is selected
        val isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.sortByNameAscending -> {
                // Sort transactions by name in ascending order
                if (isSelected) {
                    sortData(AppConstants.SORT_TYPE.NAME_ASCENDING)
                    dismiss() // Dismiss the bottom sheet
                }
            }
            R.id.sortByNameDescending -> {
                // Sort transactions by name in descending order
                if (isSelected) {
                    sortData(AppConstants.SORT_TYPE.NAME_DESCENDING)
                    dismiss()
                }
            }
            R.id.sortByDateAscending -> {
                // Sort transactions by date in ascending order
                if (isSelected) {
                    sortData(AppConstants.SORT_TYPE.DATE_ASCENDING)
                    dismiss()
                }
            }
            R.id.sortByDateDescending -> {
                // Sort transactions by date in descending order
                if (isSelected) {
                    sortData(AppConstants.SORT_TYPE.DATE_DESCENDING)
                    dismiss()
                }
            }
            R.id.sortByHigehestAmount -> {
                // Sort transactions by amount in descending order
                if (isSelected) {
                    sortData(AppConstants.SORT_TYPE.HIGHEST_AMOUNT)
                    dismiss()
                }
            }
            R.id.sortByLowestAmount -> {
                // Sort transactions by amount in ascending order
                if (isSelected) {
                    sortData(AppConstants.SORT_TYPE.LOWEST_AMOUNT)
                    dismiss()
                }
            }
        }
    }

    // Called after the view is created; can be overridden for additional setup
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    // Clean up the binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Set the adapter for the transaction list
    fun setAdapter(transactionAdapter: TransactionAdapter) {
        adapter = transactionAdapter
    }

    // Update the list of transactions
    fun updateTransactions(data: List<Transaction>) {
        transactions.clear() // Clear existing transactions
        transactions.addAll(data) // Add new transaction data
    }

    // Sort transactions based on the specified criteria
    private fun sortData(sortBy: AppConstants.SORT_TYPE) {
        when (sortBy) {
            AppConstants.SORT_TYPE.NAME_ASCENDING -> {
                transactions.sortBy { it.name } // Sort by name ascending
                adapter.updateTransactions(transactions.toList()) // Update adapter with sorted data
            }
            AppConstants.SORT_TYPE.NAME_DESCENDING -> {
                transactions.sortByDescending { it.name } // Sort by name descending
                adapter.updateTransactions(transactions.toList())
            }
            AppConstants.SORT_TYPE.DATE_ASCENDING -> {
                transactions.sortBy { it.date } // Sort by date ascending
                adapter.updateTransactions(transactions.toList())
            }
            AppConstants.SORT_TYPE.DATE_DESCENDING -> {
                transactions.sortByDescending { it.date } // Sort by date descending
                adapter.updateTransactions(transactions.toList())
            }
            AppConstants.SORT_TYPE.HIGHEST_AMOUNT -> {
                transactions.sortByDescending { it.amount } // Sort by highest amount
                adapter.updateTransactions(transactions.toList())
            }
            AppConstants.SORT_TYPE.LOWEST_AMOUNT -> {
                transactions.sortBy { it.amount } // Sort by lowest amount
                adapter.updateTransactions(transactions.toList())
            }
        }
    }
}
