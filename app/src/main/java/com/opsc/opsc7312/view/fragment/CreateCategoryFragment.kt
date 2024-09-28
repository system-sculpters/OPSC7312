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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCreateCategoryBinding
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Color
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.ColorAdapter
import com.opsc.opsc7312.view.adapter.IconAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog


class CreateCategoryFragment : Fragment() {

    // Binding object to access the views in the layout
    private var _binding: FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!

    // ViewModel to manage UI-related data for this fragment
    private lateinit var categoryViewModel: CategoryController

    // UserManager and TokenManager to handle user and token information
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    // List to store color data for category creation
    private lateinit var dataList: ArrayList<Color>

    // List to store available icons for category creation
    private lateinit var iconList: ArrayList<Int>

    // List to store available contribution types for category
    private lateinit var contributionTypes: List<String>

    // Mutable list to store error or status messages
    private lateinit var messages: MutableList<String>

    // Adapters for managing color and icon selection in RecyclerViews
    private lateinit var colorAdapter: ColorAdapter
    private lateinit var iconAdapter: IconAdapter

    // RecyclerView to display icons in a grid
    private lateinit var iconRecyclerView: RecyclerView

    // Dialog to display icon picker UI
    private lateinit var iconPickerDialog: AlertDialog

    // Request code for handling intents
    private val REQUEST_CODE = 1234

    // Variable to store the name of the selected icon
    private var selectedIconName: String = "Select an icon"

    // Variable to store the error message if input validation fails
    private var errorMessage = ""

    // Dialog to handle timeout or long running processes
    private lateinit var timeOutDialog: TimeOutDialog

    // Inflates the layout and initializes the data required for the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)

        // Initialize color data and icon data
        dataList = arrayListOf<Color>()
        iconList = getIconData()

        // Initialize empty messages list
        messages = arrayListOf()

        // Set up user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Set up ViewModel for managing category data
        categoryViewModel = ViewModelProvider(this).get(CategoryController::class.java)

        // Get contribution types from app constants
        contributionTypes = AppConstants.TRANSACTIONTYPE.entries.map { it.name }

        // Initialize timeout dialog
        timeOutDialog = TimeOutDialog()

        // Set up icon adapter and handle icon selection
        iconAdapter = IconAdapter(iconList) { selectedIcon ->
            selectedIconName = getIconName(selectedIcon)
            binding.iconName.text = selectedIconName

            // Get the theme color for the icon text
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
            val color = typedValue.data
            binding.iconName.setTextColor(color)

            // Set selected icon image in the ImageView
            binding.iconImageView.setImageResource(selectedIcon)

            // Dismiss the icon picker dialog after selection
            iconPickerDialog.dismiss()
        }

        // Set up inputs and listeners
        setUpInputs()

        // Load category data for colors and icons
        getCategoryData()

        // Set up color picker UI
        setUpColors()

