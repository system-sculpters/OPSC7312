package com.opsc.opsc7312.view.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCreateGoalBinding
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.offline.DatabaseChangeListener
import com.opsc.opsc7312.model.data.offline.dbhelpers.DatabaseHelperProvider
import com.opsc.opsc7312.model.data.offline.dbhelpers.GoalDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.NotificationHandler
import com.opsc.opsc7312.view.custom.TimeOutDialog
import java.util.Calendar
import java.util.UUID


class CreateGoalFragment : Fragment() {
    // Binding object for accessing views in the Fragment's layout
    private var _binding: FragmentCreateGoalBinding? = null
    private val binding get() = _binding!!

    // List to hold different types of contributions for goal settings
    private lateinit var contributionTypes: List<String>

    // ViewModel responsible for handling goal-related data
    private lateinit var goalViewModel: GoalController

    // Manager for handling user information
    private lateinit var userManager: UserManager

    // Manager for handling authentication tokens
    private lateinit var tokenManager: TokenManager

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog

    // Variable to hold error messages for validation purposes
    private var errorMessage = ""

    private lateinit var notificationHandler: NotificationHandler

    private lateinit var dbHelperProvider: GoalDatabaseHelper

    // Inflates the layout and initializes components when the view is created
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this Fragment
        _binding = FragmentCreateGoalBinding.inflate(inflater, container, false)

        // Initialize UserManager instance for managing user data
        userManager = UserManager.getInstance(requireContext())

        // Initialize TokenManager instance for managing authentication tokens
        tokenManager = TokenManager.getInstance(requireContext())

        // Map contribution types from AppConstants to a list
        contributionTypes = AppConstants.CONTRIBUTIONTYPE.entries.map { it.name }

        // Initialize ViewModel for goal-related operations
        goalViewModel = ViewModelProvider(this).get(GoalController::class.java)

        notificationHandler = NotificationHandler(requireContext())

        dbHelperProvider = GoalDatabaseHelper(requireContext())

        dbHelperProvider.setDatabaseChangeListener(activity as? DatabaseChangeListener)

        // Initialize the dialog for timeout handling
        timeOutDialog = TimeOutDialog()

        // Set up input fields and listeners
        setUpInputs()

