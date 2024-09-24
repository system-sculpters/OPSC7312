package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import com.opsc.opsc7312.view.custom.FilterBottomSheet
import com.opsc.opsc7312.view.custom.SortBottomSheet
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.TransactionsObserver


class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionList: MutableList<Transaction>
    private lateinit var income: ArrayList<Transaction>
    private lateinit var expenses: ArrayList<Transaction>
    private lateinit var categoryList: ArrayList<Category>

    private lateinit var transactionAdapter: TransactionAdapter

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var transactionViewModel: TransactionController

    private lateinit var authController: AuthController
    private lateinit var timeOutDialog: TimeOutDialog

    private lateinit var sortBottomSheet: SortBottomSheet
    private lateinit var filterBottomSheet: FilterBottomSheet


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        categoryList = arrayListOf<Category>()
        transactionList = mutableListOf<Transaction>()
        income = arrayListOf<Transaction>()
        expenses = arrayListOf<Transaction>()
        filterBottomSheet = FilterBottomSheet()
        sortBottomSheet = SortBottomSheet()


        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        transactionViewModel = ViewModelProvider(this).get(TransactionController::class.java)

        authController = ViewModelProvider(this).get(AuthController::class.java)

        transactionAdapter = TransactionAdapter{
            transaction ->
            redirectToDetails(transaction)
        }

        filterBottomSheet.setAdapter(transactionAdapter)

        sortBottomSheet.setAdapter(transactionAdapter)

        timeOutDialog = TimeOutDialog()

        binding.filter.setOnClickListener{
            filterBottomSheet.show(childFragmentManager, "FilterBottomSheet")
        }

        binding.sort.setOnClickListener{
            sortBottomSheet.show(childFragmentManager, "SortBottomSheet")
        }

        setUpRecyclerView()



        setUpUserDetails()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Transactions")
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

    private fun setUpUserDetails(){
        val user = userManager.getUser()

        val token = tokenManager.getToken()


        if(token != null){
            Log.d("re auth", "this is token: $token")
            observeViewModel(token, user.id)
        }else{
            Log.d("re auth", "hola me amo dora")
        }
    }



    private fun observeViewModel(token: String, userId: String){
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe LiveData
        transactionViewModel.status.observe(viewLifecycleOwner)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                // Success
                progressDialog.dismiss()
            } else {
                // Failure
                progressDialog.dismiss()
            }

        }

        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            // Show message to the user, if needed
            Log.d("Transactions message", message)

            if(message == "timeout" || message.contains("Unable to resolve host")){
                timeOutDialog.showTimeoutDialog(requireContext() ){
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "Connecting...", hideProgressBar = false)
                    transactionViewModel.getAllTransactions(token, userId)
                }
            }
        }

        transactionViewModel.transactionList.observe(viewLifecycleOwner,
            TransactionsObserver(sortBottomSheet, filterBottomSheet, transactionAdapter, binding.amount)
        )

        transactionViewModel.getAllTransactions(token, userId)
    }



    private fun changeCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}