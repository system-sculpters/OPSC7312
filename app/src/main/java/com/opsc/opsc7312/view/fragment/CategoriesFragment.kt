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
import androidx.recyclerview.widget.GridLayoutManager
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCategoriesBinding
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.offline.dbhelpers.CategoryDatabaseHelper
import com.opsc.opsc7312.model.data.offline.dbhelpers.DatabaseHelperProvider
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.CategoryAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.CategoriesObserver


class CategoriesFragment : Fragment() {

    // Binding object for accessing the fragment's UI elements. `_binding` is nullable to
    // ensure proper cleanup when the fragment is destroyed.
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    // List to store category data fetched from the ViewModel.
    private lateinit var categoryList: ArrayList<Category>

    // Adapter for displaying categories in a RecyclerView.
    private lateinit var categoryAdapter: CategoryAdapter

    // ViewModel responsible for managing and processing the category data.
    private lateinit var categoryViewModel: CategoryController

    // Manages user information such as user ID and other session-related details.
    private lateinit var userManager: UserManager

    // Manages authentication tokens required for API calls.
    private lateinit var tokenManager: TokenManager

    // Custom dialog for handling timeout errors or showing progress during API calls.
    private lateinit var timeOutDialog: TimeOutDialog

    //private lateinit var dbHelper: CategoryDatabaseHelper

    private lateinit var dbHelperProvider: CategoryDatabaseHelper


    // Inflates the fragment's view and sets up initial values and components.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding.
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        // Initialize the category list, which will hold the fetched categories.
        categoryList = arrayListOf()

        // Initialize user and token managers to handle session and authentication.
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Get the CategoryController ViewModel for interacting with category data.
        categoryViewModel = ViewModelProvider(this).get(CategoryController::class.java)

        dbHelperProvider = CategoryDatabaseHelper(requireContext())

        // Initialize a custom dialog for showing progress or handling timeouts.
        timeOutDialog = TimeOutDialog()

        // Set up the category adapter with a click listener for category items.
        // It handles both clicking on an existing category and the "create" button.
        categoryAdapter = CategoryAdapter { category ->
            if (category.isCreateButton) {
                // Redirect to the create category screen when the "create" button is clicked.
                redirectToCreate()
            } else {
                // Redirect to the category details screen when an existing category is clicked.
                redirectToDetails(category)
            }
        }

        // Set up the RecyclerView to display the list of categories.
        setUpRecyclerView()

        // Load and display the categories from the ViewModel.
        //setUpCategoriesDetails()

        getCategories()

        // Return the root view for the fragment.
        return binding.root
    }

    // Sets the toolbar title after the view has been created.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title for the fragment to "Categories".
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.categories))
    }

    // Configures the RecyclerView with a grid layout and assigns the category adapter.
    private fun setUpRecyclerView() {
        // Set up a grid layout with 2 columns.
        binding.categoryRecycleView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Ensure the RecyclerView has a fixed size for better performance.
        binding.categoryRecycleView.setHasFixedSize(true)

        // Set the adapter for the RecyclerView to display the categories.
        binding.categoryRecycleView.adapter = categoryAdapter
    }

    private fun getCategories(){
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val user = userManager.getUser()

        try {
            val categories = dbHelperProvider.getAllCategories(user.id)

            // initialize first element as the create button
            val cat0 = Category(isCreateButton = true)

            // Create a new list with cat0 as the first element
            val updatedCategories = listOf(cat0) + categories

            categoryAdapter.updateCategories(updatedCategories)
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error inserting transaction", e)
        }
    }

    // Fetches and observes category details using the ViewModel.
    private fun setUpCategoriesDetails() {
        // Retrieve the current user's details from the UserManager.
        val user = userManager.getUser()

        // Retrieve the authentication token from the TokenManager.
        val token = tokenManager.getToken()

        // If the token is valid, observe the category data through the ViewModel.
        if (token != null) {
            observeViewModel(token, user.id)
        } else {
            // Handle the scenario where the token is null (e.g., log an error or show a message).
        }
    }

    // Observes the ViewModel for changes in category data and handles API responses.
    private fun observeViewModel(token: String, id: String) {
        // Show a progress dialog while the API request is being processed.
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status of the category data API call (success or failure).
        categoryViewModel.status.observe(viewLifecycleOwner) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle the result of the API call and dismiss the progress dialog accordingly.
            if (status) {
                progressDialog.dismiss() // Success
            } else {
                progressDialog.dismiss() // Failure
            }
        }

        // Observe the message LiveData to display any important information to the user.
        categoryViewModel.message.observe(viewLifecycleOwner) { message ->
            // Handle timeout errors or other issues related to network connectivity.
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            if (message == "timeout" || message.contains("Unable to resolve host")) {
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    // Dismiss the current progress dialog and show a new one for retrying.
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.fetch_categories), hideProgressBar = false)

                    // Retry the API call to fetch categories.
                    categoryViewModel.getAllCategories(token, id)
                }
            }
        }

        // Observe the category list and update the UI accordingly.
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        categoryViewModel.categoryList.observe(viewLifecycleOwner, CategoriesObserver(categoryAdapter, null))

        // Initial API call to fetch all categories from the server.
        categoryViewModel.getAllCategories(token, id)
    }

    // Redirects the user to the Create Category screen.
    private fun redirectToCreate() {
        // Create a new instance of CreateCategoryFragment.
        val createCategoryFragment = CreateCategoryFragment()

        // Navigate to the Create Category screen.
        changeCurrentFragment(createCategoryFragment)
    }

    // Redirects the user to the Category Details screen for the selected category.
    private fun redirectToDetails(category: Category) {
        // Create a new instance of UpdateCategoryFragment and pass the selected category data.
        val categoryDetailsFragment = UpdateCategoryFragment()
        val bundle = Bundle()
        bundle.putParcelable("category", category)
        categoryDetailsFragment.arguments = bundle

        // Navigate to the Category Details screen.
        changeCurrentFragment(categoryDetailsFragment)
    }

    // Helper function to change the current fragment in the activity.
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
