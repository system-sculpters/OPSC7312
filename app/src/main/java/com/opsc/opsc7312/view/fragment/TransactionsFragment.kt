package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import com.opsc.opsc7312.view.custom.FilterBottomSheet
import com.opsc.opsc7312.view.custom.SortBottomSheet
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.TransactionsObserver


class TransactionsFragment : Fragment() {

    // Binding for the fragment's view to access UI elements
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!! // Non-nullable reference for easy access

    // List to hold all transactions and categorize them into income and expenses
    private lateinit var transactionList: MutableList<Transaction>
    private lateinit var income: ArrayList<Transaction>
    private lateinit var expenses: ArrayList<Transaction>
    private lateinit var categoryList: ArrayList<Category> // List of categories for transactions

    // Adapter for displaying transactions in a RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    // ViewModel for managing transaction-related data
    private lateinit var transactionViewModel: TransactionController

    // ViewModel for managing authentication data
    private lateinit var authController: AuthController

    // Dialogs for timeout and sorting/filtering transactions
    private lateinit var timeOutDialog: TimeOutDialog
    private lateinit var sortBottomSheet: SortBottomSheet
    private lateinit var filterBottomSheet: FilterBottomSheet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        // Initialize lists for categories and transactions
        categoryList = arrayListOf<Category>()
        transactionList = mutableListOf<Transaction>()
        income = arrayListOf<Transaction>()
        expenses = arrayListOf<Transaction>()

        // Initialize bottom sheets for filtering and sorting transactions
        filterBottomSheet = FilterBottomSheet()
        sortBottomSheet = SortBottomSheet()

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize ViewModels
        transactionViewModel = ViewModelProvider(this).get(TransactionController::class.java)
        authController = ViewModelProvider(this).get(AuthController::class.java)

        // Set up the transaction adapter with a click listener to handle item clicks
        transactionAdapter = TransactionAdapter { transaction ->
            redirectToDetails(transaction) // Redirect to transaction details
        }

        // Set the adapter for filter and sort bottom sheets
        filterBottomSheet.setAdapter(transactionAdapter)
        sortBottomSheet.setAdapter(transactionAdapter)

        // Initialize timeout dialog
        timeOutDialog = TimeOutDialog()

        // Set up click listeners for filter and sort buttons
        binding.filter.setOnClickListener {
            filterBottomSheet.show(childFragmentManager, "FilterBottomSheet") // Show filter options
        }

        binding.sort.setOnClickListener {
            sortBottomSheet.show(childFragmentManager, "SortBottomSheet") // Show sort options
        }

        // Set up the RecyclerView to display transactions
        setUpRecyclerView()

        // Load user details and set up view model observers
        setUpUserDetails()

        // Return the root view of the binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title for the activity to "Transactions"
        (activity as? MainActivity)?.setToolbarTitle("Transactions")
    }

    // Function to set up the RecyclerView for displaying transactions
    private fun setUpRecyclerView() {
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext()) // Use LinearLayout for layout
        binding.recycleView.setHasFixedSize(true) // Improve performance with fixed size
        binding.recycleView.adapter = transactionAdapter // Set the adapter to display transaction items
    }

    // Function to handle navigation to the transaction details screen
    private fun redirectToDetails(transaction: Transaction) {
        // Create a new instance of UpdateTransactionFragment to display transaction details
        val transactionDetailsFragment = UpdateTransactionFragment()
        val bundle = Bundle()
        bundle.putParcelable("transaction", transaction) // Pass the selected transaction as an argument
        transactionDetailsFragment.arguments = bundle

        // Navigate to the UpdateTransactionFragment
        changeCurrentFragment(transactionDetailsFragment)
    }

    // Function to set up user details and observe the view model for transaction updates
    private fun setUpUserDetails() {
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            // Observe the view model to get transactions based on the user ID
            observeViewModel(token, user.id)
        } else {
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }


    // Method to observe the ViewModel for transaction-related data and status updates
    private fun observeViewModel(token: String, userId: String) {
        // Show a progress dialog to indicate loading state
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status of the transaction fetching operation
        transactionViewModel.status.observe(viewLifecycleOwner) { status ->
            // Handle changes in the status (indicates success or failure)
            if (status) {
                // Success: Dismiss the progress dialog
                progressDialog.dismiss()
            } else {
                // Failure: Dismiss the progress dialog
                progressDialog.dismiss()
                // Optionally handle failure case (e.g., show an error message)
            }
        }

        // Observe any messages from the ViewModel
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            // Log the message for debugging purposes
            Log.d("Transactions message", message)

            // Check for specific messages that indicate a timeout or network issue
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    // Dismiss the progress dialog when the timeout dialog is confirmed
                    progressDialog.dismiss()

                    // Show a new progress dialog while reconnecting
                    timeOutDialog.showProgressDialog(requireContext())

                    // Update the progress dialog message to indicate connection attempts
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Connecting...", hideProgressBar = false)

                    // Retry fetching transactions after a timeout
                    transactionViewModel.getAllTransactions(token, userId)
                }
            }
        }

        // Observe the transaction list and set up a custom observer to handle changes
        transactionViewModel.transactionList.observe(viewLifecycleOwner,
            TransactionsObserver(sortBottomSheet, filterBottomSheet, transactionAdapter, binding.amount)
        )

        // Initial call to fetch all transactions for the user
        transactionViewModel.getAllTransactions(token, userId)
    }

    // Method to change the current fragment displayed in the UI
    private fun changeCurrentFragment(fragment: Fragment) {
        // Start a new fragment transaction
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment) // Replace the current fragment with the new one
            .addToBackStack(null) // Add the transaction to the back stack for navigation
            .commit() // Commit the transaction to apply changes
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}