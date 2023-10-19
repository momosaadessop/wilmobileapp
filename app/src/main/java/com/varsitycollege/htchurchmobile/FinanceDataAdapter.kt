package com.varsitycollege.htchurchmobile

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale

class FinanceDataAdapter(context: Context, resource: Int, objects: List<Finances.FinanceData>) :
    ArrayAdapter<Finances.FinanceData>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val financeData = getItem(position)

        val itemView = LayoutInflater.from(context).inflate(R.layout.history_finance, parent, false)

        // Set the data from FinanceData to your custom layout elements
        val expenseTextView = itemView.findViewById<TextView>(R.id.typeOfExpenseTextView)
        val tithesTextView = itemView.findViewById<TextView>(R.id.tithesTextView)
        val donationsTextView = itemView.findViewById<TextView>(R.id.donationsTextView)
        val fundRaiserTextView = itemView.findViewById<TextView>(R.id.fundRaiserTextView)
        val totalAmountTextView = itemView.findViewById<TextView>(R.id.totalAmountTextView)
        val confirmationTimeTextView = itemView.findViewById<TextView>(R.id.confirmationTimeTextView)

        expenseTextView.text = financeData?.typeOfExpense
        tithesTextView.text = financeData?.tithesValue.toString()
        donationsTextView.text = financeData?.donationsValue.toString()
        fundRaiserTextView.text = financeData?.fundRaiserValue.toString()
        totalAmountTextView.text = "Total Amount: R${financeData?.totalAmount}"

        // Format the timestamp and set it in the TextView
        // Format the timestamp to display only the month
        val confirmationTime = financeData?.confirmationTime
        val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
        val formattedDate = dateFormat.format(confirmationTime?.toDate())
        confirmationTimeTextView.text = "Date: $formattedDate"


        return itemView
    }
}
