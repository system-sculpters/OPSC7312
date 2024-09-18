package com.opsc.opsc7312.view.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
}