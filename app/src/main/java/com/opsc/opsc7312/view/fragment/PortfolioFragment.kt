package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentPortfolioBinding
import com.opsc.opsc7312.model.api.controllers.InvestmentController
import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.activity.InvestmentActivity
import com.opsc.opsc7312.view.adapter.InvestmentAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.InvestmentObserver


class PortfolioFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentPortfolioBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var investmentViewModel: InvestmentController

    private lateinit var investmentAdapter: InvestmentAdapter

    private lateinit var stocks: MutableList<Stock>

    private lateinit var filteredStocks: MutableList<Stock>

    // Manager for handling authentication tokens
    private lateinit var tokenManager: TokenManager

    // Manager for handling user information
    private lateinit var userManager: UserManager

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        stocks = mutableListOf()

        filteredStocks = mutableListOf()

        investmentViewModel = ViewModelProvider(this).get(InvestmentController::class.java)

        investmentAdapter = InvestmentAdapter(requireContext()) { investment, percentage ->
            val stockFragment = investment.stockData?.let { StockFragment.newInstance(it, percentage) }
            if (stockFragment != null) {
                changeCurrentFragment(stockFragment)
            }
        }


        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize UserManager instance for managing user data
        userManager = UserManager.getInstance(requireContext())

        // Initialize the timeout dialog
        timeOutDialog = TimeOutDialog()

        setUpRecyclerView()

        setupStocks()

        // Inflate the layout for this fragment
        return  binding.root
    }

    // Sets the toolbar title after the view has been created.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title for the fragment to "Categories".
        (activity as? InvestmentActivity)?.setToolbarTitle(getString(R.string.portfolio))
    }

    // method that sets up the RecyclerView for displaying the list of goals.
    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set the layout manager
        binding.recyclerView.setHasFixedSize(true) // Improve performance if the size is fixed
        binding.recyclerView.adapter = investmentAdapter // Set the adapter for the RecyclerView
    }

    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.investment_frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupStocks() {
        val token = tokenManager.getToken() // Retrieve the authentication token
        val user = userManager.getUser()
        if (token != null) {
            observeViewModel(token, user.id) // Observe the ViewModel for goal data
        } else {
            // Handle case when token is null (e.g., show an error message or prompt for login)
        }
    }

    private fun observeViewModel(token: String, userId: String) {
        // Show a progress dialog while waiting for data
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status LiveData from the ViewModel
        investmentViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle changes in the status of goal retrieval (success or failure)
            if (status) {
                // If the status indicates success, dismiss the progress dialog
                progressDialog.dismiss()
            } else {
                // If the status indicates failure, also dismiss the progress dialog
                progressDialog.dismiss()
            }
        }

        // Observe the message LiveData from the ViewModel
        investmentViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Check if the message indicates a timeout error
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog to the user
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    // If the status indicates failure, also dismiss the progress dialog
                    progressDialog.dismiss()
                    // Display a new progress dialog indicating a reconnection attempt
                    timeOutDialog.showProgressDialog(requireContext())
                    // Update the progress dialog with a message while connecting
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.connecting), hideProgressBar = false)
                    // Retry fetching all goals from the ViewModel
                    investmentViewModel.getUserInvestments(token, userId)
                }
            }
        }

        // Observe the goalList LiveData from the ViewModel with a custom observer

        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        investmentViewModel.investmentList.observe(viewLifecycleOwner, InvestmentObserver(investmentAdapter, binding.amount, binding.percentageIncrease, requireContext()))

        // Make an initial API call to retrieve all goals for the user
        investmentViewModel.getUserInvestments(token, userId)
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}