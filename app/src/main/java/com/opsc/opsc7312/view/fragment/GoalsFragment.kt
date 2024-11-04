package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentGoalsBinding
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.offline.dbhelpers.CategoryDatabaseHelper
import com.opsc.opsc7312.model.data.offline.dbhelpers.DatabaseHelperProvider
import com.opsc.opsc7312.model.data.offline.dbhelpers.GoalDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.GoalAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.GoalObserver

// GoalsFragment is a Fragment that manages the display and interaction with user goals.
class GoalsFragment : Fragment() {
    // Binding object for accessing the layout views
    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    // Adapter for the RecyclerView to display goals
    private lateinit var goalAdapter: GoalAdapter

    // List to hold the user's goals
    private lateinit var goals: ArrayList<Goal>

    // ViewModel for managing goal-related data and operations
    private lateinit var goalViewModel: GoalController

    // Manager for user-related data
    private lateinit var userManager: UserManager

    // Manager for handling authentication tokens
    private lateinit var tokenManager: TokenManager

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog

    private lateinit var dbHelperProvider: GoalDatabaseHelper

    // Inflates the layout for this fragment and initializes views and variables.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)

        // Initialize the goals list and the adapter with a click listener for each goal item
        goals = ArrayList()
        goalAdapter = GoalAdapter { goal -> redirectToDetails(goal) }

        // Set up a click listener for the add goal button
        binding.addGoal.setOnClickListener {
            redirectToCreate()
        }

        // Initialize user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize the ViewModel for managing goal data
        goalViewModel = ViewModelProvider(this).get(GoalController::class.java)

        dbHelperProvider = GoalDatabaseHelper(requireContext())


        // Initialize the timeout dialog
        timeOutDialog = TimeOutDialog()

        // Set up the RecyclerView and goals
        setUpRecyclerView()
        //setUpGoals()

        getGoals()

        return binding.root // Return the root view of the binding
    }

    // Called after the view has been created. Sets the toolbar title in the MainActivity.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.goals))
    }

    // method that sets up the RecyclerView for displaying the list of goals.
    private fun setUpRecyclerView() {
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext()) // Set the layout manager
        binding.recycleView.setHasFixedSize(true) // Improve performance if the size is fixed
        binding.recycleView.adapter = goalAdapter // Set the adapter for the RecyclerView
    }

    private fun getGoals(){
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val user = userManager.getUser()

        try {
            val goals = dbHelperProvider.getAllGoals(user.id)
            goalAdapter.updateGoals(goals)
            updateGoalProgress(goals)
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error inserting transaction", e)
        }
    }

    private fun updateGoalProgress(goals: List<Goal>) {
        var totalCurrentAmount = 0.0  // Initialize total for current amounts.
        var totalTargetAmount = 0.0    // Initialize total for target amounts.

        // Iterate through each goal to accumulate the current and target amounts.
        for (goal in goals) {
            totalCurrentAmount += goal.currentamount
            totalTargetAmount += goal.targetamount
        }

        // Update the TextView with the formatted amounts reflecting progress.
        binding.amount.text = "${AppConstants.formatAmount(totalCurrentAmount)}/" +
                "${AppConstants.formatAmount(totalTargetAmount)} ZAR"
    }

    //Redirects the user to the CreateGoalFragment to add a new goal.
    private fun redirectToCreate() {
        // Create a new instance of CreateGoalFragment
        val createGoal = CreateGoalFragment()
        changeCurrentFragment(createGoal) // Change the current fragment
    }

    //Redirects the user to the UpdateGoalFragment to view or edit the selected goal.
    private fun redirectToDetails(goal: Goal) {
        // Create a new instance of UpdateGoalFragment and pass the goal data
        val goalDetailsFragment = UpdateGoalFragment()
        val bundle = Bundle().apply {
            putParcelable("goal", goal) // Pass the goal object
            putString("screen", "redirectToDetails goal") // Indicate the source of the navigation
        }
        goalDetailsFragment.arguments = bundle // Set arguments for the fragment

        changeCurrentFragment(goalDetailsFragment) // Change to the goal details fragment
    }

    // Changes the current displayed fragment in the activity.
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

    private fun setUpGoals() {
        val user = userManager.getUser() // Retrieve the user information
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            observeViewModel(token, user.id) // Observe the ViewModel for goal data
        } else {
            // Handle case when token is null (e.g., show an error message or prompt for login)
        }
    }

    private fun observeViewModel(token: String, id: String) {
        // Show a progress dialog while waiting for data
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status LiveData from the ViewModel
        goalViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle changes in the status of goal retrieval (success or failure)
            if (status) {
                // If the status indicates success, dismiss the progress dialog
                progressDialog.dismiss()
            } else {
                // If the status indicates failure, also dismiss the progress dialog
                progressDialog.dismiss()
            }
        }

        // Observe the message LiveData from the ViewModel
        goalViewModel.message.observe(viewLifecycleOwner) { message ->
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
                    goalViewModel.getAllGoals(token, id)
                }
            }
        }

        // Observe the goalList LiveData from the ViewModel with a custom observer

        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        goalViewModel.goalList.observe(viewLifecycleOwner, GoalObserver(goalAdapter, binding.amount))

        // Make an initial API call to retrieve all goals for the user
        goalViewModel.getAllGoals(token, id)
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}