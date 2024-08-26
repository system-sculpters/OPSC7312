package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312.model.data.Category
import com.opsc.opsc7312.model.data.Transaction
import com.opsc.opsc7312.view.adapter.TransactionAdapter
import java.math.BigDecimal


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


        transactionAdapter = TransactionAdapter{
            transaction ->
            redirectToDetails(transaction)
        }
        setUpRecyclerView()

        radioButtonIncome.setOnClickListener { onRadioButtonClicked(it) }
        radioButtonExpense.setOnClickListener { onRadioButtonClicked(it) }


        categoryList()

        return binding.root
    }

    private fun setUpRecyclerView(){
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.adapter = transactionAdapter
    }

    private fun categoryList(){

        val cat1 = Category(id = "id", name = "blue", color = "Blue", icon = "yellow", transactiontype = AppConstants.TRANSACTIONTYPE.INCOME.name,
            userid = "userid")

        val cat2 = Category(id = "id", name = "red", color = "Red", icon = "green", transactiontype = AppConstants.TRANSACTIONTYPE.INCOME.name,
            userid = "userid")

        val cat3 = Category(id = "id", name = "yellow", color = "Yellow", icon = "red", transactiontype = AppConstants.TRANSACTIONTYPE.INCOME.name,
            userid = "userid")

        val cat4 = Category(id = "id", name = "green", color = "Green", icon = "blue", transactiontype = AppConstants.TRANSACTIONTYPE.INCOME.name,
            userid = "userid")

        categoryList.add(cat1)
        categoryList.add(cat2)
        categoryList.add(cat3)
        categoryList.add(cat4)

        val Transact1 = Transaction(
            name = "transaction 1", isRecurring = true, type = "Income",
            amount =  3200.00, category = cat1)

        val Transact2 = Transaction(
            name = "transaction 2", type = "Expense",
            amount =  1500.00, category = cat2)

        val Transact3 = Transaction(
            name = "transaction 3", isRecurring = true, type = "Income",
            amount =  4035.66, category = cat3)

        val Transact4 = Transaction(
            name = "transaction 4", type = "Expense",
            amount =  2060.91, category = cat4)

        transactionList.add(Transact1)
        transactionList.add(Transact2)
        transactionList.add(Transact3)
        transactionList.add(Transact4)

        transactionAdapter.updateTransactions(transactionList)

        for (transaction in transactionList) {
            if(transaction.type == "Income"){
                income.add(transaction)
            } else{
                expenses.add(transaction)
            }
        }
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