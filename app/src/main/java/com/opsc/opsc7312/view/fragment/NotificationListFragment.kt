package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentNotificationListBinding
import com.opsc.opsc7312.databinding.FragmentPlaceholderBinding
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.api.controllers.NotificationsController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.Notification
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.GoalAdapter
import com.opsc.opsc7312.view.adapter.NotificationAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.GoalObserver
import com.opsc.opsc7312.view.observers.NotificationsObserver


class NotificationListFragment : Fragment() {
    private var _binding: FragmentNotificationListBinding? = null
    private val binding get() = _binding!!

    // Adapter for the RecyclerView to display goals
    private lateinit var notificationAdapter: NotificationAdapter

    // List to hold the user's goals
    private lateinit var notifications: ArrayList<Notification>

    // ViewModel for managing goal-related data and operations
    private lateinit var notificationViewModel: NotificationsController

    // Manager for user-related data
    private lateinit var userManager: UserManager

    // Manager for handling authentication tokens
    private lateinit var tokenManager: TokenManager

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationListBinding.inflate(inflater, container, false)

        // Initialize the goals list and the adapter with a click listener for each goal item
        notifications = ArrayList()
        notificationAdapter = NotificationAdapter { goal -> redirectToDetails(goal) }



        // Initialize user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize the ViewModel for managing goal data
        notificationViewModel = ViewModelProvider(this).get(NotificationsController::class.java)

        // Initialize the timeout dialog
        timeOutDialog = TimeOutDialog()

        // Set up the RecyclerView and goals
        setUpRecyclerView()
        setUpNotifications()
        return binding.root
    }

    // Called after the view has been created. Sets the toolbar title in the MainActivity.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.notification))
    }

    private fun redirectToDetails(notification: Notification) {

    }
    // method that sets up the RecyclerView for displaying the list of goals.
    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set the layout manager
        binding.recyclerView.setHasFixedSize(true) // Improve performance if the size is fixed
        binding.recyclerView.adapter = notificationAdapter // Set the adapter for the RecyclerView
    }

    private fun setUpNotifications() {
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
        notificationViewModel.status.observe(viewLifecycleOwner) { status ->
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
        notificationViewModel.message.observe(viewLifecycleOwner) { message ->
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
                    notificationViewModel.fetchAllNotifications(token, id)
                }
            }
        }

        // Observe the goalList LiveData from the ViewModel with a custom observer

        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        notificationViewModel.notifications.observe(viewLifecycleOwner, NotificationsObserver(notificationAdapter))

        // Make an initial API call to retrieve all goals for the user
        notificationViewModel.fetchAllNotifications(token, id)
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
}