        // Return the root view for this Fragment
        return binding.root
    }

    // Called after the view has been created, useful for additional setup
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title to "Create"
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.create_text))
    }

    // Set up input fields for the goal creation
    private fun setUpInputs() {
        // Initialize the contribution type dropdown with available options
        binding.contributionType.setItems(contributionTypes)

        // Set a click listener to show the date picker dialog for selecting a deadline
        binding.deadline.setOnClickListener {
            showDatePickerDialog()
        }

        // Set a click listener for the submit button to proceed with goal creation
        binding.submitButton.setOnClickListener {
            //setUpCategoriesDetails()
            addGoal()
        }
    }

    // Displays a date picker dialog to select a deadline
    private fun showDatePickerDialog() {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/45842167/how-to-use-datepickerdialog-in-kotlin
        // Derek
        // https://stackoverflow.com/users/8195525/derek
        try {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

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

            datePickerDialog.show()
        } catch (e: Exception) {
            // Print the stack trace if an exception occurs
            e.printStackTrace()
        }
    }

    // Prepares to set up category details for the goal
    private fun setUpCategoriesDetails() {
        // Retrieve the current user from UserManager
        val user = userManager.getUser()

        // Retrieve the current authentication token
        val token = tokenManager.getToken()

        // If the token is available, proceed to add the goal
        if (token != null) {
            addGoal(token, user.id)
        } else {
            // Handle case where token is null (not implemented here)
        }
    }

    // Prepares to set up category details for the goal
    private fun addGoal() {
        // Retrieve the current user from UserManager
        val user = userManager.getUser()

        // Retrieve the current authentication token
        val token = tokenManager.getToken()

        // If the token is available, proceed to add the goal
        addNewGoal(user.id)
    }

    private fun addNewGoal(id: String){
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user

        // Show a progress dialog while the goal is being created
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Collect goal details from input fields
        val name = binding.goalName.text.toString()
        val targetAmount = binding.targetAmount.text.toString()
        var currentAmount = binding.currentAmount.text.toString()
        val deadlineText = binding.selectedDateText.text.toString()
        val contributionType = binding.contributionType
        val contributionAmount = binding.contributionAmount.text.toString()

        // Convert the deadline from string to long if not blank
        var deadline = 0L
        if (deadlineText.isNotBlank()) {
            deadline = AppConstants.convertStringToLong(deadlineText)
        }

        // Set current amount to 0 if it is blank
        if (currentAmount.isBlank()) {
            currentAmount = "0"
        }

        // Validate the input data before proceeding
        if (!validateData(name, targetAmount, contributionType.selectedIndex, contributionAmount)) {
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        //progressDialog.dismiss()

        val uniqueID = UUID.randomUUID().toString()

        // Create a new Goal object with the provided details
        val newGoal = Goal(
            id= uniqueID,
            userid = id,
            name = name,
            targetamount = targetAmount.toDouble(),
            currentamount = currentAmount.toDouble(),
            deadline = deadline,
            contributionamount = contributionAmount.toDouble(),
            contributiontype = contributionTypes[contributionType.selectedIndex]
        )

        val isInserted = dbHelperProvider.insertGoal(newGoal)

        progressDialog.dismiss()

        if(isInserted != -1L){
            val notificationTitle = getString(R.string.goal_created)
            val notificationMessage = "Your Goal '${newGoal.name}' has been created successfully."
            notificationHandler.createNotificationChannel()
            notificationHandler.showNotification(notificationTitle, notificationMessage)
            getAllGoals()

            redirectToGoals()
        } else {
            // Handle the case where the category was not inserted
            timeOutDialog.showAlertDialog(requireContext(), getString(R.string.create_goal_failed))
        }
    }

    private fun getAllGoals(){
        val user = userManager.getUser()

        val goals = dbHelperProvider.getAllGoals(user.id)
        Log.d("DB TEST", "categories: ${goals.size}")

        goals.forEach {
            Log.d("DB TEST", "categories: ${it}")
        }
    }

    // Initiates the process of adding a goal with provided details
    private fun addGoal(token: String, id: String) {
        // Show a progress dialog while the goal is being created
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Collect goal details from input fields
        val name = binding.goalName.text.toString()
        val targetAmount = binding.targetAmount.text.toString()
        var currentAmount = binding.currentAmount.text.toString()
        val deadlineText = binding.selectedDateText.text.toString()
        val contributionType = binding.contributionType
        val contributionAmount = binding.contributionAmount.text.toString()

        // Convert the deadline from string to long if not blank
        var deadline = 0L
        if (deadlineText.isNotBlank()) {
            deadline = AppConstants.convertStringToLong(deadlineText)
        }

        // Set current amount to 0 if it is blank
        if (currentAmount.isBlank()) {
            currentAmount = "0"
        }

        // Validate the input data before proceeding
        if (!validateData(name, targetAmount, contributionType.selectedIndex, contributionAmount)) {
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }


        // Create a new Goal object with the provided details
        val newGoal = Goal(
            userid = id,
            name = name,
            targetamount = targetAmount.toDouble(),
            currentamount = currentAmount.toDouble(),
            deadline = deadline,
            contributionamount = contributionAmount.toDouble(),
            contributiontype = contributionTypes[contributionType.selectedIndex]
        )

        // Observe the status of goal creation from the ViewModel
        goalViewModel.status.observe(viewLifecycleOwner) { status ->
            // If goal creation is successful, update the progress dialog and redirect
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Goal creation successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds and redirect to goals
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    redirectToGoals()
                }, 2000)
            } else {
                // If goal creation fails, update the progress dialog accordingly
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Goal creation failed!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                }, 2000)
            }
        }

        // Observe messages from the ViewModel for timeout or connectivity issues
        goalViewModel.message.observe(viewLifecycleOwner) { message ->
            // Handle timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Creating Goal...", hideProgressBar = false)
                    goalViewModel.createGoal(token, newGoal) // Retry creating the goal
                }
            }
        }

        // Attempt to create the goal with the ViewModel
        goalViewModel.createGoal(token, newGoal)
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

    // Redirects to the GoalsFragment after a goal has been created
    private fun redirectToGoals() {
        // Create a new instance of GoalsFragment
        val goalsFragment = GoalsFragment()

        // Navigate to the GoalsFragment
        changeCurrentFragment(goalsFragment)
    }

    // Changes the current fragment to the specified fragment
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
