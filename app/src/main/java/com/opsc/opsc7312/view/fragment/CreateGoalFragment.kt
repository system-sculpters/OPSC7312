package com.opsc.opsc7312.view.fragment

import android.app.DatePickerDialog
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
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCreateGoalBinding
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.api.retrofitclients.GoalRetrofitClient
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.TimeOutDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CreateGoalFragment : Fragment() {
    private var _binding: FragmentCreateGoalBinding? = null
    private val binding get() = _binding!!

    private lateinit var contributionTypes: List<String>

    private lateinit var goalViewModel: GoalController

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var timeOutDialog: TimeOutDialog

    private var errorMessage = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGoalBinding.inflate(inflater, container, false)


        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        contributionTypes = AppConstants.CONTRIBUTIONTYPE.entries.map { it.name }

        goalViewModel = ViewModelProvider(this).get(GoalController::class.java)

        timeOutDialog = TimeOutDialog()

        setUpInputs()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Create")
    }

    private fun setUpInputs(){
        //binding.selectedDateText.text = getCurrentDate()

        binding.contributionType.setItems(contributionTypes)

        binding.deadline.setOnClickListener {
            showDatePickerDialog()
        }

        binding.submitButton.setOnClickListener {
            setUpCategoriesDetails()
        }
    }

    private fun showDatePickerDialog() {


        try {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

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
            e.printStackTrace()
        }
    }

    private fun setUpCategoriesDetails(){
        val user = userManager.getUser()

        val token = tokenManager.getToken()


        if(token != null){
            addGoal(token, user.id)
        } else {

        }
    }

    private fun addGoal(token: String, id: String) {
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        val name = binding.goalName.text.toString()
        val targetAmount = binding.targetAmount.text.toString()
        var currentAmount = binding.currentAmount.text.toString()
        val deadlineText = binding.selectedDateText.text.toString()
        val contributionType = binding.contributionType
        val contributionAmount = binding.contributionAmount.text.toString()

        var deadline = 0L
        if(deadlineText.isNotBlank()){
            deadline = convertStringToLong(deadlineText)
        }

        if(currentAmount.isBlank()){
            currentAmount = "0"
        }

        if(!validateData(name, targetAmount, contributionType.selectedIndex, contributionAmount)){
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        val newGoal = Goal(
            userid = id,
            name = name,
            targetamount = targetAmount.toDouble(),
            currentamount = currentAmount.toDouble(),
            deadline = deadline,
            contributionamount = contributionAmount.toDouble(),
            contrubitiontype = contributionTypes[contributionType.selectedIndex]
        )

        goalViewModel.status.observe(viewLifecycleOwner){
            status ->

            if (status) {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Goal creation successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()
                    redirectToGoals()
                }, 2000)

            } else {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Goal creation failed!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                }, 2000)
            }
        }

        goalViewModel.message.observe(viewLifecycleOwner){ message ->
            if(message == "timeout"){
                timeOutDialog.showTimeoutDialog(requireContext()){
                    //progressDialog.show()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Creating Goal...", hideProgressBar = false)
                    goalViewModel.createGoal(token, newGoal)                }
            }
        }

        goalViewModel.createGoal(token, newGoal)
    }

    private fun validateData(name: String, targetAmount: String, selectedIndex: Int, contributionAmount: String): Boolean {
        var errors = 0

        if (name.isBlank()) {
            errors += 1
            errorMessage += "• Enter a goal name\n"
        }

        if (targetAmount.isBlank()) {
            errorMessage += "• Enter a target amount\n"
            errors += 1
        }

        if (selectedIndex == -1) {
            //binding.contributionType.error = "Enter a transaction type"
            errorMessage += "• Select a contribution type\n"
            errors += 1
        }

        if (contributionAmount.isBlank()) {
            errorMessage += "• Enter a contribution amount"
            errors += 1
        }

        return errors == 0
    }


    private fun redirectToGoals(){
        // Create a new instance of GoalsFragment
        val goalsFragment = GoalsFragment()

        // Navigate to CategoryDetailsFragment
        changeCurrentFragment(goalsFragment)
    }

    private fun changeCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun convertStringToLong(dateString: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

         val date: Date = dateFormat.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")

        return date.time
    }

}