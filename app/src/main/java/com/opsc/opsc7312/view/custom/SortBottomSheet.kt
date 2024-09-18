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
    private var _binding: SortTransactionBottomDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TransactionAdapter

    private var transactions: MutableList<Transaction> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SortTransactionBottomDialogBinding.inflate(inflater, container, false)



        // Set up the onClick functionality (listener)
        binding.sortByNameAscending.setOnClickListener{ onRadioButtonClicked(it)}
        binding.sortByNameDescending.setOnClickListener{ onRadioButtonClicked(it)}
        binding.sortByDateAscending.setOnClickListener{ onRadioButtonClicked(it)}
        binding.sortByDateDescending.setOnClickListener{ onRadioButtonClicked(it)}
        binding.sortByHigehestAmount.setOnClickListener{ onRadioButtonClicked(it)}
        binding.sortByLowestAmount.setOnClickListener{ onRadioButtonClicked(it)}

        return binding.root
    }

    fun onRadioButtonClicked(view: View){
        val isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.sortByNameAscending -> {
                if(isSelected){

                    sortData(AppConstants.SORT_TYPE.NAME_ASCENDING)
                    dismiss()
                }
            }
            R.id.sortByNameDescending -> {
                // Handle sort by date
                if(isSelected){

                    sortData(AppConstants.SORT_TYPE.NAME_DESCENDING)
                    dismiss()
                }
            }
            R.id.sortByDateAscending -> {
                // Handle sort by amount
                if(isSelected){
                    sortData(AppConstants.SORT_TYPE.DATE_ASCENDING)
                    dismiss()
                }
            }

            R.id.sortByDateDescending -> {
                // Handle sort by amount
                if(isSelected){

                    sortData(AppConstants.SORT_TYPE.DATE_DESCENDING)
                    dismiss()
                }
            }
            R.id.sortByHigehestAmount -> {
                // Handle sort by amount
                if(isSelected){
                    sortData(AppConstants.SORT_TYPE.HIGHEST_AMOUNT)
                    dismiss()

                }
            }

            R.id.sortByLowestAmount -> {
                // Handle sort by amount
                if(isSelected){
                    sortData(AppConstants.SORT_TYPE.LOWEST_AMOUNT)
                    dismiss()
                }

            }
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    private fun sortData(sortBy: AppConstants.SORT_TYPE){

        when(sortBy){
            AppConstants.SORT_TYPE.NAME_ASCENDING -> {
                transactions.sortBy { it.name }
                adapter.updateTransactions(transactions.toList())
            }

            AppConstants.SORT_TYPE.NAME_DESCENDING -> {
                transactions.sortByDescending { it.name }
                adapter.updateTransactions(transactions.toList())
            }

            AppConstants.SORT_TYPE.DATE_ASCENDING -> {
                transactions.sortBy { it.date }
                adapter.updateTransactions(transactions.toList())
            }

            AppConstants.SORT_TYPE.DATE_DESCENDING -> {
                transactions.sortByDescending { it.date }
                adapter.updateTransactions(transactions.toList())
            }

            AppConstants.SORT_TYPE.HIGHEST_AMOUNT -> {
                transactions.sortByDescending { it.amount }
                adapter.updateTransactions(transactions.toList())
            }

            AppConstants.SORT_TYPE.LOWEST_AMOUNT -> {
                transactions.sortBy { it.amount }
                adapter.updateTransactions(transactions.toList())
            }


        }


    }
}