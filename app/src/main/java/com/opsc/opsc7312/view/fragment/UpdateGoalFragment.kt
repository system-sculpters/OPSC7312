package com.opsc.opsc7312.view.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentUpdateGoalBinding
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.TimeOutDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


// Fragment for updating the goal details in the application.
class UpdateGoalFragment : Fragment() {
    // Binding variable for the fragment layout
    private var _binding: FragmentUpdateGoalBinding? = null
    private val binding get() = _binding!!

    // List to hold contribution types
    private lateinit var contributionTypes: List<String>

    // ViewModel for managing goal-related data
    private lateinit var goalViewModel: GoalController

    // User manager for retrieving user details
    private lateinit var userManager: UserManager

    // Token manager for handling authentication tokens
    private lateinit var tokenManager: TokenManager

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog

    // ID of the goal being updated/deleted
    private var goalId = ""

    // Name of the goal being updated/deleted
    private var goalName = ""

    // Variable to hold error messages
    private var errorMessage = ""

    // Inflate the layout for this fragment and initialize necessary components
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout using view binding
        _binding = FragmentUpdateGoalBinding.inflate(inflater, container, false)

        // Initialize user manager and token manager
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Get the list of contribution types from the constants
        contributionTypes = AppConstants.CONTRIBUTIONTYPE.entries.map { it.name }

        // Initialize the ViewModel for goal management
        goalViewModel = ViewModelProvider(this).get(GoalController::class.java)

        // Initialize the timeout dialog
        timeOutDialog = TimeOutDialog()

        // Set up input fields and load existing transaction details
        setUpInputs()
        loadTransactionDetails()

