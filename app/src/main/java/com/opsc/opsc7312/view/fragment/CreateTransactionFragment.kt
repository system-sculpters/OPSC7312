package com.opsc.opsc7312.view.fragment

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCreateTransactionBinding
import com.opsc.opsc7312.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.DatabaseChangeListener
import com.opsc.opsc7312.model.data.offline.dbhelpers.CategoryDatabaseHelper
import com.opsc.opsc7312.model.data.offline.dbhelpers.DatabaseHelperProvider
import com.opsc.opsc7312.model.data.offline.dbhelpers.GoalDatabaseHelper
import com.opsc.opsc7312.model.data.offline.dbhelpers.TransactionDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.IconAdapter
import com.opsc.opsc7312.view.adapter.SelectCategoryAdapter
import com.opsc.opsc7312.view.custom.NotificationHandler
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.CategoriesObserver
import java.util.UUID


// This class represents a Fragment that allows users to create a new transaction.
class CreateTransactionFragment : Fragment() {
    // Private variable to hold the binding instance for the Fragment's layout
    private var _binding: FragmentCreateTransactionBinding? = null
    private val binding get() = _binding!! // Non-nullable reference to binding

    // List to store different types of transactions
    private lateinit var transactionTypes: List<String>

    // ViewModel for handling transaction-related operations
    private lateinit var transactionViewModel: TransactionController

    // UserManager instance to manage user-related information
    private lateinit var userManager: UserManager

    // TokenManager instance to handle authentication tokens
    private lateinit var tokenManager: TokenManager

    // RecyclerView for displaying icons for category selection
    private lateinit var iconRecyclerView: RecyclerView

    // AlertDialog for selecting icons from a list
    private lateinit var iconPickerDialog: AlertDialog

    // Adapter for managing category selection in the RecyclerView
    private lateinit var adapter: SelectCategoryAdapter

    // ViewModel for managing category-related operations
    private lateinit var categoryViewModel: CategoryController

    // Variable to store error messages for validation
    private var errorMessage = ""

    // Boolean variable indicating if the transaction is recurring
    private var isRecurring = true

    // String variables to store the name and ID of the selected category
    private var selectedIconName: String = "Select a category"
    private var selectedCategoryId: String = ""

    // Instance of TimeOutDialog for managing timeout-related dialogs
    private lateinit var timeOutDialog: TimeOutDialog

    private lateinit var notificationHandler: NotificationHandler

    private lateinit var dbHelperProvider: CategoryDatabaseHelper

    private lateinit var transactionDatabaseHelper: TransactionDatabaseHelper

    // Inflates the Fragment's layout and initializes necessary components
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the Fragment's layout and initialize binding
        _binding = FragmentCreateTransactionBinding.inflate(inflater, container, false)

        // Initialize UserManager and TokenManager instances
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Retrieve the list of transaction types from AppConstants
        transactionTypes = AppConstants.TRANSACTIONTYPE.entries.map { it.name }

        // Initialize ViewModels for transaction and category management
        transactionViewModel = ViewModelProvider(this).get(TransactionController::class.java)

        categoryViewModel = ViewModelProvider(this).get(CategoryController::class.java)

        notificationHandler = NotificationHandler(requireContext())

        dbHelperProvider = CategoryDatabaseHelper(requireContext())


        transactionDatabaseHelper = TransactionDatabaseHelper(requireContext())
        transactionDatabaseHelper.setDatabaseChangeListener(activity as? DatabaseChangeListener)

        // Initialize the TimeOutDialog instance
        timeOutDialog = TimeOutDialog()

        // Create an adapter for selecting categories and set its click listener
        adapter = SelectCategoryAdapter { selectedCategory ->
            // Update the selected icon and category ID
            selectedIconName = selectedCategory.name
            selectedCategoryId = selectedCategory.id
            binding.categoryName.text = selectedIconName

            // Get the current theme color for the category name
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
            val color = typedValue.data
            binding.categoryName.setTextColor(color)

            // Set the selected category icon in the ImageView
            AppConstants.ICONS[selectedCategory.icon]?.let {
                binding.categoryImageView.setImageResource(it)
            }

            // Update the background color of the category image container
            val colorResId = AppConstants.COLOR_DICTIONARY[selectedCategory.color]
            if (colorResId != null) {
                val originalColor = ContextCompat.getColor(requireContext(), colorResId)
                binding.categoryImageContainer.setBackgroundColor(originalColor)
                val cornerRadius = requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_main)
                val shapeDrawable = GradientDrawable()
                shapeDrawable.setColor(originalColor)
                shapeDrawable.cornerRadius = cornerRadius.toFloat()
                binding.categoryImageContainer.background = shapeDrawable
            }

