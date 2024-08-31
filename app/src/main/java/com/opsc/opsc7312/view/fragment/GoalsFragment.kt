package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentGoalsBinding
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.GoalAdapter
import com.opsc.opsc7312.view.observers.GoalObserver


class GoalsFragment : Fragment() {
    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalAdapter: GoalAdapter

    private lateinit var goals: ArrayList<Goal>

    private lateinit var goalViewModel: GoalController

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

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

        setUpRecyclerView()

        setUpGoals()

        return binding.root
    }

    private fun setUpRecyclerView(){
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.adapter = goalAdapter
    }

    private fun redirectToCreate(){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val createGoal = CreateGoalFragment()

        changeCurrentFragment(createGoal)
    }
    private fun redirectToDetails(goal: Goal){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val categoryDetailsFragment = PlaceholderFragment()
        val bundle = Bundle()
        bundle.putParcelable("goal", goal)
        bundle.putString("screen", "redirectToDetails goal")
        categoryDetailsFragment.arguments = bundle

        // Navigate to CategoryDetailsFragment
        changeCurrentFragment(categoryDetailsFragment)
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
        // Observe LiveData
        goalViewModel.status.observe(viewLifecycleOwner)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                // Success
            } else {
                // Failure
            }
        }

        goalViewModel.message.observe(viewLifecycleOwner) { message ->
            // Show message to the user, if needed
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        goalViewModel.goalList.observe(viewLifecycleOwner, GoalObserver(goalAdapter, binding.amount))

        // Example API calls
        goalViewModel.getAllGoals(token, id)
    }
}