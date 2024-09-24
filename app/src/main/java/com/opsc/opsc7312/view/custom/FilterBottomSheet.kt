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

class FilterBottomSheet: BottomSheetDialogFragment() {
    private var _binding: FilterTransactionDialogBinding? = null
    private val binding get() = _binding!!

    private var adapter = TransactionAdapter{

    }

    private var transactions: MutableList<Transaction> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilterTransactionDialogBinding.inflate(inflater, container, false)

        binding.allFilter.setOnClickListener{ onRadioButtonClicked(it)}
        binding.incomeFilter.setOnClickListener{ onRadioButtonClicked(it)}
        binding.expenseFilter.setOnClickListener{ onRadioButtonClicked(it)}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle the filter options here
        binding.applyFilter.setOnClickListener {
            // Perform filter action here
            dismiss() // Close the bottom sheet
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setAdapter(transactionAdapter: TransactionAdapter){
        adapter = transactionAdapter
    }

    fun updateTransactions(data: List<Transaction>){
        transactions.clear()
        transactions.addAll(data)
    }

    fun onRadioButtonClicked(view: View){
        val isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.allFilter -> {
                // Handle sort by date
                if(isSelected){

                    filter(AppConstants.Filter_TYPE.ALL)
                    dismiss()
                }
            }

            R.id.incomeFilter -> {
                // Handle sort by date
                if(isSelected){

                    filter(AppConstants.Filter_TYPE.INCOME)
                    dismiss()
                }
            }

            R.id.expenseFilter -> {
                // Handle sort by date
                if(isSelected){

                    filter(AppConstants.Filter_TYPE.EXPENSE)
                    dismiss()
                }
            }
        }
    }

    private fun filter(filterBy: AppConstants.Filter_TYPE) {

        when (filterBy) {
            AppConstants.Filter_TYPE.ALL -> {
                adapter.updateTransactions(transactions.toList())
            }

            AppConstants.Filter_TYPE.INCOME -> {
                adapter.updateTransactions(filerData(AppConstants.Filter_TYPE.INCOME))
            }

            AppConstants.Filter_TYPE.EXPENSE -> {
                adapter.updateTransactions(filerData(AppConstants.Filter_TYPE.EXPENSE))
            }
        }
    }

    private fun filerData(filterBy: AppConstants.Filter_TYPE): List<Transaction> {
        val filteredList = mutableListOf<Transaction>()
        for(transaction in transactions){
            Log.d("transaction filer", "type: ${transaction.type} == filterBy.name: ${filterBy.name}")
            if (transaction.type == filterBy.name){
                filteredList.add(transaction)
            }
        }
        return filteredList.toList()
    }
}