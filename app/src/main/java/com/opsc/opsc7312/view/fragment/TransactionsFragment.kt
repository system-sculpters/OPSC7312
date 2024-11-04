package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.dbhelpers.CategoryDatabaseHelper
import com.opsc.opsc7312.model.data.offline.dbhelpers.DatabaseHelperProvider
import com.opsc.opsc7312.model.data.offline.dbhelpers.TransactionDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import com.opsc.opsc7312.view.custom.FilterBottomSheet
import com.opsc.opsc7312.view.custom.NotificationHandler
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

    private lateinit var dbHelperProvider: TransactionDatabaseHelper
    private lateinit var categoryDatabaseHelper: CategoryDatabaseHelper

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

        dbHelperProvider = TransactionDatabaseHelper(requireContext())
        categoryDatabaseHelper = CategoryDatabaseHelper(requireContext())
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
        //setUpUserDetails()

        getTransactions()

        // Return the root view of the binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title for the activity to "Transactions"
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.transactions))
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

    private fun getTransactions(){
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        // Retrieve the current user from UserManager
        val user = userManager.getUser()

        try {
            // Fetch all transactions for the current user from the database
            val transactions = dbHelperProvider.getAllTransactions(user.id)

            // Update each transaction with the correct category information
            updateTransactionCategory(transactions = transactions)

            // Update the user's balance based on the retrieved transactions
            updateBalance(transactions)

            // Pass the transactions data to a filter UI component to update the display
            filterBottomSheet.updateTransactions(transactions)

            // Update the transaction adapter with the new transactions for UI display
            transactionAdapter.updateTransactions(transactions)
        } catch (e: Exception) {
            // Log any exceptions encountered during database operations
            Log.e("DatabaseError", "Error inserting transaction", e)
        }
    }

    private fun updateTransactionCategory(transactions: List<Transaction>){
        for (transaction in transactions){
            val category = categoryDatabaseHelper.getCategoryById(transaction.categoryId)

            if (category != null) {
                transaction.category = category
            }
        }
    }

    private fun updateBalance(value: List<Transaction>) {
        var totalIncome = 0.0  // Initialize total income
        var totalExpense = 0.0  // Initialize total expense

        // Iterate over each transaction to calculate total income and expenses
        for (transaction in value) {
            if (transaction.type == AppConstants.TRANSACTIONTYPE.INCOME.name) {
                // Add to total income if the transaction type is income
                totalIncome += transaction.amount
            } else {
                // Add to total expenses if the transaction type is expense
                totalExpense += transaction.amount
            }
        }

        // Calculate the total balance
        val totalBalance = totalIncome - totalExpense

        // Update the amount TextView to display the formatted balance
        binding.amount.text = "${AppConstants.formatAmount(totalBalance)} ZAR"
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

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
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
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

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
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.connecting), hideProgressBar = false)

                    // Retry fetching transactions after a timeout
                    transactionViewModel.getAllTransactions(token, userId)
                }
            }
        }

        // Observe the transaction list and set up a custom observer to handle changes
        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        transactionViewModel.transactionList.observe(viewLifecycleOwner,
            TransactionsObserver(sortBottomSheet, filterBottomSheet, transactionAdapter, binding.amount)
        )

        // Initial call to fetch all transactions for the user
        transactionViewModel.getAllTransactions(token, userId)
    }

    // Method to change the current fragment displayed in the UI
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki

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