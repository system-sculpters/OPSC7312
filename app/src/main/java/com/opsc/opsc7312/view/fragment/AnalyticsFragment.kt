package com.opsc.opsc7312.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentAnalyticsBinding
import com.opsc.opsc7312.model.api.controllers.AnalyticsController
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.data.model.CategoryExpense
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.IncomeExpense
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.AnalyticsAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.AnalyticsObserver
import com.opsc.opsc7312.view.observers.TransactionsObserver


class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var pieChart: PieChart
    private lateinit var incomeExpenseChart: BarChart
    private lateinit var incomeChart: BarChart
    private lateinit var radioButtonMonth: AppCompatRadioButton
    private lateinit var radioButtonWeek: AppCompatRadioButton

    private lateinit var analyticsViewModel: AnalyticsController

    private lateinit var adapter: AnalyticsAdapter

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var timeOutDialog: TimeOutDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)

        pieChart = binding.categoryExpenseChart
        incomeExpenseChart = binding.incomeExpenseBarChart
        incomeChart = binding.incomeChart

        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        analyticsViewModel = ViewModelProvider(this).get(AnalyticsController::class.java)

        adapter = AnalyticsAdapter(requireContext(), pieChart, incomeExpenseChart, incomeChart, binding.totalIncome, textColor())

        timeOutDialog = TimeOutDialog()


        setUpUserDetails()

        return binding.root
    }



    fun onRadioButtonClicked(view: View) {
        val isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.month -> {
                if (isSelected) {
                    // Handle the action for "All" button
                    radioButtonMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioButtonWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))

                }
            }
            R.id.week -> {
                if (isSelected) {
                    // Handle the action for "Today" button
                    radioButtonWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioButtonMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
                    //viewModel.getTodayLeaderboard()

                }
            }
        }
    }


    private fun populateGoalProgress(goals: List<Goal>){
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

    private fun setUpUserDetails(){
        val user = userManager.getUser()

        val token = tokenManager.getToken()


        if(token != null){
            Log.d("re auth", "this is token: $token")
            observeViewModel(token, user.id)
        }else{
            Log.d("re auth", "hola me amo dora")
        }
    }

    private fun observeViewModel(token: String, userId: String){
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        analyticsViewModel.status.observe(viewLifecycleOwner)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                // Success
                progressDialog.dismiss()
            } else {
                // Failure
                progressDialog.dismiss()
            }
        }

        analyticsViewModel.message.observe(viewLifecycleOwner) { message ->
            // Show message to the user, if needed
            Log.d("Transactions message", message)

            if(message == "timeout"){
                timeOutDialog.showTimeoutDialog(requireContext() ){
                    //progressDialog.show()
                    analyticsViewModel.fetchAllAnalytics(token, userId)
                }
            }
        }

        analyticsViewModel.analytics.observe(viewLifecycleOwner, AnalyticsObserver(adapter))
        analyticsViewModel.fetchAllAnalytics(token, userId)
    }


    private fun textColor(): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
        val color = typedValue.data
        return color
    }
}