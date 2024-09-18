package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentHomeBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.HomeTransactionsObserver


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: TransactionAdapter

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var transactionViewModel: TransactionController

    private lateinit var authController: AuthController

    private lateinit var timeOutDialog: TimeOutDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        transactionViewModel = ViewModelProvider(this).get(TransactionController::class.java)

        authController = ViewModelProvider(this).get(AuthController::class.java)

        timeOutDialog = TimeOutDialog()

        transactionAdapter = TransactionAdapter{
                transaction ->
            redirectToDetails(transaction)
        }

        binding.seeAll.setOnClickListener {
            changeCurrentFragment(TransactionsFragment())
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
            Log.d("re auth", "this is token: $token")
            observeViewModel(token, user.id)
        }else{

        }
    }



    private fun observeViewModel(token: String, userId: String){
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        Log.d("token", token)

        Log.d("userId", userId)
        // Observe LiveData
        transactionViewModel.status.observe(viewLifecycleOwner)  {
            // Handle status changes (success or failure)
            status ->
            if (status) {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Transaction retrieval successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                }, 1000)

            } else {
                timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Transaction retrieval failed!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                }, 2000)
            }

        }

        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            if(message == "timeout"){
                timeOutDialog.showTimeoutDialog(requireContext() ){
                    //progressDialog.show()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Connecting...", hideProgressBar = false)
                    transactionViewModel.getAllTransactions(token, userId)
                }
            }
        }

        transactionViewModel.transactionList.observe(viewLifecycleOwner, HomeTransactionsObserver(transactionAdapter, binding.amount, binding.incomeAmount, binding.expenseAmount))

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
        val transactionDetailsFragment = UpdateTransactionFragment()
        val bundle = Bundle()
        bundle.putParcelable("transaction", transaction)
        transactionDetailsFragment.arguments = bundle

        // Navigate to CategoryDetailsFragment
        changeCurrentFragment(transactionDetailsFragment)
    }

    private fun changeCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}