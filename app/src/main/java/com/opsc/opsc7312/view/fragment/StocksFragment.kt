package com.opsc.opsc7312.view.fragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentStocksBinding
import com.opsc.opsc7312.model.api.controllers.StockController
import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.view.activity.InvestmentActivity
import com.opsc.opsc7312.view.adapter.StockAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.GoalObserver
import com.opsc.opsc7312.view.observers.StockObserver


class StocksFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentStocksBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var stockViewModel: StockController

    private lateinit var stockAdapter: StockAdapter

    private lateinit var stocks: MutableList<Stock>

    private lateinit var filteredStocks: MutableList<Stock>

    // Manager for handling authentication tokens
    private lateinit var tokenManager: TokenManager

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStocksBinding.inflate(inflater, container, false)

        stocks = mutableListOf()

        filteredStocks = mutableListOf()

        stockViewModel = ViewModelProvider(this).get(StockController::class.java)

        stockAdapter = StockAdapter(requireContext()) { selectedStock, percentage ->
            val stockFragment = StockFragment.newInstance(selectedStock, percentage)
            changeCurrentFragment(stockFragment)
        }


        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize the timeout dialog
        timeOutDialog = TimeOutDialog()

        setUpRecyclerView()

        setupStocks()

        binding.searchStock.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    filterStocks(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Inflate the layout for this fragment
        return  binding.root
    }

    // Sets the toolbar title after the view has been created.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title for the fragment to "Categories".
        (activity as? InvestmentActivity)?.setToolbarTitle(getString(R.string.market))
    }

    // method that sets up the RecyclerView for displaying the list of goals.
    private fun setUpRecyclerView() {
        binding.searchResultsList.layoutManager = LinearLayoutManager(requireContext()) // Set the layout manager
        binding.searchResultsList.setHasFixedSize(true) // Improve performance if the size is fixed
        binding.searchResultsList.adapter = stockAdapter // Set the adapter for the RecyclerView
    }

    private fun filterStocks(query: String) {
        filteredStocks.clear() // Clear the current filtered list
        // Filter properties based on the query
        filteredStocks.addAll(stocks.filter { stock ->
            stock.name.contains(query, ignoreCase = true) // Assuming Property has a 'name' field
        })

        stockAdapter.updateStocks(filteredStocks) // Update the adapter with filtered properties
    }

    fun updateStocks(data: List<Stock>){
        stocks.clear()
        stocks.addAll(data)
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

        if (token != null) {
            observeViewModel(token) // Observe the ViewModel for goal data
        } else {
            // Handle case when token is null (e.g., show an error message or prompt for login)
        }
    }

    private fun observeViewModel(token: String) {
        // Show a progress dialog while waiting for data
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status LiveData from the ViewModel
        stockViewModel.status.observe(viewLifecycleOwner) { status ->
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
        stockViewModel.message.observe(viewLifecycleOwner) { message ->
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
                    stockViewModel.getAllStocks(token)
                }
            }
        }

        // Observe the goalList LiveData from the ViewModel with a custom observer

        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        stockViewModel.stockList.observe(viewLifecycleOwner, StockObserver(stockAdapter, this))

        // Make an initial API call to retrieve all goals for the user
        stockViewModel.getAllStocks(token)
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}