package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.databinding.FragmentAnalyticsBinding
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.Transaction


class AnalyticsFragment : Fragment() {


    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)



        return binding.root
    }

    private fun populateGoalProgress(goals: ArrayList<Goal>){
        var totalCurrentAmount = 0.00
        var totalGoalAmount = 0.00

        for(goal in goals){
            totalCurrentAmount += goal.currentamount
            totalGoalAmount += goal.targetamount
        }

        val remainingAmount = totalGoalAmount - totalCurrentAmount

        val progress = if (totalGoalAmount > 0) {
            (totalCurrentAmount / totalGoalAmount * 100).toInt()
        } else {
            0
        }

        binding.amount.text = "${totalCurrentAmount}/${totalGoalAmount} ZAR"

        binding.remainingAmount.text = "${remainingAmount} ZAR remaining to achieve your goal"

        binding.progressBar.progress = progress

    }

    private fun populateExpenseCategories(transactions: ArrayList<Transaction>){
        val expenses = arrayListOf<Transaction>()

        for(transaction in transactions){
            if(transaction.type == AppConstants.TRANSACTIONTYPE.EXPENSE.name){
                expenses.add(transaction)
            }
        }


    }

}