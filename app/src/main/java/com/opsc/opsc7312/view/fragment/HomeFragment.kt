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
import com.opsc.opsc7312.model.data.offline.dbhelpers.CategoryDatabaseHelper
import com.opsc.opsc7312.model.data.offline.dbhelpers.TransactionDatabaseHelper
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

    private lateinit var dbHelperProvider: TransactionDatabaseHelper
    private lateinit var categoryDatabaseHelper: CategoryDatabaseHelper


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

        dbHelperProvider = TransactionDatabaseHelper(requireContext())
        categoryDatabaseHelper = CategoryDatabaseHelper(requireContext())

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
        //setUpUserDetails()
        getTransactions()

        // Set up the RecyclerView to display transactions
        setUpRecyclerView()

        // Return the root view of the fragment
        return binding.root
    }

    // Called immediately after onCreateView. Used for additional setup of the fragment's UI.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title to "Home"
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.home))
    }

    private fun getTransactions(){
        val user = userManager.getUser()

        try {
            val transactions = dbHelperProvider.getAllTransactions(user.id)
            setUpData(transactions)
            val firstThree = transactions.take(3)
            updateTransactionCategory(transactions = firstThree)
            transactionAdapter.updateTransactions(firstThree)
        } catch (e: Exception) {
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

    // Calculates and updates the income, expense, and balance amounts based on the provided list of transactions.
    private fun setUpData(transactionList: List<Transaction>) {
        var totalIncome = 0.0  // Initialize total for income amounts.
        var totalExpense = 0.0  // Initialize total for expense amounts.

        // Create separate lists to hold income and expense transactions.
        val income: ArrayList<Transaction> = arrayListOf()
        val expenses: ArrayList<Transaction> = arrayListOf()

        // Iterate through each transaction to categorize them as income or expense.
        for (transaction in transactionList) {
            if (transaction.type == "INCOME") {
                income.add(transaction)           // Add to income list.
                totalIncome += transaction.amount  // Accumulate total income.
            } else {
                expenses.add(transaction)         // Add to expenses list.
                totalExpense += transaction.amount  // Accumulate total expenses.
            }
        }

        // Calculate the overall balance by subtracting total expenses from total income.
        val totalBalance: Double = totalIncome - totalExpense

        // Update the TextViews with the formatted amounts reflecting balance, income, and expenses.
        "${AppConstants.formatAmount(totalBalance)} ZAR".also { binding.amount.text = it }
        "${AppConstants.formatAmount(totalIncome)} ZAR".also { binding.incomeAmount.text = it }
        "${AppConstants.formatAmount(totalExpense)} ZAR".also { binding.expenseAmount.text = it }
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

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // If successful, update the progress dialog message
                //timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Transaction retrieval successful!", hideProgressBar = true)

                // Dismiss the progress dialog immediately upon success
                progressDialog.dismiss()
            } else {
                // If failed, update the progress dialog message
                //timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Transaction retrieval failed!", hideProgressBar = true)

                // Dismiss the progress dialog immediately upon failure
                progressDialog.dismiss()
            }
        }

        // Observe messages from the ViewModel
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check if the message indicates a timeout scenario

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog to inform the user
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    // Dismiss the previous progress dialog (if it is still visible)
                    progressDialog.dismiss()
                    // Show a new progress dialog while attempting to reconnect
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.connecting), hideProgressBar = false)
                    // Retry fetching transactions after a timeout
                    transactionViewModel.getAllTransactions(token, userId)
                }
            }
        }

        // Observe the list of transactions and update the UI accordingly

        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
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