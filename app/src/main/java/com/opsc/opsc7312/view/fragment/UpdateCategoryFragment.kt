package com.opsc.opsc7312.view.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentUpdateCategoryBinding
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Color
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.ColorAdapter
import com.opsc.opsc7312.view.adapter.IconAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog


// Fragment for updating the details of a category
class UpdateCategoryFragment : Fragment() {

    // View binding for the fragment's layout
    private var _binding: FragmentUpdateCategoryBinding? = null
    private val binding get() = _binding!!

    // ViewModel and manager instances
    private lateinit var categoryViewModel: CategoryController
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    // Lists to hold color and icon data
    private lateinit var dataList: ArrayList<Color>
    private lateinit var iconList: ArrayList<Int>
    private lateinit var transactionTypes: List<String>
    private lateinit var messages: MutableList<String>

    // Adapters for displaying colors and icons
    private lateinit var colorAdapter: ColorAdapter
    private lateinit var iconAdapter: IconAdapter

    // RecyclerView and dialog for selecting icons
    private lateinit var iconRecyclerView: RecyclerView
    private lateinit var iconPickerDialog: AlertDialog

    // Variables for selected icon and category ID
    private var selectedIconName: String = "Select an icon"
    private var categoryId: String = ""
    private var categoryName: String = ""

    // Dialog for handling timeout scenarios
    private lateinit var timeOutDialog: TimeOutDialog

    // Error message variable
    private var errorMessage = ""

    // Inflate the fragment's view and set up UI components
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentUpdateCategoryBinding.inflate(inflater, container, false)

        // Initialize data lists
        dataList = arrayListOf<Color>()
        iconList = getIconData()
        messages = arrayListOf()

        // Initialize user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize ViewModel
        categoryViewModel = ViewModelProvider(this).get(CategoryController::class.java)

        // Initialize timeout dialog
        timeOutDialog = TimeOutDialog()

        // Get transaction types from constants
        transactionTypes = AppConstants.TRANSACTIONTYPE.entries.map { it.name }

        // Set up the icon adapter with a click listener
        iconAdapter = IconAdapter(iconList) { selectedIcon ->
            // Set the selected icon to the ImageView and update UI
            selectedIconName = getIconName(selectedIcon)
            binding.iconName.text = selectedIconName

            // Retrieve the theme color for setting text color
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
            val color = typedValue.data
            binding.iconName.setTextColor(color)
            binding.iconImageView.setImageResource(selectedIcon)

            // Dismiss the icon picker dialog after selection
            iconPickerDialog.dismiss()
        }

        // Set up input fields and load category data
        setUpInputs()
        getCategoryData()
        setUpColors()
        loadCategoryDetails()

