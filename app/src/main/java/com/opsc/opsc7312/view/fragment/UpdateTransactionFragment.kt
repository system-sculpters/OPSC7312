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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentUpdateTransactionBinding
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.SelectCategoryAdapter
import com.opsc.opsc7312.view.adapter.TransactionAdapter.ViewHolder
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.CategoriesObserver


class UpdateTransactionFragment : Fragment() {
    // Binding object to access views in the layout
    private var _binding: FragmentUpdateTransactionBinding? = null
    private val binding get() = _binding!!

    // List of transaction types
    private lateinit var transactionTypes: List<String>

    // ViewModel for managing transaction data
    private lateinit var transactionViewModel: TransactionController

    // User and token management
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    // UI elements for icon selection
    private lateinit var iconRecyclerView: RecyclerView
    private lateinit var iconPickerDialog: AlertDialog

    // Adapter for selecting category icons
    private lateinit var adapter: SelectCategoryAdapter

    // ViewModel for managing category data
    private lateinit var categoryViewModel: CategoryController

    // Flag to indicate if the transaction is recurring
    private var isRecurring = true

    // Variables to hold selected category information
    private var selectedIconName: String = ""
    private var selectedCategoryId: String = ""

    // Dialog for timeout handling
    private lateinit var timeOutDialog: TimeOutDialog

    // ID of the transaction being updated
    private var transactionId = ""

    // Name of the transaction being updated
    private var transactionName = ""

    // Variable to store error messages
    private var errorMessage = ""

    // Inflate the layout and initialize necessary components
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateTransactionBinding.inflate(inflater, container, false)

        // Initialize user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Load available transaction types
        transactionTypes = AppConstants.TRANSACTIONTYPE.entries.map { it.name }

        // Initialize ViewModels
        transactionViewModel = ViewModelProvider(this).get(TransactionController::class.java)
        categoryViewModel = ViewModelProvider(this).get(CategoryController::class.java)

        // Initialize timeout dialog
        timeOutDialog = TimeOutDialog()

        // Setup adapter for selecting category icons
        adapter = SelectCategoryAdapter { selectedCategory ->
            // Set the selected icon to the ImageView and dismiss the dialog
            setUpCategory(selectedCategory)
            iconPickerDialog.dismiss()
        }

        selectedIconName = getString(R.string.icon_selection)

        // Set up toggle buttons and input fields
        toggleButton()
        setUpInputs()

        // Load transaction details if available
        loadTransactionDetails()

