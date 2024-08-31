package com.opsc.opsc7312.view.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCreateGoalBinding
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.api.retrofitclients.GoalRetrofitClient
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGoalBinding.inflate(inflater, container, false)


        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        contributionTypes = AppConstants.CONTRIBUTIONTYPE.entries.map { it.name }

        goalViewModel = ViewModelProvider(this).get(GoalController::class.java)

        setUpInputs()

        return binding.root
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

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
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
        val name = binding.goalName.text.toString()
        val targetAmount = binding.targetAmount.text.toString()
        val currentAmount = binding.currentAmount.text.toString()
        val deadlineText = binding.selectedDateText.text.toString()
        val contrubitiontype = binding.contributionType
        val contributionamount = binding.contributionAmount.text.toString()

        var deadline = 0L
        if(deadlineText.isNotBlank()){
            deadline = convertStringToLong(deadlineText)
        }

        val newGoal = Goal(
            userid = id,
            name = name,
            targetamount = targetAmount.toDouble(),
            currentamount = currentAmount.toDouble(),
            deadline = deadline,
            contributionamount = contributionamount.toDouble(),
            contrubitiontype = contributionTypes[contrubitiontype.selectedIndex]
        )

        goalViewModel.status.observe(viewLifecycleOwner){
            status ->
            if(status){
                redirectToGoals()
                Toast.makeText(requireContext(), "Goal creation successful", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Goal creation failed", Toast.LENGTH_LONG).show()
            }
        }

        goalViewModel.createGoal(token, newGoal)
    }

    private fun redirectToGoals(){
        // Create a new instance of CategoryDetailsFragment and pass category data
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