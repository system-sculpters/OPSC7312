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
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentUpdateGoalBinding
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.TimeOutDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class UpdateGoalFragment : Fragment() {
    private var _binding: FragmentUpdateGoalBinding? = null
    private val binding get() = _binding!!

    private lateinit var contributionTypes: List<String>

    private lateinit var goalViewModel: GoalController

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var timeOutDialog: TimeOutDialog

    private var goalId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateGoalBinding.inflate(inflater, container, false)


        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        contributionTypes = AppConstants.CONTRIBUTIONTYPE.entries.map { it.name }

        goalViewModel = ViewModelProvider(this).get(GoalController::class.java)

        timeOutDialog = TimeOutDialog()

        setUpInputs()

        loadTransactionDetails()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Goal Details")
    }

    private fun loadTransactionDetails(){
        val goal = arguments?.getParcelable<Goal>("goal")

        // Update UI with category data
        if (goal != null){
            Log.d("goal", "this is the goal: $goal")
            val selectedIndex = contributionTypes.indexOf(goal.contrubitiontype)
            goalId = goal.id
            binding.goalName.setText(goal.name)
            binding.targetAmount.setText(AppConstants.formatAmount(goal.targetamount))
            binding.currentAmount.setText(AppConstants.formatAmount(goal.currentamount))
            binding.selectedDateText.setText(AppConstants.longToDate(goal.deadline))
            binding.contributionType.selectItemByIndex(selectedIndex)
            binding.contributionAmount.setText(AppConstants.formatAmount(goal.currentamount))

        }
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
            updateGoal(token, user.id)
        } else {

        }
    }

    private fun updateGoal(token: String, id: String) {
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
            return
        }

        val updatedGoal = Goal(
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

                    changeCurrentFragment(GoalsFragment())
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
                    goalViewModel.updateGoal(token, goalId, updatedGoal)                }
            }
        }

        goalViewModel.updateGoal(token, goalId, updatedGoal)
    }

    private fun validateData(name: String, targetAmount: String, selectedIndex: Int, contributionAmount: String): Boolean {
        var errors = 0

        if (name.isBlank()) {
            AppConstants.showFloatingToast(requireContext(), "Enter a goal name")
            errors += 1
        }

        if (targetAmount.isBlank()) {
            AppConstants.showFloatingToast(requireContext(), "Enter a target amount")
            errors += 1
        }

        if (selectedIndex == -1) {
            //binding.contributionType.error = "Enter a transaction type"
            AppConstants.showFloatingToast(requireContext(), "Select a contribution type")
            errors += 1
        }

        if (contributionAmount.isBlank()) {
            AppConstants.showFloatingToast(requireContext(), "Select contribution amount")
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