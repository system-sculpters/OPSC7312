package com.opsc.opsc7312.view.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCategoriesBinding
import com.opsc.opsc7312.databinding.FragmentCreateGoalBinding
import com.opsc.opsc7312.model.api.retrofitclients.GoalRetrofitClient
import com.opsc.opsc7312.model.data.Category
import com.opsc.opsc7312.model.data.Goal
import com.opsc.opsc7312.model.data.Transaction
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGoalBinding.inflate(inflater, container, false)

        contributionTypes = AppConstants.CONTRIBUTIONTYPE.values().map { it.name }

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
            addGoal()
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

    private fun addGoal(){
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
            userid = "pmp6jWuGYfPGS4FdlgQtWzKHHug1",
            name = name,
            targetamount = targetAmount.toDouble(),
            currentamount = currentAmount.toDouble(),
            deadline = deadline,
            contributionamount = contributionamount.toDouble(),
            contrubitiontype = contributionTypes[contrubitiontype.selectedIndex]
        )

        GoalRetrofitClient.apiService.createGoal(newGoal).enqueue(object : Callback<Goal> {
            override fun onResponse(call: Call<Goal>, response: Response<Goal>) {
                if (response.isSuccessful) {
                    val createdCategory = response.body()
                    createdCategory?.let {
                        Log.d("MainActivity", "Category created: $it")
                    }
                } else {
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Goal>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
            }
        })

        redirectToGoals()
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