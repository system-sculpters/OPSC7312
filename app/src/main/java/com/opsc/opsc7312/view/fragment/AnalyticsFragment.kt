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

    // View binding object for accessing views in the layout
    private var _binding: FragmentAnalyticsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // UI elements for displaying charts and filters
    private lateinit var pieChart: PieChart
    private lateinit var incomeExpenseChart: BarChart
    private lateinit var incomeChart: BarChart
    private lateinit var radioButtonMonth: AppCompatRadioButton
    private lateinit var radioButtonWeek: AppCompatRadioButton

    // ViewModel for managing analytics data and business logic
    private lateinit var analyticsViewModel: AnalyticsController

    // Adapter for populating charts and other views with data
    private lateinit var adapter: AnalyticsAdapter

    // UserManager instance to handle user-related operations
    private lateinit var userManager: UserManager

    // TokenManager instance to handle user authentication tokens
    private lateinit var tokenManager: TokenManager

    // Dialog for handling timeouts and showing progress
    private lateinit var timeOutDialog: TimeOutDialog

    // Response object for storing analytics data
    private lateinit var analyticsResponse: AnalyticsResponse

    // Called when the fragment's view is created. Sets up binding and initializes UI elements.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and initialize binding
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)

        // Initialize the pie chart and bar charts for displaying analytics data
        pieChart = binding.categoryExpenseChart
        incomeExpenseChart = binding.incomeExpenseBarChart
        incomeChart = binding.incomeChart

        // Initialize radio buttons for filtering analytics data by week or month
        radioButtonWeek = binding.week
        radioButtonMonth = binding.month

        // Initialize user and token managers to handle user details and authentication tokens
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize empty analytics response data structure
        analyticsResponse = AnalyticsResponse(listOf(), listOf(), listOf(), listOf())

        // Set up ViewModel to handle analytics-related data
        analyticsViewModel = ViewModelProvider(this).get(AnalyticsController::class.java)

        // Initialize the adapter for managing data visualization (charts and text)
        adapter = AnalyticsAdapter(
            requireContext(), pieChart, incomeExpenseChart, incomeChart,
            binding.totalIncome, textColor(), binding.amount, binding.remainingAmount,
            binding.progressBar
        )

        // Initialize the dialog for handling timeouts and progress feedback
        timeOutDialog = TimeOutDialog()

        // Set up user details (fetch user info and analytics)
        setUpUserDetails()

        // Set click listeners for the radio buttons to handle filtering by week or month
        // This radio button setOnClickListener was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        radioButtonWeek.setOnClickListener { onRadioButtonClicked(it) }
        radioButtonMonth.setOnClickListener { onRadioButtonClicked(it) }

        // Set click listener for editing goals
        binding.editGoals.setOnClickListener {
            changeCurrentFragment(GoalsFragment())
        }

        // Set up a spinner (dropdown) to select the time period for analytics
        setupPowerSpinner()

        // Return the root view for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.analytics))
    }

    // Handles the click event for the radio buttons (week/month filters)
    private fun onRadioButtonClicked(view: View) {
        // This radio button  was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.month -> {
                if (isSelected) {
                    // If the month radio button is selected, update UI and chart for monthly data
                    radioButtonMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioButtonWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
                    adapter.updateIncomeExpenseChart(analyticsResponse.transactionsByMonth.take(6))
                }
            }
            R.id.week -> {
                if (isSelected) {
                    // If the week radio button is selected, update UI and chart for weekly data
                    radioButtonWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioButtonMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
                    adapter.updateIncomeExpenseChart(analyticsResponse.dailyTransactions)
                }
            }
        }
    }

    // Sets up the spinner for selecting time periods like "1 Year", "6 Months", etc.
    private fun setupPowerSpinner() {
        val months = listOf(getString(R.string.one_year), getString(R.string.six_months), getString(R.string.three_months))

        binding.months.setItems(months)

        // Set default selected item (index 1: "6 Months")
        binding.months.selectItemByIndex(1)

        // Set listener for handling the selected item change in the spinner
        binding.months.setOnSpinnerItemSelectedListener<String> { _, _, newIndex, newItem ->
            adapter.updateIncomeChart(newItem, analyticsResponse.transactionsByMonth)
        }
    }

    // Sets up user details by fetching the user and token, and observing ViewModel for updates
    private fun setUpUserDetails() {
        val user = userManager.getUser() // Fetch the current user
        val token = tokenManager.getToken() // Fetch the authentication token

        if (token != null) {
            // If a valid token is available, observe the ViewModel for analytics data
            Log.d("re auth", "this is token: $token")
            observeViewModel(token, user.id)
        } else {
            Log.d("re auth", "hola me amo dora")
        }
    }

    // Observes the ViewModel to update the UI with analytics data or handle errors
    private fun observeViewModel(token: String, userId: String) {
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe changes in status (success/failure of data fetching)
        analyticsViewModel.status.observe(viewLifecycleOwner) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            if (status) {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.analytics_update_successful), hideProgressBar = true)
                progressDialog.dismiss()
            } else {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.analytics_update_fail), hideProgressBar = true)

                // Dismiss dialog after 2 seconds if there's a failure
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                }, 2000)
            }
        }

        // Observe messages from ViewModel, such as errors like timeouts
        analyticsViewModel.message.observe(viewLifecycleOwner) { message ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            Log.d("Transactions message", message)

            // Handle timeout or network issues by retrying data fetch
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.fetch_analytics), hideProgressBar = false)
                    analyticsViewModel.fetchAllAnalytics(token, userId)
                }
            }
        }

        // Observe analytics data and update the UI when data is available
        analyticsViewModel.analytics.observe(viewLifecycleOwner, AnalyticsObserver(this, adapter))
        analyticsViewModel.fetchAllAnalytics(token, userId)
    }

    // Updates the analytics data in the fragment
    fun updateAnalyticData(data: AnalyticsResponse) {
        analyticsResponse = data
    }

    // Fetches the theme-based text color for UI elements
    private fun textColor(): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
        return typedValue.data
    }

    // Replaces the current fragment with another one, adding to the backstack
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
