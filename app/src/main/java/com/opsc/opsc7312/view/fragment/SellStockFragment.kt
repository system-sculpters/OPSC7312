package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentSellStockBinding
import com.opsc.opsc7312.model.api.controllers.InvestmentController
import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.model.Trade
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.NotificationHandler
import com.opsc.opsc7312.view.custom.TimeOutDialog
import kotlinx.coroutines.launch

class SellStockFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentSellStockBinding? = null

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

    private var ownedQuantity = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stock = it.getParcelable(BuyStockFragment.STOCK_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellStockBinding.inflate(inflater, container, false)

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


        val token = tokenManager.getToken() // Retrieve the authentication token
        val user = userManager.getUser()
        if (token != null) {
            getInvestment(token, user.id) // Observe the ViewModel for goal data
        } else {
            // Handle case when token is null (e.g., show an error message or prompt for login)
        }

        binding.targetAmount.text = "${stock?.currentPrice?.let { AppConstants.formatAmount(it) }} ZAR"

        binding.buyButton.setOnClickListener {
            sellStocks()
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

    private fun sellStocks() {
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


        val quantity = binding.quantity.text.toString()
        if(quantity.isEmpty()){
            errorMessage += "${getString(R.string.enter_a_quantity)}\n"
        } else if(quantity.toIntOrNull() == null){
            errorMessage += "${getString(R.string.int_quantity)}\n"
        } else if(quantity.toInt() < 0) {
            errorMessage += "${getString(R.string.more_or_equal_zero)}\n"
        } else if(quantity.toInt() > ownedQuantity){
            errorMessage += "${getString(R.string.more_than_owned)}\n"
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
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.stock_sold), hideProgressBar = true)

                // If the status indicates success, dismiss the progress dialog

                val notificationTitle = getString(R.string.goal_created)
                val notificationMessage = "${quantity} quantities of '${stock?.name}' stock sold successfully."
                notificationHandler.createNotificationChannel()
                notificationHandler.showNotification(notificationTitle, notificationMessage)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    redirectToPortfolio()
                }, 1000)

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
                    investmentViewModel.sellInvestment(token, buyStock)
                }
            }
        }

        // Make an initial API call to retrieve all goals for the user
        investmentViewModel.sellInvestment(token, buyStock)
    }

    private fun getInvestment(token: String, userId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val symbol = stock?.symbol // Get the stock symbol safely
            if (symbol != null) {
                // Call the investment retrieval function and await the result
                val investment = investmentViewModel.getUserInvestment(token, userId, symbol)

                if (investment != null) {
                    binding.quantity.setText(investment.quantity.toString())
                    ownedQuantity = investment.quantity
                } else {
                    // Show a popup indicating the user doesn't own this stock
                    showPopup()
                }
            } else {
                // Handle the case where the stock symbol is null
                showPopup() // You might want to specify a different message for this case
            }
        }
    }


    private fun showPopup(){
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.stock_not_owned))
            .setMessage(getString(R.string.dont_own))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                // Redirect to Portfolio
                redirectToPortfolio()
            }
            .setCancelable(false)
            .show()
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
            SellStockFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STOCK_ARG, stock)
                }
            }
    }
}