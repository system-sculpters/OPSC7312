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
import com.opsc.opsc7312.databinding.FragmentHomeBinding
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import com.opsc.opsc7312.view.observers.TransactionsObserver


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: TransactionAdapter

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var transactionViewModel: TransactionController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        transactionViewModel = ViewModelProvider(this).get(TransactionController::class.java)


        transactionAdapter = TransactionAdapter{
                transaction ->
            redirectToDetails(transaction)
        }

        setUpUserDetails()

        setUpRecyclerView()

        return binding.root
    }

    private fun setUpUserDetails(){
        val user = userManager.getUser()

        val token = tokenManager.getToken()

        binding.username.text = user.username
        binding.email.text = user.email

        if(token != null){
            observeViewModel(token, user.id)
        } else {

        }
    }



    private fun observeViewModel(token: String, userId: String){
        // Observe LiveData
        transactionViewModel.status.observe(viewLifecycleOwner)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                // Success
            } else {
                // Failure
            }
        }

        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            // Show message to the user, if needed
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        transactionViewModel.transactionList.observe(viewLifecycleOwner, TransactionsObserver(transactionAdapter, binding.amount, binding.incomeAmount, binding.expenseAmount))


        // Example API calls
        transactionViewModel.getAllTransactions(token, userId)
    }


    private fun setUpRecyclerView(){
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.adapter = transactionAdapter
    }

    private fun redirectToDetails(transaction: Transaction){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val categoryDetailsFragment = PlaceholderFragment()
        val bundle = Bundle()
        bundle.putParcelable("transaction", transaction)
        bundle.putString("screen", "redirectToDetails transaction")
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
}