package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentGoalsBinding
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.GoalAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.GoalObserver


class GoalsFragment : Fragment() {
    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalAdapter: GoalAdapter

    private lateinit var goals: ArrayList<Goal>

    private lateinit var goalViewModel: GoalController

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var timeOutDialog: TimeOutDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)

        goals = ArrayList()
        goalAdapter = GoalAdapter{
            goal -> redirectToDetails(goal)
        }
        binding.addGoal.setOnClickListener {
            redirectToCreate()
        }

        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        goalViewModel = ViewModelProvider(this).get(GoalController::class.java)

        timeOutDialog = TimeOutDialog()

        setUpRecyclerView()

        setUpGoals()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Goals")
    }

    private fun setUpRecyclerView(){

        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.adapter = goalAdapter

        setItemTouchHelper()
    }


    private fun redirectToCreate(){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val createGoal = CreateGoalFragment()

        changeCurrentFragment(createGoal)
    }
    private fun redirectToDetails(goal: Goal){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val goalDetailsFragment = UpdateGoalFragment()
        val bundle = Bundle()
        bundle.putParcelable("goal", goal)
        bundle.putString("screen", "redirectToDetails goal")
        goalDetailsFragment.arguments = bundle

        // Navigate to CategoryDetailsFragment
        changeCurrentFragment(goalDetailsFragment)
    }

    private fun changeCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }



    private fun setUpGoals(){
        val user = userManager.getUser()

        val token = tokenManager.getToken()


        if(token != null){
            observeViewModel(token, user.id)
        } else {

        }
    }

    private fun observeViewModel(token: String, id: String) {
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())
        // Observe LiveData
        goalViewModel.status.observe(viewLifecycleOwner)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                progressDialog.dismiss()
            } else {
                progressDialog.dismiss()
            }
        }

        goalViewModel.message.observe(viewLifecycleOwner){ message ->
            if(message == "timeout"){
                timeOutDialog.showTimeoutDialog(requireContext() ){
                    //progressDialog.show()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Connecting...", hideProgressBar = false)
                    goalViewModel.getAllGoals(token, id)                }
            }
        }


        goalViewModel.goalList.observe(viewLifecycleOwner, GoalObserver(goalAdapter, binding.amount))

        // Example API calls
        goalViewModel.getAllGoals(token, id)
    }

    private fun setItemTouchHelper(){

    }
}