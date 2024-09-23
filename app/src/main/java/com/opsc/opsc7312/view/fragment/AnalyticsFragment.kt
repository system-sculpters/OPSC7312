package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentAnalyticsBinding
import com.opsc.opsc7312.model.api.controllers.AnalyticsController
import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.AnalyticsAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.AnalyticsObserver


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

    private lateinit var analyticsResponse: AnalyticsResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)

        pieChart = binding.categoryExpenseChart
        incomeExpenseChart = binding.incomeExpenseBarChart
        incomeChart = binding.incomeChart

        radioButtonWeek = binding.week
        radioButtonMonth = binding.month

        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        analyticsResponse = AnalyticsResponse(listOf(), listOf(), listOf(), listOf())

        analyticsViewModel = ViewModelProvider(this).get(AnalyticsController::class.java)

        adapter = AnalyticsAdapter(requireContext(), pieChart, incomeExpenseChart, incomeChart, binding.totalIncome, textColor(),
            binding.amount, binding.remainingAmount, binding.progressBar)

        timeOutDialog = TimeOutDialog()


        setUpUserDetails()

        radioButtonWeek.setOnClickListener{onRadioButtonClicked(it)}
        radioButtonMonth.setOnClickListener{onRadioButtonClicked(it)}

        binding.editGoals.setOnClickListener {
            changeCurrentFragment(GoalsFragment())
        }

        setupPowerSpinner()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Analytics")
    }


    private fun onRadioButtonClicked(view: View) {
        val isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.month -> {
                if (isSelected) {
                    // Handle the action for "All" button
                    radioButtonMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioButtonWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
                    adapter.updateIncomeExpenseChart(analyticsResponse.transactionsByMonth.take(6))
                }
            }
            R.id.week -> {
                if (isSelected) {
                    // Handle the action for "Today" button
                    radioButtonWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioButtonMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
                    //viewModel.getTodayLeaderboard()
                    adapter.updateIncomeExpenseChart(analyticsResponse.dailyTransactions)
                }
            }
        }
    }

    private fun setupPowerSpinner(){
        val months = listOf("1 Year", "6 Months", "3 Months")

        binding.months.setItems(months)

        binding.months.selectItemByIndex(1)
        binding.months.setOnSpinnerItemSelectedListener<String> { _, _, newIndex, newItem ->
            // `newItem` is the selected month from the spinner
            // Update the chart based on the selected month
            adapter.updateIncomeChart(newItem, analyticsResponse.transactionsByMonth)
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
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "analytics update successful!", hideProgressBar = true, )

                progressDialog.dismiss()

            } else {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "analytics update failed!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                }, 2000)
            }
        }

        analyticsViewModel.message.observe(viewLifecycleOwner) { message ->
            // Show message to the user, if needed
            Log.d("Transactions message", message)

            if(message == "timeout" || message.contains("Unable to resolve host")){
                timeOutDialog.showTimeoutDialog(requireContext() ){
                    //progressDialog.show()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Fetching analytics...", hideProgressBar = false)
                    analyticsViewModel.fetchAllAnalytics(token, userId)
                }
            }
        }

        analyticsViewModel.analytics.observe(viewLifecycleOwner, AnalyticsObserver(this, adapter))
        analyticsViewModel.fetchAllAnalytics(token, userId)
    }

    fun updateAnalyticData(data: AnalyticsResponse){
        analyticsResponse = data
    }

    private fun textColor(): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
        val color = typedValue.data
        return color
    }

    private fun changeCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}