        return binding.root // Return the root view
    }

    // Called after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title in the MainActivity
        (activity as? MainActivity)?.setToolbarTitle("Details")
    }

    // Load the category details into the UI
    private fun loadCategoryDetails() {
        // Retrieve the category passed as an argument
        val category = arguments?.getParcelable<Category>("category")

        // Update UI components with the category data
        if (category != null) {
            categoryId = category.id

            categoryName = category.name

            val colorId = AppConstants.COLOR_LIST.indexOf(category.color).toString()
            val selectedIndex = transactionTypes.indexOf(category.transactiontype)
            val selectedColor = Color(Id = colorId, name = category.color)
            val selectedIcon = getIconNames().indexOf(category.icon)
            val iconValue = AppConstants.ICONS.entries.find { it.key == category.icon }?.value

            // Populate the UI fields with the category details
            binding.categoryNameEdittext.setText(category.name)
            binding.contributionType.selectItemByIndex(selectedIndex)

            colorAdapter.setSelectedColor(selectedColor)
            iconAdapter.setSelectedIcon(iconValue)

            binding.iconName.text = category.icon

            // Set text color for the icon name
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
            val color = typedValue.data
            binding.iconName.setTextColor(color)
            binding.iconImageView.setImageResource(iconList[selectedIcon])
        }
    }

    // Set up the color list in the UI
    private fun setUpColors() {
        binding.colorList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.colorList.setHasFixedSize(false)

        // Initialize color adapter with a click listener
        colorAdapter = ColorAdapter(dataList) { selectedCategory ->
            Log.d("SelectedCategory", "Selected category: $selectedCategory")
            // Handle the selected category here
        }

        // Set the adapter for the color list
        binding.colorList.adapter = colorAdapter
    }

    // Show the dialog for picking an icon
    private fun showIconPickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.icon_picker_dialog, null)

        // Initialize RecyclerView for displaying icons
        iconRecyclerView = dialogView.findViewById(R.id.icon_recycler_view)
        iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // Set span count for grid layout
        iconRecyclerView.adapter = iconAdapter // Set the icon adapter

        // Create and show the icon picker dialog
        iconPickerDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        iconPickerDialog.show() // Show the dialog
    }

    // Retrieve color data for categories
    private fun getCategoryData(): ArrayList<Color> {
        // Populate the dataList with Color objects based on AppConstants
        for (i in AppConstants.COLOR_LIST.indices) {
            val dataClass = Color(i.toString(), AppConstants.COLOR_LIST[i])
            dataList.add(dataClass)
        }
        return dataList // Return the populated dataList
    }

    // Get the name of an icon based on its drawable ID
    private fun getIconName(iconDrawableId: Int): String {
        // Implement logic to map the icon drawable ID to its corresponding name
        val iconsMap = AppConstants.ICONS
        return iconsMap.entries.find { it.value == iconDrawableId }?.key ?: "Unknown Icon"
    }

    // Get the icon data (drawable IDs)
    private fun getIconData(): ArrayList<Int> {
        val iconsMap = AppConstants.ICONS
        return ArrayList(iconsMap.values) // Return a list of icon drawable IDs
    }

    // Get the names of the icons
    private fun getIconNames(): ArrayList<String> {
        val iconsMap = AppConstants.ICONS
        return ArrayList(iconsMap.keys) // Return a list of icon names
    }

    // Set up input fields and their listeners
    private fun setUpInputs() {
        // Retrieve the current user and token
        val user = userManager.getUser()
        val token = tokenManager.getToken()

        binding.contributionType.setItems(transactionTypes) // Set transaction types in the input

        // Set click listener for the icon container to show the icon picker dialog
        binding.iconContainer.setOnClickListener {
            showIconPickerDialog()
        }

        // Set click listener for the submit button
        binding.submitButton.setOnClickListener {
            if (token != null) {
                updateCategory(token, user.id) // Call method to update the category
            } else {
                // Handle case where token is null (e.g., show error message)
            }        }

        binding.deleteButton.setOnClickListener {


            // Check if the token is available before updating the category
            if (token != null) {
                showCustomDeleteDialog(token) // Call method to update the category
            } else {
                // Handle case where token is null (e.g., show error message)
            }
        }
    }

    // Updates the category information in the database using the provided token and user ID.
    private fun updateCategory(token: String, id: String) {
        // Show a progress dialog to indicate that the category update is in progress.
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Retrieve the category name from the input field.
        val catName = binding.categoryNameEdittext.text.toString()

        // Get the selected color and icon from their respective adapters.
        val selectedColor = colorAdapter.getSelectedItem()
        val selectedIcon = iconAdapter.getSelectedItem()

        // Validate the category data; if invalid, dismiss the progress dialog and show an alert.
        if (!validateCategoryData(catName, binding.contributionType.selectedIndex, selectedColor, selectedIcon)) {
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        // Determine the transaction type based on the selected index.
        val transactionType = transactionTypes[binding.contributionType.selectedIndex]

        // Create a new Category object with the updated details.
        val updatedCategory = Category(
            id = "",
            name = catName,
            transactiontype = transactionType,
            color = selectedColor!!.name,
            icon = getIconName(selectedIcon!!),
            userid = id
        )

        // Observe the status of the category update operation.
        categoryViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Show a success message and redirect to categories after a delay.
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_category_successful), hideProgressBar = true)

                // Dismiss the dialog after 2 seconds and redirect to the categories screen.
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    redirectToCategories()
                }, 2000)
            } else {
                // Show a failure message and dismiss the progress dialog after a delay.
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_category_fail), hideProgressBar = true)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                }, 2000)
            }
        }

        // Observe messages from the ViewModel for timeout or connection issues.
        categoryViewModel.message.observe(viewLifecycleOwner) { message ->
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
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.connecting), hideProgressBar = false)
                    categoryViewModel.updateCategory(token, categoryId, updatedCategory)
                }
            }
        }

        // Initiate the category update operation in the ViewModel.
        categoryViewModel.updateCategory(token, categoryId, updatedCategory)
    }


    // Validates the input fields for creating a category
    private fun validateCategoryData(
        catName: String,
        transactionType: Int,
        selectedColor: Color?,
        selectedIcon: Int?
    ): Boolean {
        var errors = 0

        // Check if category name is entered
        if (catName.isBlank()) {
            errors += 1
            messages.add("Enter a category name")
            errorMessage += "${getString(R.string.enter_category_name)}\n"
        }

        // Check if a transaction type is selected
        if (transactionType == -1) {
            messages.add("Select a transaction type")
            errorMessage += "${getString(R.string.enter_transaction_type)}\n"
            errors += 1
        }

        // Check if a color is selected
        if (selectedColor == null) {
            messages.add("Select a category color")
            errorMessage += "${getString(R.string.enter_category_color)}\n"
            errors += 1
        }

        // Check if an icon is selected
        if (selectedIcon == null) {
            messages.add("Select a category icon")
            errorMessage += "${getString(R.string.icon_selection)}\n"
            errors += 1
        }

        // Return whether all fields are valid or not
        return errors == 0
    }


    // Updates the category information in the database using the provided token and user ID.
    private fun deleteCategory(token: String) {
        // Show a progress dialog to indicate that the category update is in progress.
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe the status of the category update operation.
        categoryViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Show a success message and redirect to categories after a delay.
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Category deleted successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds and redirect to the categories screen.
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    redirectToCategories()
                }, 2000)
            } else {
                // Show a failure message and dismiss the progress dialog after a delay.
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Category deletion failed!", hideProgressBar = true)

                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                }, 2000)
            }
        }

        // Observe messages from the ViewModel for timeout or connection issues.
        categoryViewModel.message.observe(viewLifecycleOwner) { message ->
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
                    categoryViewModel.deleteCategory(token, categoryId)
                }
            }
        }

        // Initiate the category update operation in the ViewModel.
        categoryViewModel.deleteCategory(token, categoryId)
    }

    // Redirects to the CategoriesFragment, updating the UI to show the categories list.
    private fun redirectToCategories() {
        val categoriesFragment = CategoriesFragment()

        // Navigate to the CategoriesFragment and add this transaction to the back stack.
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, categoriesFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showCustomDeleteDialog(token: String) {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // naved_alam
        // https://www.geeksforgeeks.org/user/naved_alam/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user

        // Inflate the custom dialog view
        val dialogView = layoutInflater.inflate(R.layout.delete_dialog, null)

        // Create the AlertDialog using a custom view
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Find the dialog views
        val confirmButton: LinearLayout  = dialogView.findViewById(R.id.confirmButton)
        val cancelButton: LinearLayout = dialogView.findViewById(R.id.cancelButton)
        val titleTextView: TextView = dialogView.findViewById(R.id.titleTextView)
        val messageTextView: TextView = dialogView.findViewById(R.id.messageTextView)

        // Optionally set a custom title or message if needed
        titleTextView.text = "'${categoryName}'"
        messageTextView.text = "Are you sure you want to delete \nthis category?"

        // Set click listeners for the buttons
        confirmButton.setOnClickListener {
            // Confirm delete action
            deleteCategory(token) // Call method to update the category
        }

        cancelButton.setOnClickListener {
            // Just close the dialog without doing anything
            dialogBuilder.dismiss()
        }

        // Show the dialog
        dialogBuilder.show()
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}