        // Return the root view of the binding
        return binding.root
    }

    // Set the toolbar title in the MainActivity after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle("Details")
    }

    // Load transaction details from the passed arguments and update the UI
    private fun loadTransactionDetails() {
        // Retrieve the goal object from fragment arguments
        val goal = arguments?.getParcelable<Goal>("goal")

        // If the goal is not null, populate the UI elements with its data
        if (goal != null) {
            Log.d("goal", "this is the goal: $goal")
            val selectedIndex = contributionTypes.indexOf(goal.contributiontype) // Get index of the contribution type
            goalId = goal.id // Store the goal ID
            goalName = goal.name  // Store the goal Name
            binding.goalName.setText(goal.name) // Set the goal name
            binding.targetAmount.setText(AppConstants.formatAmount(goal.targetamount)) // Set the target amount
            binding.currentAmount.setText(AppConstants.formatAmount(goal.currentamount)) // Set the current amount
            binding.selectedDateText.setText(AppConstants.longToDate(goal.deadline)) // Set the selected date
            binding.contributionType.selectItemByIndex(selectedIndex) // Select the contribution type
            binding.contributionAmount.setText(AppConstants.formatAmount(goal.currentamount)) // Set contribution amount
        }
    }

    // Set up the input fields and their event listeners
    private fun setUpInputs() {
        // Retrieve the current user and token
        val user = userManager.getUser()
        val token = tokenManager.getToken()

        // Initialize the contribution type dropdown with available types
        binding.contributionType.setItems(contributionTypes)

        // Set a click listener for the deadline input to show a date picker dialog
        binding.deadline.setOnClickListener {
            showDatePickerDialog()
        }

        // Set a click listener for the submit button to gather and update goal details
        binding.submitButton.setOnClickListener {
            setUpCategoriesDetails(token, user.id)
        }

        binding.deleteButton.setOnClickListener {
            if(token != null){
                showCustomDeleteDialog(token)
            } else{

            }
        }
    }

    // Show a date picker dialog to allow the user to select a date
    private fun showDatePickerDialog() {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/45842167/how-to-use-datepickerdialog-in-kotlin
        // Derek
        // https://stackoverflow.com/users/8195525/derek
        try {
            val calendar = Calendar.getInstance() // Create a calendar instance
            val year = calendar.get(Calendar.YEAR) // Get the current year
            val month = calendar.get(Calendar.MONTH) // Get the current month
            val day = calendar.get(Calendar.DAY_OF_MONTH) // Get the current day

            // Create and show the date picker dialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->
                    // Update the TextView with the selected date
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.selectedDateText.text = selectedDate
                },
                year,
                month,
                day
            )

            datePickerDialog.show() // Display the dialog
        } catch (e: Exception) {
            e.printStackTrace() // Log any exceptions
        }
    }

    // Gather details and update the goal
    private fun setUpCategoriesDetails(token: String?, userId: String) {

        // If token is not null, proceed to update the goal
        if (token != null) {
            updateGoal(token, userId)
        } else {
            // Handle cases where the token is null (e.g., show an error message)
        }
    }

    // Updates the goal with the provided token and user ID
    private fun updateGoal(token: String, id: String) {
        // Show a progress dialog to indicate the update process is ongoing
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Retrieve input values from the UI
        val name = binding.goalName.text.toString()
        val targetAmount = binding.targetAmount.text.toString()
        var currentAmount = binding.currentAmount.text.toString()
        val deadlineText = binding.selectedDateText.text.toString()
        val contributionType = binding.contributionType
        val contributionAmount = binding.contributionAmount.text.toString()

        // Convert the deadline string to a long value (timestamp)
        var deadline = 0L
        if (deadlineText.isNotBlank()) {
            deadline = AppConstants.convertStringToLong(deadlineText)
        }

        // Default current amount to "0" if it's blank
        if (currentAmount.isBlank()) {
            currentAmount = "0"
        }

        // Validate the input data; if validation fails, show an error and exit
        if (!validateData(name, targetAmount, contributionType.selectedIndex, contributionAmount)) {
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        // Create an updated Goal object with the provided input values
        val updatedGoal = Goal(
            userid = id,
            name = name,
            targetamount = targetAmount.toDouble(),
            currentamount = currentAmount.toDouble(),
            deadline = deadline,
            contributionamount = contributionAmount.toDouble(),
            contributiontype = contributionTypes[contributionType.selectedIndex]
        )

        // Observe the status of the goal update operation
        goalViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Update progress dialog to show success message
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_goal_successful), hideProgressBar = true)

                // Dismiss the dialog after a delay and navigate to GoalsFragment
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    changeCurrentFragment(GoalsFragment())
                }, 2000)

            } else {
                // Update progress dialog to show failure message
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_goal_fail), hideProgressBar = true)

                // Dismiss the dialog after a delay
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                }, 2000)
            }
        }

        // Observe messages from the ViewModel for handling timeouts or errors
        goalViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Creating Goal...", hideProgressBar = false)
                    goalViewModel.updateGoal(token, goalId, updatedGoal) // Retry updating the goal
                }
            }
        }

        // Trigger the goal update operation in the ViewModel
        goalViewModel.updateGoal(token, goalId, updatedGoal)
    }

    // Validates the input data for goal creation
    private fun validateData(name: String, targetAmount: String, selectedIndex: Int, contributionAmount: String): Boolean {
        var errors = 0

        // Check if the goal name is blank
        if (name.isBlank()) {
            errors += 1
            errorMessage += "${getString(R.string.enter_goal_name)}\n"
        }

        // Check if the target amount is blank
        if (targetAmount.isBlank()) {
            errorMessage += "${getString(R.string.enter_target_amount)}\n"
            errors += 1
        }

        // Check if a contribution type has been selected
        if (selectedIndex == -1) {
            errorMessage += "${getString(R.string.contribution_type)}\n"
            errors += 1
        }

        // Check if the contribution amount is blank
        if (contributionAmount.isBlank()) {
            errorMessage += getString(R.string.contribution_amount)
            errors += 1
        }

        // Return true if there are no validation errors
        return errors == 0
    }
    // Changes the current displayed fragment to the specified fragment
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment) // Replace the current fragment
            .addToBackStack(null) // Add the transaction to the back stack
            .commit() // Commit the transaction
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
        titleTextView.text = "'${goalName}'"
        messageTextView.text = "Are you sure you want to delete \nthis goal?"

        // Set click listeners for the buttons
        confirmButton.setOnClickListener {
            // Check if the token is available before updating the category
            deleteGoal(token) // Call method to update the category
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
    private fun deleteGoal(token: String) {
        // Show a progress dialog to indicate that the category update is in progress.
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status of the category update operation.
        goalViewModel.status.observe(viewLifecycleOwner) { status ->
            if (status) {
                // Show a success message and redirect to categories after a delay.
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Goal deleted successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds and redirect to the categories screen.
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    changeCurrentFragment(GoalsFragment())
                }, 2000)
            } else {
                // Show a failure message and dismiss the progress dialog after a delay.
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Goal deletion failed!", hideProgressBar = true)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                }, 2000)
            }
        }

        // Observe messages from the ViewModel for timeout or connection issues.
        goalViewModel.message.observe(viewLifecycleOwner) { message ->
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and retry updating the category.
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Connecting...", hideProgressBar = false)
                    goalViewModel.deleteGoal(token, goalId)
                }
            }
        }

        // Initiate the category update operation in the ViewModel.
        goalViewModel.deleteGoal(token, goalId)
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}