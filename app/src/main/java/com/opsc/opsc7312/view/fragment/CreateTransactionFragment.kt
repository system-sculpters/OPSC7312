package com.opsc.opsc7312.view.fragment

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCreateTransactionBinding
import com.opsc.opsc7312.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.IconAdapter
import com.opsc.opsc7312.view.adapter.SelectCategoryAdapter
import com.opsc.opsc7312.view.observers.CategoriesObserver


class CreateTransactionFragment : Fragment() {
    private var _binding: FragmentCreateTransactionBinding? = null
    private val binding get() = _binding!!


    private lateinit var transactionTypes: List<String>


    private lateinit var transactionViewModel: TransactionController

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager


    private lateinit var iconRecyclerView: RecyclerView

    private lateinit var iconPickerDialog: AlertDialog

    private lateinit var adapter: SelectCategoryAdapter

    private lateinit var categoryViewModel: CategoryController



    private var isRecurring = true

    private var selectedIconName: String = "Select an category"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTransactionBinding.inflate(inflater, container, false)


        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        transactionTypes = AppConstants.TRANSACTIONTYPE.entries.map { it.name }

        transactionViewModel = ViewModelProvider(this).get(TransactionController::class.java)

        categoryViewModel = ViewModelProvider(this).get(CategoryController::class.java)

        adapter = SelectCategoryAdapter { selectedCategory ->
            // Set the selected icon to the ImageView
            selectedIconName = selectedCategory.name
            binding.categoryNameText.text = selectedIconName

            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
            val color = typedValue.data
            binding.categoryNameText.setTextColor(color)
            //binding.iconName.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
            AppConstants.ICONS[selectedCategory.icon]?.let {
                binding.categoryImageView.setImageResource(
                    it
                )
            }

            val colorResId = AppConstants.COLOR_DICTIONARY[selectedCategory.color]
            if (colorResId != null) {
                val originalColor = ContextCompat.getColor(requireContext(),
                    colorResId
                )
                //holder.background.setBackgroundColor(originalColor)
                binding.categoryImageContainer.setBackgroundColor(originalColor)
                val cornerRadius = requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_main)
                val shapeDrawable = GradientDrawable()
                shapeDrawable.setColor(originalColor)
                shapeDrawable.cornerRadius = cornerRadius.toFloat()
                binding.categoryImageContainer.background = shapeDrawable
            }


            iconPickerDialog.dismiss() // Dismiss the dialog after selecting an icon
        }
        toggleButton()

        setUpInputs()

        return binding.root
    }

    private fun setUpInputs(){
        //binding.selectedDateText.text = getCurrentDate()

        val user = userManager.getUser()

        val token = tokenManager.getToken()

        if (token != null) {
            observeViewModel(token, user.id)
        }

        binding.transactionType.setItems(transactionTypes)

        binding.iconContainer.setOnClickListener {
            showIconPickerDialog()
        }

        binding.submitButton.setOnClickListener {
            if (token != null) {
                addTransaction(token, user.id)
            }
        }
    }

    private fun showIconPickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.icon_picker_dialog, null)

        iconRecyclerView= dialogView.findViewById(R.id.icon_recycler_view)


        iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // Adjust the span count as needed

        iconRecyclerView.adapter = adapter

        iconPickerDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        iconPickerDialog.show()
    }




    private fun toggleButton(){
        binding.isRecurring.addOnButtonCheckedListener{
                group, checkedId, isChecked ->
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


    private fun addTransaction(token: String, id: String){
        val transactionName = binding.transactionNameEdittext.text.toString()
        val amount = binding.amount.text.toString()
        val selectedCategory = adapter.getSelectedItem()
        val transactionType = transactionTypes[binding.transactionType.selectedIndex]




        val newTransaction = Transaction(
            name = transactionName,
            amount = amount.toDouble(),
            userid = id,
            isrecurring = isRecurring,
            type = transactionType,
            categoryId = selectedCategory!!.id
        )

        transactionViewModel.status.observe(viewLifecycleOwner) { status ->
            binding.progressBar.visibility = View.GONE
            if (status) {
                redirectToTransactions()
                Toast.makeText(requireContext(), "Transaction creation successful", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Transaction creation failed", Toast.LENGTH_LONG).show()
            }
        }

        transactionViewModel.createTransaction(token, newTransaction)
    }

    private fun observeViewModel(token: String, id: String) {
        // Observe LiveData
        categoryViewModel.status.observe(viewLifecycleOwner)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                // Success
            } else {
                // Failure
            }
        }

        categoryViewModel.message.observe(viewLifecycleOwner) { message ->
            // Show message to the user, if needed
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        categoryViewModel.categoryList.observe(viewLifecycleOwner, CategoriesObserver(null, adapter))


        // Example API calls
        categoryViewModel.getAllCategories(token, id)
    }

    private fun redirectToTransactions(){
        val transactionsFragment = TransactionsFragment()


        // Navigate to CategoryDetailsFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, transactionsFragment)
            .addToBackStack(null)
            .commit()
    }
}