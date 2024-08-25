package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentGoalsBinding
import com.opsc.opsc7312.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312.model.data.Goal
import com.opsc.opsc7312.model.data.Transaction
import com.opsc.opsc7312.view.adapter.GoalAdapter


class GoalsFragment : Fragment() {
    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalAdapter: GoalAdapter

    private lateinit var goals: ArrayList<Goal>

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

        setUpRecyclerView()

        goalList()

        return binding.root
    }

    private fun setUpRecyclerView(){
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.adapter = goalAdapter
    }

    private fun redirectToCreate(){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val createGoal = PlaceholderFragment()

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

    private fun goalList() {
        val g1 = Goal(totalamount = 5000.00, currentamount = 3290.21, name = "Goal 1")
        val g2 = Goal(totalamount = 8000.00, currentamount = 999.51, name = "Goal 2")
        val g3 = Goal(totalamount = 3400.00, currentamount = 2168.43, name = "Goal 3")
        val g4 = Goal(totalamount = 770.00, currentamount = 329.95, name = "Goal 4")
        val g5 = Goal(totalamount = 6320.00, currentamount = 3122.55, name = "Goal 5")

        goals.add(g1)
        goals.add(g2)
        goals.add(g3)
        goals.add(g4)
        goals.add(g5)

        goalAdapter.updateGoals(goals)

    }
}