        return binding.root
    }

    // Set toolbar title when view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.details))
    }

    // Load transaction details from arguments
    private fun loadTransactionDetails(){
        val transaction = arguments?.getParcelable<Transaction>("transaction")

        // Update UI with category data if transaction is not null
        if (transaction != null){
            Log.d("", "this is the cat: $transaction")

            // Set transaction category and ID
            val transactionCategory = transaction.category
            transactionCategory.id = transaction.categoryId
            transactionId = transaction.id
            transactionName = transaction.name
            // Set selected transaction type index
            val selectedIndex = transactionTypes.indexOf(transaction.type)

            // Populate UI fields with transaction details
            binding.transactionNameEdittext.setText(transaction.name)
            binding.transactionType.selectItemByIndex(selectedIndex)
            binding.amount.setText(AppConstants.formatAmount(transaction.amount))
            setUpCategory(transactionCategory)

            // Set toggle button for recurring transaction
            if (transaction.isrecurring) {
                binding.isRecurring.check(R.id.toggleYes)
            } else {
                binding.isRecurring.check(R.id.toggleNo)
            }
        }
    }

    // Set up input fields and listeners
    private fun setUpInputs(){
        // Get user and token information
        val user = userManager.getUser()
        val token = tokenManager.getToken()

        if (token != null) {
            observeViewModel(token, user.id) // Observe ViewModel for updates
        }

        // Set available transaction types to the dropdown
        binding.transactionType.setItems(transactionTypes)

        // Show icon picker dialog when the icon container is clicked
        binding.iconContainer.setOnClickListener {
            showIconPickerDialog()
        }

        // Add transaction when the submit button is clicked
        binding.submitButton.setOnClickListener {
            if (token != null) {
                updateTransaction(token)
            }
        }

        binding.deleteButton.setOnClickListener {
            if(token != null){
                showCustomDeleteDialog(token)
            } else{

            }
        }

    }

    // Show a dialog for selecting an icon
    private fun showIconPickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.icon_picker_dialog, null)

        // Set up the RecyclerView for icon selection
        iconRecyclerView= dialogView.findViewById(R.id.icon_recycler_view)
        iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // Adjust the span count as needed
        iconRecyclerView.adapter = adapter

        // Create and show the dialog
        iconPickerDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        iconPickerDialog.show()
    }

    // Handle toggle button selection for recurring transactions
    private fun toggleButton(){
        binding.isRecurring.addOnButtonCheckedListener { group, checkedId, isChecked ->
            // This radio button setOnClickListener was adapted from geeksforgeeks
            // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
            // bibeksah36
            // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
            when (checkedId) {
                R.id.toggleYes -> if (isChecked) {
                    isRecurring = true
                    binding.toggleYes.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
                    binding.toggleYes.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                    binding.toggleNo.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.white))
                    binding.toggleNo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }
                R.id.toggleNo -> if (isChecked) {
                    isRecurring = false
                    binding.toggleNo.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary))
                    binding.toggleNo.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                    binding.toggleYes.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.white))
                    binding.toggleYes.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                }
            }
        }
    }


    private fun updateTransaction(token: String) {
        // Show a progress dialog to indicate the transaction update process has started
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Retrieve the transaction name and amount from the UI
        val transactionName = binding.transactionNameEdittext.text.toString()
        val amount = binding.amount.text.toString()

        // Verify that the input data is valid
        if (!verifyData(transactionName, amount, selectedCategoryId, binding.transactionType.selectedIndex)) {
            // Dismiss the progress dialog if data verification fails
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = "" // Reset error message
            return
        }

        // Get the transaction type based on the selected index
        val transactionType = transactionTypes[binding.transactionType.selectedIndex]

        // Create an updated Transaction object with the new values
        val updatedTransaction = Transaction(
            name = transactionName,
            amount = amount.toDouble(),
            isrecurring = isRecurring,
            type = transactionType,
            categoryId = selectedCategoryId
        )

        Log.d("newTransaction", "this is the transaction: $updatedTransaction")

        // Observe the status of the transaction update
        transactionViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Show a success message and dismiss the dialog after 2 seconds
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_transaction_successful), hideProgressBar = true)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the progress dialog
                    redirectToTransactions() // Navigate back to the transactions list
                }, 2000)
            } else {
                // Show a failure message and dismiss the dialog after 2 seconds
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_transaction_fail), hideProgressBar = true)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the progress dialog
                }, 2000)
            }
        }

        // Observe messages for timeouts or connectivity issues
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss() // Dismiss the previous dialog
                    timeOutDialog.showProgressDialog(requireContext()) // Show a new progress dialog
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.connecting), hideProgressBar = false)
                    transactionViewModel.updateTransaction(token, transactionId, updatedTransaction) // Retry updating the transaction
                }
            }
        }

        // Initiate the update transaction request
        transactionViewModel.updateTransaction(token, transactionId, updatedTransaction)
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
        // Show a progress dialog while fetching categories
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status of the category retrieval
        categoryViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Show a success message when categories are retrieved
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Categories retrieved successfully!", hideProgressBar = true)

                progressDialog.dismiss() // Dismiss the progress dialog

            } else {
                // Show a failure message if category retrieval fails
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Category retrieval failed!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the progress dialog
                }, 2000)
            }
        }

        // Observe messages for timeouts or connectivity issues
        categoryViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss() // Dismiss the previous dialog
                    timeOutDialog.showProgressDialog(requireContext()) // Show a new progress dialog
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Connecting...", hideProgressBar = false)
                    categoryViewModel.getAllCategories(token, id) // Retry fetching categories
                }
            }
        }

        // Observe the category list and set it to the adapter
        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        categoryViewModel.categoryList.observe(viewLifecycleOwner, CategoriesObserver(null, adapter))

        // Example API call to fetch categories
        categoryViewModel.getAllCategories(token, id)
    }

    private fun setUpCategory(selectedCategory: Category) {
        // Store the selected category name and ID for future reference
        selectedIconName = selectedCategory.name
        selectedCategoryId = selectedCategory.id

        // Set the category name text view with the selected category name
        binding.categoryName.text = selectedIconName

        // Set the category-related visuals based on whether it's uncategorized or not
        val isUncategorized = selectedCategoryId == AppConstants.UNCATEGORIZED

        // Create a rounded corner drawable with the selected color for the image container
        val cornerRadius = requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_main)
        val shapeDrawable = GradientDrawable()
        shapeDrawable.cornerRadius = cornerRadius.toFloat()

        if(isUncategorized){
            val categoryBackgroundColor = getTextColor()  // Use textColor as background
            val iconImageResId = R.drawable.baseline_close_24
            binding.categoryImageView.setImageResource(iconImageResId)
            binding.categoryImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))

            shapeDrawable.setColor(categoryBackgroundColor)

            binding.categoryImageContainer.background = shapeDrawable
        } else{

            binding.categoryImageView.clearColorFilter()
            // Set the category image view with the icon associated with the selected category
            AppConstants.ICONS[selectedCategory.icon]?.let {
                binding.categoryImageView.setImageResource(it)
            }

            // Retrieve the color resource ID associated with the selected category color
            val colorResId = AppConstants.COLOR_DICTIONARY[selectedCategory.color]
            if (colorResId != null) {
                // Get the actual color value and set it as the background color for the image container
                val originalColor = ContextCompat.getColor(requireContext(), colorResId)
                binding.categoryImageContainer.setBackgroundColor(originalColor)

                shapeDrawable.setColor(originalColor)

                binding.categoryImageContainer.background = shapeDrawable
            }
            // Retrieve the theme background border color from the app's theme attributes
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
            val color = typedValue.data

            // Set the text color of the category name to the retrieved color
            binding.categoryName.setTextColor(color)
        }
    }

    private fun showCustomDeleteDialog(token: String) {
        // Inflate the custom dialog view

        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // naved_alam
        // https://www.geeksforgeeks.org/user/naved_alam/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val dialogView = layoutInflater.inflate(R.layout.delete_dialog, null)

        // Create the AlertDialog using a custom view
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Find the dialog views
        val confirmButton: LinearLayout = dialogView.findViewById(R.id.confirmButton)
        val cancelButton: LinearLayout = dialogView.findViewById(R.id.cancelButton)
        val titleTextView: TextView = dialogView.findViewById(R.id.titleTextView)
        val messageTextView: TextView = dialogView.findViewById(R.id.messageTextView)

        // Optionally set a custom title or message if needed
        titleTextView.text = "'${transactionName}'"
        messageTextView.text = "Are you sure you want to delete \nthis transaction?"

        // Set click listeners for the buttons
        confirmButton.setOnClickListener {
            // Check if the token is available before updating the category
            deleteTransaction(token) // Call method to update the category
            dialogBuilder.dismiss()  // Close the dialog
        }

        cancelButton.setOnClickListener {
            // Just close the dialog without doing anything
            dialogBuilder.dismiss()
        }

        // Show the dialog
        dialogBuilder.show()
    }

    // Updates the category information in the database using the provided token and user ID.
    private fun deleteTransaction(token: String) {
        // Show a progress dialog to indicate that the category update is in progress.
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status of the category update operation.
        transactionViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Show a success message and redirect to categories after a delay.
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Transaction deleted successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds and redirect to the categories screen.
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                   redirectToTransactions()
                }, 2000)
            } else {
                // Show a failure message and dismiss the progress dialog after a delay.
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Transaction deletion failed!", hideProgressBar = true)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                }, 2000)
            }
        }

        // Observe messages from the ViewModel for timeout or connection issues.
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and retry updating the category.
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Connecting...", hideProgressBar = false)
                    transactionViewModel.deleteTransaction(token, transactionId)
                }
            }
        }

        // Initiate the category update operation in the ViewModel.
        transactionViewModel.deleteTransaction(token, transactionId)
    }

    private fun redirectToTransactions() {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki

        // Create a new instance of the TransactionsFragment
        val transactionsFragment = TransactionsFragment()

        // Replace the current fragment with TransactionsFragment and add the transaction to the back stack
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, transactionsFragment)
            .addToBackStack(null) // Allows the user to navigate back to the previous fragment
            .commit() // Commit the transaction
    }

    // Fetches the theme-based text color for uncategorized transactions
    private fun getTextColor(): Int {
        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
        return typedValue.data
    }
}