            // Dismiss the icon picker dialog after selecting an icon
            iconPickerDialog.dismiss()
        }

        // Set up the toggle button for recurring transactions
        toggleButton()

        // Set up input fields for transaction creation
        setUpInputs()

        // Return the root view of the binding
        return binding.root
    }

    // Lifecycle method called after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title to "Create"
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.create_text))
    }

    // Initializes input fields and sets up listeners for user interactions
    private fun setUpInputs() {
        // Get the current user and token
        val user = userManager.getUser()

        getCategories()

        // Set the available transaction types in the dropdown
        binding.transactionType.setItems(transactionTypes)

        // Set up the listener for the icon container click
        binding.iconContainer.setOnClickListener {
            showIconPickerDialog() // Show the icon picker dialog
        }

        // Set up the listener for the submit button
        binding.submitButton.setOnClickListener {
            createTransaction(user.id) // Add the transaction if a token is available
        }
    }

    private fun getCategories(){
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val user = userManager.getUser()

        val categories = dbHelperProvider.getAllCategories(user.id)

        categories.forEach {
            // Log.d("DB TEST", "PIN: ${it.first}, Locker No: ${it.second}, timestamp: ${it.third}")
            Log.d("DB TEST", "categories: ${it}")
        }

        adapter.updateCategories(categories)
    }

    private fun createTransaction(id: String){
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user

        // Show a progress dialog while the transaction is being created
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Retrieve the transaction name and amount from the input fields
        val transactionName = binding.transactionNameEdittext.text.toString()
        val amount = binding.amount.text.toString()

        // Verify the entered data before proceeding
        if (!verifyData(transactionName, amount, selectedCategoryId, binding.transactionType.selectedIndex)) {
            progressDialog.dismiss() // Dismiss the progress dialog if validation fails
            timeOutDialog.showAlertDialog(requireContext(), errorMessage) // Show error message dialog
            errorMessage = "" // Reset error message
            return // Exit the function
        }

        // Get the selected transaction type from the dropdown
        val transactionType = transactionTypes[binding.transactionType.selectedIndex]

        val uniqueID = UUID.randomUUID().toString()

        // Create a new Transaction object with the user input
        val newTransaction = Transaction(
            id = uniqueID,
            name = transactionName,
            amount = amount.toDouble(),
            userid = id,
            isrecurring = isRecurring,
            type = transactionType,
            categoryId = selectedCategoryId
        )

        val isInserted = transactionDatabaseHelper.addTransaction(newTransaction)


        progressDialog.dismiss()

        if(isInserted){
            val notificationTitle = getString(R.string.goal_created)
            val notificationMessage = "Your Goal '${newTransaction.name}' has been created successfully."
            notificationHandler.createNotificationChannel()
            notificationHandler.showNotification(notificationTitle, notificationMessage)

            redirectToTransactions()
        } else {
            // Handle the case where the category was not inserted
            timeOutDialog.showAlertDialog(requireContext(), getString(R.string.create_transaction_failed))
        }
    }


    // Displays a dialog for the user to pick an icon for the transaction category
    private fun showIconPickerDialog() {
        // Inflate the icon picker dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.icon_picker_dialog, null)

        // Initialize the RecyclerView for icon selection
        iconRecyclerView = dialogView.findViewById(R.id.icon_recycler_view)
        iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // Set layout manager with a span count of 3
        iconRecyclerView.adapter = adapter // Set the adapter for the RecyclerView

        // Create and show the icon picker dialog
        iconPickerDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        iconPickerDialog.show() // Show the dialog
    }

    // Sets up the toggle button for selecting whether the transaction is recurring
    private fun toggleButton() {
        // This radio button setOnClickListener was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        binding.isRecurring.addOnButtonCheckedListener { group, checkedId, isChecked ->
            // Check which button is selected and update the recurring status and UI accordingly
            when (checkedId) {
                R.id.toggleYes -> if (isChecked) {
                    isRecurring = true // Set the recurring status to true
                    binding.toggleYes.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary) // Update background color
                    binding.toggleYes.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white)) // Update text color

                    // Update the appearance of the "No" button
                    binding.toggleNo.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.white))
                    binding.toggleNo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }
                R.id.toggleNo -> if (isChecked) {
                    isRecurring = false // Set the recurring status to false
                    binding.toggleNo.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)) // Update background color
                    binding.toggleNo.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white)) // Update text color

                    // Update the appearance of the "Yes" button
                    binding.toggleYes.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.white))
                    binding.toggleYes.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }
            }
        }
    }

    // Adds a new transaction based on the user input
    private fun addTransaction(token: String, id: String) {
        // Show a progress dialog while the transaction is being created
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Retrieve the transaction name and amount from the input fields
        val transactionName = binding.transactionNameEdittext.text.toString()
        val amount = binding.amount.text.toString()

        // Verify the entered data before proceeding
        if (!verifyData(transactionName, amount, selectedCategoryId, binding.transactionType.selectedIndex)) {
            progressDialog.dismiss() // Dismiss the progress dialog if validation fails
            timeOutDialog.showAlertDialog(requireContext(), errorMessage) // Show error message dialog
            errorMessage = "" // Reset error message
            return // Exit the function
        }

        // Get the selected transaction type from the dropdown
        val transactionType = transactionTypes[binding.transactionType.selectedIndex]

        // Create a new Transaction object with the user input
        val newTransaction = Transaction(
            name = transactionName,
            amount = amount.toDouble(),
            userid = id,
            isrecurring = isRecurring,
            type = transactionType,
            categoryId = selectedCategoryId
        )

        // Log the new transaction for debugging purposes
        Log.d("newTransaction", "this is the transaction: $newTransaction")

        // Observe the status of the transaction creation process
        transactionViewModel.status.observe(viewLifecycleOwner) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Show success message if transaction creation is successful
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.create_transaction_successful), hideProgressBar = true)

                // Dismiss the dialog after a delay
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the progress dialog
                    redirectToTransactions() // Navigate to the Transactions screen
                }, 2000)
            } else {
                // Show error message if transaction creation fails
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.create_transaction_fail), hideProgressBar = true)

                // Dismiss the dialog after a delay
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the progress dialog
                }, 2000)
            }
        }

        // Observe the 'message' LiveData from the transactionViewModel to handle different message responses
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check if the message indicates a timeout or if the host could not be resolved
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog to inform the user about the connection issue
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    // Dismiss the current progress dialog when the timeout dialog is confirmed
                    progressDialog.dismiss()
                    // Show a new progress dialog to indicate that a reconnection attempt is being made
                    timeOutDialog.showProgressDialog(requireContext())
                    // Update the progress dialog message to inform the user that the application is attempting to connect
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.connecting), hideProgressBar = false)
                    // Attempt to create the transaction again after the user acknowledges the timeout dialog
                    transactionViewModel.createTransaction(token, newTransaction)
                }
            }
        }

        // Trigger the ViewModel to create the new transaction
        transactionViewModel.createTransaction(token, newTransaction)
    }

    // Verifies the user input data before creating a transaction
    private fun verifyData(transactionName: String, amount: String, selectedCategory: String, transactionType: Int): Boolean {
        var errors = 0

        // Check if the transaction name is empty or blank
        if (transactionName.isBlank()) {
            errorMessage += "${getString(R.string.enter_transaction_name)}\n"  // Append error message for empty transaction name
            errors += 1  // Increment error count
        }

        // Check if the amount is empty or blank
        if (amount.isBlank()) {
            errorMessage += "${getString(R.string.enter_transaction_amount)}\n"  // Append error message for empty amount
            errors += 1  // Increment error count
        }

        // Check if a category has been selected
        if (selectedCategory.isBlank()) {
            errorMessage += "${getString(R.string.enter_category)}\n"  // Append error message for no category selection
            errors += 1  // Increment error count
        }

        // Check if the transaction type is valid (not selected)
        if (transactionType == -1) {
            errorMessage += getString(R.string.enter_transaction_type)  // Append error message for no transaction type selected
            errors += 1  // Increment error count
        }

        // Return true if no errors were found; otherwise, return false
        return errors == 0
    }

    private fun observeViewModel(token: String, id: String) {
        // Show a progress dialog to indicate loading
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status of the category retrieval
        categoryViewModel.status.observe(viewLifecycleOwner) { status ->
            // Handle status changes (success or failure)
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Success case
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.category_retrieval_successful), hideProgressBar = true)

                // Dismiss the dialog immediately after success
                progressDialog.dismiss()

            } else {
                // Failure case
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.category_retrieval_fail), hideProgressBar = true)

                // Dismiss the dialog after a delay of 2 seconds to show the failure message
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()  // Dismiss the dialog after the delay
                }, 2000)
            }
        }

        // Observe messages for timeout or connection issues
        categoryViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                progressDialog.dismiss()  // Dismiss the current progress dialog
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    // Retry logic: dismiss the current dialog and show a new one
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.connecting), hideProgressBar = false)
                    categoryViewModel.getAllCategories(token, id)  // Retry fetching categories
                }
            }
        }

        // Observe the category list and update the UI with the retrieved categories
        categoryViewModel.categoryList.observe(viewLifecycleOwner, CategoriesObserver(null, adapter))

        // Initial API call to retrieve all categories
        categoryViewModel.getAllCategories(token, id)
    }


    private fun redirectToTransactions(){
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        val transactionsFragment = TransactionsFragment()

        // Navigate to CategoryDetailsFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, transactionsFragment)
            .addToBackStack(null)
            .commit()
    }

    // Clean up resources and bindings when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }
}