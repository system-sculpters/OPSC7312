package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentBuyStockBinding
import com.opsc.opsc7312.model.api.controllers.InvestmentController
import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.model.Trade
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.NotificationHandler
import com.opsc.opsc7312.view.custom.TimeOutDialog


class BuyStockFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentBuyStockBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var investmentViewModel: InvestmentController

    private var stock: Stock? = Stock()

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog

    // Manager for handling authentication tokens
    private lateinit var tokenManager: TokenManager

    // Manager for handling user information
    private lateinit var userManager: UserManager

    // Variable to store the error message if input validation fails
    private var errorMessage = ""

    private lateinit var notificationHandler: NotificationHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stock = it.getParcelable(STOCK_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyStockBinding.inflate(inflater, container, false)

        investmentViewModel = ViewModelProvider(this).get(InvestmentController::class.java)

        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize UserManager instance for managing user data
        userManager = UserManager.getInstance(requireContext())

        notificationHandler = NotificationHandler(requireContext())

        timeOutDialog = TimeOutDialog()

        // Load the image using Glide
        Glide.with(requireContext())
            .load(stock?.logoUrl) // Use image URL or resource ID
            .placeholder(R.drawable.baseline_image_search_24) // Placeholder image
            .into(binding.stockImage)

        binding.name.text = stock?.name

        binding.symbol.text = stock?.symbol

        binding.currentValue.text = "${stock?.currentPrice?.let { AppConstants.formatAmount(it) }} ZAR"

        val percentage = percentageChange(stock?.currentPrice!!, stock?.previousClosePrice!!)

        if(percentage >= 0){
            binding.currentValuePercentage.text = "+${AppConstants.formatAmount(percentage)}%"
            binding.currentValuePercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        } else{
            binding.currentValuePercentage.text = "${AppConstants.formatAmount(percentage)}%"
            binding.currentValuePercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        binding.quantity.setText("1")

        binding.targetAmount.text = "${stock?.currentPrice?.let { AppConstants.formatAmount(it) }} ZAR"

        binding.buyButton.setOnClickListener {
            buyStocks()
        }

        quantityListener()

        return binding.root
    }

    private fun quantityListener() {
        binding.quantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    val quantity = s.toString().toIntOrNull()
                    if (quantity != null) {
                        // Multiply stock's current price by quantity
                        val totalAmount = (stock?.currentPrice ?: 0.0) * quantity
                        // Set the calculated amount to targetAmount
                        binding.targetAmount.text = "${AppConstants.formatAmount(totalAmount)} ZAR"
                    } else {
                        // If quantity is invalid, reset target amount
                        binding.targetAmount.text = "${stock?.currentPrice?.let { AppConstants.formatAmount(it) }} ZAR"
                    }
                } else {
                    // If quantity is empty, reset target amount
                    binding.targetAmount.text = "${stock?.currentPrice?.let { AppConstants.formatAmount(it) }} ZAR"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun percentageChange(currentPrice: Double, previousClosePrice: Double): Double {
        return ((currentPrice - previousClosePrice) / previousClosePrice) * 100;
    }

    private fun buyStocks() {
        val token = tokenManager.getToken() // Retrieve the authentication token
        val user = userManager.getUser()
        if (token != null) {
            observeViewModel(token, user.id) // Observe the ViewModel for goal data
        } else {
            // Handle case when token is null (e.g., show an error message or prompt for login)
        }
    }

    private fun observeViewModel(token: String, userId: String) {
        // This meth implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel

        // Show a progress dialog while waiting for data
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())


        val quantity = binding.quantity.text.toString()
        if(quantity.isEmpty()){
            errorMessage += "${getString(R.string.enter_a_quantity)}\n"
        } else if(quantity.toIntOrNull() == null){
            errorMessage += "${getString(R.string.int_quantity)}\n"
        } else if(quantity.toInt() < 0) {
            errorMessage += "${getString(R.string.more_or_equal_zero)}\n"
        }

        if(errorMessage.isNotEmpty()){
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        val buyStock = Trade(userid = userId, symbol = stock?.symbol!!, quantity = quantity.toInt())
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
                val notificationTitle = getString(R.string.goal_created)
                val notificationMessage = "'${stock?.name}' stock bought successfully."
                notificationHandler.createNotificationChannel()
                notificationHandler.showNotification(notificationTitle, notificationMessage)

                redirectToPortfolio()

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
                    investmentViewModel.buyInvestment(token, buyStock)
                }
            }
        }

        // Make an initial API call to retrieve all goals for the user
        investmentViewModel.buyInvestment(token, buyStock)
    }

    private fun redirectToPortfolio(){
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        val portfolioFragment = PortfolioFragment()

        // Navigate to CategoryDetailsFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.investment_frame_layout, portfolioFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val STOCK_ARG = "stock"
        @JvmStatic
        fun newInstance(stock: Stock) =
            BuyStockFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STOCK_ARG, stock)
                }
            }
    }
}