package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentHomeBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.HomeTransactionsObserver

//HomeFragment is a UI component that displays the user's home screen
class HomeFragment : Fragment() {
    // Binding for the fragment's layout
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Adapter for displaying transactions in a RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    // User manager for retrieving user-related data
    private lateinit var userManager: UserManager

    // Token manager for handling authentication tokens
    private lateinit var tokenManager: TokenManager

    // ViewModel for managing transaction-related data
    private lateinit var transactionViewModel: TransactionController

    // ViewModel for managing authentication-related data
    private lateinit var authController: AuthController

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for the fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize user and token managers for managing user data and tokens
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize ViewModels for transactions and authentication
        transactionViewModel = ViewModelProvider(this).get(TransactionController::class.java)
        authController = ViewModelProvider(this).get(AuthController::class.java)

        // Initialize the timeout dialog for connection issues
        timeOutDialog = TimeOutDialog()

        // Initialize the transaction adapter with a click listener for transaction details
        transactionAdapter = TransactionAdapter { transaction ->
            redirectToDetails(transaction)
        }

        // Set up a click listener to navigate to the TransactionsFragment
        binding.seeAll.setOnClickListener {
            changeCurrentFragment(TransactionsFragment())
        }

        // Set up a click listener to navigate to the TransactionsFragment
        binding.seeAllText.setOnClickListener {
            changeCurrentFragment(TransactionsFragment())
        }

        // Set up user details in the UI
        setUpUserDetails()

        // Set up the RecyclerView to display transactions
        setUpRecyclerView()

        // Return the root view of the fragment
        return binding.root
    }

    // Called immediately after onCreateView. Used for additional setup of the fragment's UI.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title to "Home"
        (activity as? MainActivity)?.setToolbarTitle("Home")
    }

    // Sets up user details in the UI, including username and email.
    // Observes the ViewModel for transactions if a valid token is present.
    private fun setUpUserDetails() {
        // Retrieve the current user and token
        val user = userManager.getUser()
        val token = tokenManager.getToken()

        // Set the user's username and email in the UI
        binding.username.text = user.username
        binding.email.text = user.email

        // If a valid token exists, observe the ViewModel for transaction data
        if (token != null) {
            observeViewModel(token, user.id)
        } else {
            // Handle the case where the token is null (optional implementation)
        }
    }

    //Observes the ViewModel for transaction-related data and manages UI interactions
    // based on the retrieval status and messages received.
    private fun observeViewModel(token: String, userId: String) {
        // Display a progress dialog to indicate that data is being fetched
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status of transaction retrieval from the ViewModel
        transactionViewModel.status.observe(viewLifecycleOwner) { status ->
            // Handle status changes to determine success or failure of the transaction retrieval
            if (status) {
                // If successful, update the progress dialog message
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Transaction retrieval successful!", hideProgressBar = true)

                // Dismiss the progress dialog immediately upon success
                progressDialog.dismiss()
            } else {
                // If failed, update the progress dialog message
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Transaction retrieval failed!", hideProgressBar = true)

                // Dismiss the progress dialog immediately upon failure
                progressDialog.dismiss()
            }
        }

        // Observe messages from the ViewModel
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check if the message indicates a timeout scenario
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog to inform the user
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    // Dismiss the previous progress dialog (if it is still visible)
                    progressDialog.dismiss()
                    // Show a new progress dialog while attempting to reconnect
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Connecting...", hideProgressBar = false)
                    // Retry fetching transactions after a timeout
                    transactionViewModel.getAllTransactions(token, userId)
                }
            }
        }

        // Observe the list of transactions and update the UI accordingly
        transactionViewModel.transactionList.observe(viewLifecycleOwner, HomeTransactionsObserver(transactionAdapter, binding.amount, binding.incomeAmount, binding.expenseAmount))

        // Initiate the API call to fetch all transactions for the user
        transactionViewModel.getAllTransactions(token, userId)
    }

    //Sets up the RecyclerView for displaying transaction items.
    private fun setUpRecyclerView() {
        // Set the layout manager for the RecyclerView to LinearLayoutManager
        // This arranges the items in a vertical list
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())

        // This helps improve layout performance since the size does not change based on the adapter content
        binding.recycleView.setHasFixedSize(true)

        // Set the adapter to provide data to the RecyclerView
        // The adapter will manage the individual transaction items
        binding.recycleView.adapter = transactionAdapter
    }

    // Sets up the RecyclerView for displaying transaction items.
    private fun redirectToDetails(transaction: Transaction){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val transactionDetailsFragment = UpdateTransactionFragment()
        val bundle = Bundle()
        bundle.putParcelable("transaction", transaction)
        transactionDetailsFragment.arguments = bundle

        // Navigate to CategoryDetailsFragment
        changeCurrentFragment(transactionDetailsFragment)
    }

    private fun changeCurrentFragment(fragment: Fragment) {
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