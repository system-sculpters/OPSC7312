package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentStockBinding
import com.opsc.opsc7312.databinding.FragmentStocksBinding
import com.opsc.opsc7312.model.api.controllers.StockController
import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.view.activity.InvestmentActivity
import com.opsc.opsc7312.view.adapter.StockHistoryAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.StockHistoryObserver
import com.opsc.opsc7312.view.observers.StockObserver

class StockFragment : Fragment() {
    private var selectedStock: Stock? = Stock()
    private var percentage: Double? = 0.0

    // View binding object for accessing views in the layout
    private var _binding: FragmentStockBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var stockViewModel: StockController

    private lateinit var adapter: StockHistoryAdapter

    // Manager for handling authentication tokens
    private lateinit var tokenManager: TokenManager

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedStock = it.getParcelable(SELECTED_STOCK_ARG)
            percentage = it.getDouble(PERCENTAGE_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)

        stockViewModel = ViewModelProvider(this).get(StockController::class.java)

        adapter = StockHistoryAdapter(requireContext(), binding.stockHistoryChart)

        tokenManager = TokenManager.getInstance(requireContext())

        timeOutDialog = TimeOutDialog()

        setupStocks()

        setupData()

        binding.buyButton.setOnClickListener {
            if(selectedStock != null){
                val buyStockFragment = BuyStockFragment.newInstance(selectedStock!!)
                changeCurrentFragment(buyStockFragment)
            }
        }

        binding.sellButton.setOnClickListener {
            if(selectedStock != null){
                val sellStockFragment = SellStockFragment.newInstance(selectedStock!!)
                changeCurrentFragment(sellStockFragment)
            }
        }


        return binding.root
    }

    // Sets the toolbar title after the view has been created.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title for the fragment to "Categories".
        (activity as? InvestmentActivity)?.setToolbarTitle(getString(R.string.stock))
    }

    private fun setupData(){
        binding.stockSymbol.text = selectedStock?.symbol

        binding.currentValue.text = "${selectedStock?.currentPrice?.let {
            AppConstants.formatAmount(
                it
            )
        }} ZAR"

        val percentageVal = percentageChange(selectedStock?.currentPrice!!, selectedStock?.previousClosePrice!!)

        if(percentageVal!! >= 0){
            binding.valueChange.text = "+${AppConstants.formatAmount(percentageVal!!)}%"
            binding.valueChange.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        } else{
            binding.valueChange.text = "${AppConstants.formatAmount(percentageVal!!)}%"
            binding.valueChange.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        binding.fullName.text = selectedStock?.name

        binding.marketName.text = selectedStock?.symbol
    }

    private fun percentageChange(currentPrice: Double, previousClosePrice: Double): Double {
        return ((currentPrice - previousClosePrice) / previousClosePrice) * 100;
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
                    selectedStock?.let { stockViewModel.getStockHistory(token, it.symbol) }
                }
            }
        }

        // Observe the goalList LiveData from the ViewModel with a custom observer

        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        stockViewModel.stockHistory.observe(viewLifecycleOwner, StockHistoryObserver(adapter))

        // Make an initial API call to retrieve all goals for the user
        selectedStock?.let { stockViewModel.getStockHistory(token, it.symbol) }
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

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SELECTED_STOCK_ARG = "selectedStock"
        const val PERCENTAGE_ARG = "percentage"

        @JvmStatic
        fun newInstance(selectedStock: Stock, percentage: Double) =
            StockFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SELECTED_STOCK_ARG, selectedStock)
                    putDouble(PERCENTAGE_ARG, percentage)
                }
            }
    }
}