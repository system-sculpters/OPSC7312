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


class UpdateCategoryFragment : Fragment() {
    private var _binding: FragmentUpdateCategoryBinding? = null
    private val binding get() = _binding!!



    private lateinit var categoryViewModel: CategoryController

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager


    private lateinit var dataList: ArrayList<Color>

    private lateinit var iconList: ArrayList<Int>

    private lateinit var transactionTypes: List<String>

    private lateinit var messages: MutableList<String>



    private lateinit var colorAdapter: ColorAdapter

    private lateinit var iconAdapter: IconAdapter



    private lateinit var iconRecyclerView: RecyclerView

    private lateinit var iconPickerDialog: AlertDialog

    private val REQUEST_CODE = 1234

    private var selectedIconName:String = "Select an icon"

    private var categoryId: String = ""

    private lateinit var timeOutDialog: TimeOutDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateCategoryBinding.inflate(inflater, container, false)

        dataList = arrayListOf<Color>()
        iconList = getIconData()

        messages = arrayListOf()

        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        categoryViewModel = ViewModelProvider(this).get(CategoryController::class.java)

        timeOutDialog = TimeOutDialog()

        transactionTypes = AppConstants.TRANSACTIONTYPE.entries.map { it.name }

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

        loadCategoryDetails()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Category Details")

        // Check if permission is granted
        if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().packageName))
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun loadCategoryDetails(){
        val category = arguments?.getParcelable<Category>("category")

        // Update UI with category data
        if (category != null){
            Log.d("", "this is the cat: $category")

            categoryId = category.id

            val colorId = AppConstants.COLOR_LIST.indexOf(category.color).toString()

            val selectedIndex = transactionTypes.indexOf(category.transactiontype)

            val selectedColor = Color(Id=colorId, name = category.color)
            val selectedIcon = getIconNames().indexOf(category.icon)
            val iconValue = AppConstants.ICONS.entries.find { it.key == category.icon }?.value

            binding.categoryNameEdittext.setText(category.name)
            binding.contributionType.selectItemByIndex(selectedIndex)

            colorAdapter.setSelectedColor(selectedColor)


            iconAdapter.setSelectedIcon(iconValue)


            binding.iconName.text = category.icon

            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
            val color = typedValue.data
            binding.iconName.setTextColor(color)
            //binding.iconName.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
            binding.iconImageView.setImageResource(iconList[selectedIcon])

        }
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

    private fun getIconNames(): ArrayList<String> {
        val iconsMap = AppConstants.ICONS
        return ArrayList(iconsMap.keys)
    }


    private fun setUpInputs(){
        binding.contributionType.setItems(transactionTypes)

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
            updateCategory(token, user.id)
        } else {

        }
    }

    private fun updateCategory(token: String, id: String) {
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        Log.d("selectedIndex", "the selected index is ${binding.contributionType.selectedIndex}")
        val catName = binding.categoryNameEdittext.text.toString()

        val selectedColor = colorAdapter.getSelectedItem()
        val selectedIcon = iconAdapter.getSelectedItem()

        if (!validateCategoryData(catName, binding.contributionType.selectedIndex, selectedColor, selectedIcon)) {
            return
        }

        val transactionType = transactionTypes[binding.contributionType.selectedIndex]

        val updatedCategory = Category(
            id = "",
            name = catName,
            transactiontype = transactionType,
            color = selectedColor!!.name,
            icon = getIconName(selectedIcon!!),
            userid = id
        )

        categoryViewModel.status.observe(viewLifecycleOwner) { status ->
            if (status) {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Category update successful!", hideProgressBar = true, )

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                    // Navigate to MainActivity
                    redirectToCategories()
                }, 2000)

                //Toast.makeText(requireContext(), "Category creation successful", Toast.LENGTH_LONG).show()
            } else {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Category update failed!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()


                }, 2000)
            }
        }

        categoryViewModel.message.observe(viewLifecycleOwner){ message ->
            if(message == "timeout"){
                timeOutDialog.showTimeoutDialog(requireContext() ){
                    //progressDialog.show()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Connecting...", hideProgressBar = false)
                    categoryViewModel.updateCategory(token, categoryId, updatedCategory)
                }
            }
        }

        categoryViewModel.updateCategory(token, categoryId, updatedCategory)
    }

    private fun validateCategoryData(
        catName: String,
        transactionType: Int,
        selectedColor: Color?,
        selectedIcon: Int?
    ): Boolean {
        var errors = 0

        if (catName.isBlank()) {
            AppConstants.showFloatingToast(requireContext(), "Enter a category name")
            errors += 1
            messages.add("Enter a category name")
        }

        if (transactionType == -1) {
            //binding.contributionType.error = "Enter a transaction type"
            AppConstants.showFloatingToast(requireContext(), "Select a transaction type")
            messages.add("Select a transaction type")
            errors += 1
        }

        if (selectedColor == null) {
            //binding.colorLabel.text = "Select a color"
            //binding.colorLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            AppConstants.showFloatingToast(requireContext(), "Select a color")
            messages.add("Select an color")
            errors += 1
        } else {
            binding.colorLabel.text = "Color"
//            val typedValue = TypedValue()
//            requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
//            val color = typedValue.data
//            binding.colorLabel.setTextColor(color)
        }

        if (selectedIcon == null) {
            messages.add("Select an icon")
            AppConstants.showFloatingToast(requireContext(), "Select an icon")
//            binding.iconName.text = "Select an icon"
//            binding.iconName.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
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