        return binding.root
    }

    // Once the view is created, set the toolbar title to "Create"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set toolbar title to "Create"
        (activity as? MainActivity)?.setToolbarTitle("Create")
    }

    // Sets up the color picker RecyclerView
    private fun setUpColors() {
        binding.colorList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.colorList.setHasFixedSize(true)

        // Adapter to display available colors and handle color selection
        colorAdapter = ColorAdapter(dataList) { selectedCategory ->
            Log.d("SelectedCategory", "Selected category: $selectedCategory")
            // Handle the selected category (color) here
        }

        binding.colorList.adapter = colorAdapter
    }

    // Displays the icon picker dialog when icon is clicked
    private fun showIconPickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.icon_picker_dialog, null)

        // Set up RecyclerView for icons inside the dialog
        iconRecyclerView = dialogView.findViewById(R.id.icon_recycler_view)
        iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // 3 icons per row
        iconRecyclerView.adapter = iconAdapter

        // Create and display the dialog
        iconPickerDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        iconPickerDialog.show()
    }

    // Retrieves available category colors
    private fun getCategoryData(): ArrayList<Color> {
        for (i in AppConstants.COLOR_LIST.indices) {
            val dataClass = Color(i.toString(), AppConstants.COLOR_LIST[i])
            dataList.add(dataClass)
        }
        return dataList
    }

    // Retrieves the name of the icon by matching the drawable ID
    private fun getIconName(iconDrawableId: Int): String {
        val iconsMap = AppConstants.ICONS
        return iconsMap.entries.find { it.value == iconDrawableId }?.key ?: "Unknown Icon"
    }

    // Retrieves available icons for category creation
    private fun getIconData(): ArrayList<Int> {
        val iconsMap = AppConstants.ICONS
        return ArrayList(iconsMap.values)
    }

    // Sets up input fields, icon picker, and submit button
    private fun setUpInputs() {
        // Set contribution types in the dropdown
        binding.contributionType.setItems(contributionTypes)

        // Show icon picker dialog when icon container is clicked
        binding.iconContainer.setOnClickListener {
            showIconPickerDialog()
        }

        // Handle submit button click
        binding.submitButton.setOnClickListener {
            setUpUserData()
        }
    }

    // Gathers user and token information before creating the category
    private fun setUpUserData() {
        val user = userManager.getUser()
        val token = tokenManager.getToken()

        if (token != null) {
            addCategory(token, user.id)
        } else {
            // Handle case when token is null (optional)
        }
    }

    // Sends the data to create a new category using the ViewModel
    private fun addCategory(token: String, id: String) {
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        val catName = binding.categoryNameEdittext.text.toString()
        val selectedColor = colorAdapter.getSelectedItem()
        val selectedIcon = iconAdapter.getSelectedItem()

        // Validate user input before sending data to the server
        if (!validateCategoryData(catName, binding.contributionType.selectedIndex, selectedColor, selectedIcon)) {
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        // Prepare the new category data
        val transactionType = contributionTypes[binding.contributionType.selectedIndex]
        val newCategory = Category(
            id = "",
            name = catName,
            transactiontype = transactionType,
            color = selectedColor!!.name,
            icon = getIconName(selectedIcon!!),
            userid = id
        )

        // Observe ViewModel status for success or failure
        categoryViewModel.status.observe(viewLifecycleOwner) { status ->
            if (status) {
                // Update and dismiss the progress dialog on success
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Category created successfully!", hideProgressBar = true)
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                    redirectToCategories()
                }, 2000)
            } else {
                // Handle failure and dismiss dialog after a delay
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Category creation failed!", hideProgressBar = true)
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss()
                }, 2000)
            }
        }

        // Observe ViewModel for timeout messages
        categoryViewModel.message.observe(viewLifecycleOwner) { message ->
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Creating Category...", hideProgressBar = false)
                    categoryViewModel.createCategory(token, newCategory)
                }
            }
        }

        // Trigger the category creation process
        categoryViewModel.createCategory(token, newCategory)
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
            errorMessage += "• Enter a category name\n"
        }

        // Check if a transaction type is selected
        if (transactionType == -1) {
            messages.add("Select a transaction type")
            errorMessage += "• Select a transaction type\n"
            errors += 1
        }

        // Check if a color is selected
        if (selectedColor == null) {
            messages.add("Select a category color")
            errorMessage += "• Select a category color\n"
            errors += 1
        }

        // Check if an icon is selected
        if (selectedIcon == null) {
            messages.add("Select a category icon")
            errorMessage += "• Select a category icon\n"
            errors += 1
        }

        // Return whether all fields are valid or not
        return errors == 0
    }

    // Redirects the user to the category list fragment after successful creation
    private fun redirectToCategories(){
        val categoriesFragment = CategoriesFragment()


        // Navigate to CategoryDetailsFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, categoriesFragment)
            .addToBackStack(null)
            .commit()
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
