package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import com.opsc.opsc7312.view.observers.HomeTransactionsObserver
import com.opsc.opsc7312.view.observers.TransactionsObserver


class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionList: ArrayList<Transaction>
    private lateinit var income: ArrayList<Transaction>
    private lateinit var expenses: ArrayList<Transaction>
    private lateinit var categoryList: ArrayList<Category>

    private lateinit var transactionAdapter: TransactionAdapter

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonIncome: AppCompatRadioButton
    private lateinit var radioButtonExpense: AppCompatRadioButton

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var transactionViewModel: TransactionController

    private lateinit var authController: AuthController



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        categoryList = arrayListOf<Category>()
        transactionList = arrayListOf<Transaction>()
        income = arrayListOf<Transaction>()
        expenses = arrayListOf<Transaction>()

        radioGroup = binding.radioGroup
        radioButtonIncome = binding.income
        radioButtonExpense = binding.expense

        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        transactionViewModel = ViewModelProvider(this).get(TransactionController::class.java)

        authController = ViewModelProvider(this).get(AuthController::class.java)

        transactionAdapter = TransactionAdapter{
            transaction ->
            redirectToDetails(transaction)
        }
        setUpRecyclerView()

        radioButtonIncome.setOnClickListener { onRadioButtonClicked(it) }
        radioButtonExpense.setOnClickListener { onRadioButtonClicked(it) }


        setUpUserDetails()

        return binding.root
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

    private fun setUpUserDetails(){
        val user = userManager.getUser()

        val token = tokenManager.getToken()


        if(token != null){
            Log.d("re auth", "this is token: $token")
            observeViewModel(token, user.id)
        }else{
            Log.d("re auth", "hola me amo dora")
            reAuthenticateUser(user.email, user.id)
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

        transactionViewModel.transactionList.observe(viewLifecycleOwner,
            TransactionsObserver(transactionAdapter)
        )

        transactionViewModel.getAllTransactions(token, userId)
    }

    private fun reAuthenticateUser(email: String, userId: String){
        authController.newToken.observe(viewLifecycleOwner){
                response ->
            if(response != null){
                tokenManager.saveToken(response.token, AppConstants.tokenExpirationTime())
                observeViewModel(response.token, userId)
            }
        }

        authController.message.observe(viewLifecycleOwner){
                message -> Log.d("authController", message)
        }

        val user = User(email = email)

        authController.reauthenticate(user)
    }



    private fun changeCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun onRadioButtonClicked(view: View) {
        val isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.income -> {
                if (isSelected) {
                    // Handle the action for "All" button
                    radioButtonIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioButtonExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
                    transactionAdapter.updateTransactions(income)

                    binding.transactionTitle.text = "Total Income"
                    binding.amount.text = "${totalTransactionAmount(income)} ZAR"
                }
            }
            R.id.expense -> {
                if (isSelected) {
                    // Handle the action for "Today" button
                    radioButtonExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    radioButtonIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
                    //viewModel.getTodayLeaderboard()
                    transactionAdapter.updateTransactions(expenses)

                    binding.transactionTitle.text = "Total Expenses"
                    binding.amount.text = "${totalTransactionAmount(expenses)} ZAR"
                }
            }
        }
    }

    fun totalTransactionAmount(transactions: ArrayList<Transaction>): Double{
        var total = 0.00

        for (transaction in transactions) {
            total += transaction.amount
        }
        return total
    }
}