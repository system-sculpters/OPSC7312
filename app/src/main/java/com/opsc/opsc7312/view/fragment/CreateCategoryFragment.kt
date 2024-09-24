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
    private var _binding: FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!


    private lateinit var categoryViewModel: CategoryController

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager


    private lateinit var dataList: ArrayList<Color>

    private lateinit var iconList: ArrayList<Int>

    private lateinit var contributionTypes: List<String>

    private lateinit var messages: MutableList<String>



    private lateinit var colorAdapter: ColorAdapter

    private lateinit var iconAdapter: IconAdapter



    private lateinit var iconRecyclerView: RecyclerView

    private lateinit var iconPickerDialog: AlertDialog

    private val REQUEST_CODE = 1234

    private var selectedIconName:String = "Select an icon"

    private var errorMessage = ""

    private lateinit var timeOutDialog: TimeOutDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)

        dataList = arrayListOf<Color>()
        iconList = getIconData()

        messages = arrayListOf()

        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        categoryViewModel = ViewModelProvider(this).get(CategoryController::class.java)

        contributionTypes = AppConstants.TRANSACTIONTYPE.entries.map { it.name }

        timeOutDialog = TimeOutDialog()


        iconAdapter = IconAdapter(iconList) { selectedIcon ->
            // Set the selected icon to the ImageView
            selectedIconName = getIconName(selectedIcon)
            binding.iconName.text = selectedIconName

            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
            val color = typedValue.data
            binding.iconName.setTextColor(color)
            //binding.iconName.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
            binding.iconImageView.setImageResource(selectedIcon)
            iconPickerDialog.dismiss() // Dismiss the dialog after selecting an icon
        }
        setUpInputs()

        getCategoryData()

        setUpColors()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Create")

    }

    private fun setUpColors(){
        binding.colorList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.colorList.setHasFixedSize(true)

        colorAdapter = ColorAdapter(dataList) { selectedCategory ->
            Log.d("SelectedCategory", "Selected category: $selectedCategory")
            // Handle the selected category here
        }

        binding.colorList.adapter = colorAdapter
    }

    private fun showIconPickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.icon_picker_dialog, null)

        iconRecyclerView= dialogView.findViewById(R.id.icon_recycler_view)


        iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // Adjust the span count as needed

        iconRecyclerView.adapter = iconAdapter

        iconPickerDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        iconPickerDialog.show()
    }

    private fun getCategoryData(): ArrayList<Color>{
        for(i in AppConstants.COLOR_LIST.indices){
            val dataClass = Color(i.toString(), AppConstants.COLOR_LIST[i])
            dataList.add(dataClass)
        }
        return dataList
    }

    private fun getIconName(iconDrawableId: Int): String {
        // Implement your logic here to map the icon drawable ID to its corresponding name
        // For simplicity, I'll use a hardcoded map for demonstration purposes
        val iconsMap = AppConstants.ICONS
        return iconsMap.entries.find { it.value == iconDrawableId }?.key ?: "Unknown Icon"
    }

    private fun getIconData(): ArrayList<Int> {
        val iconsMap = AppConstants.ICONS
        return ArrayList(iconsMap.values)
    }

    private fun setUpInputs(){
        //binding.selectedDateText.text = getCurrentDate()

        binding.contributionType.setItems(contributionTypes)

        binding.iconContainer.setOnClickListener {
            showIconPickerDialog()
        }

        binding.submitButton.setOnClickListener {
            setUpUserData()
        }
    }

    private fun setUpUserData(){
        val user = userManager.getUser()

        val token = tokenManager.getToken()


        if(token != null){
            addCategory(token, user.id)
        } else {

        }
    }

    private fun addCategory(token: String, id: String) {
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        val catName = binding.categoryNameEdittext.text.toString()

        val selectedColor = colorAdapter.getSelectedItem()
        val selectedIcon = iconAdapter.getSelectedItem()

        if (!validateCategoryData(catName, binding.contributionType.selectedIndex, selectedColor, selectedIcon)) {
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        val transactionType = contributionTypes[binding.contributionType.selectedIndex]

        val newCategory = Category(
            id = "",
            name = catName,
            transactiontype = transactionType,
            color = selectedColor!!.name,
            icon = getIconName(selectedIcon!!),
            userid = id
        )

        categoryViewModel.status.observe(viewLifecycleOwner)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "category created successfully!", hideProgressBar = true, )

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()
                    redirectToCategories()
                }, 2000)

            } else {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "category creation failed!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                }, 2000)
            }
        }

        categoryViewModel.message.observe(viewLifecycleOwner){ message ->
            if(message == "timeout" || message.contains("Unable to resolve host")){
                timeOutDialog.showTimeoutDialog(requireContext()){
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Creating Category...", hideProgressBar = false)
                    categoryViewModel.createCategory(token, newCategory)
                }
            }
        }

        categoryViewModel.createCategory(token, newCategory)
    }

    private fun validateCategoryData(
        catName: String,
        transactionType: Int,
        selectedColor: Color?,
        selectedIcon: Int?
    ): Boolean {
        var errors = 0

        if (catName.isBlank()) {
            errors += 1
            messages.add("Enter a category name")
            errorMessage += "• Enter a category name\n"
        }

        if (transactionType == -1) {
            messages.add("Select a transaction type")
            errorMessage += "• Select a transaction type\n"
            errors += 1
        }

        if (selectedColor == null) {
            errorMessage +="• Select an color\n"
            errors += 1
        }

        if (selectedIcon == null) {
            errorMessage += "• Select an icon"
            errors += 1
        }

        val test = "Cat Name: $catName, transaction type: $transactionType, Color: ${selectedColor?.name}, icon: ${selectedIcon?.let { getIconName(it) }}"
        Log.d("Category", test)

        return errors == 0
    }


    private fun redirectToCategories(){
        val categoriesFragment = CategoriesFragment()


        // Navigate to CategoryDetailsFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, categoriesFragment)
            .addToBackStack(null)
            .commit()
    }
}