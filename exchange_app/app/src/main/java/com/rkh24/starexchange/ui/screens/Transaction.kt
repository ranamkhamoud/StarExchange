package com.rkh24.starexchange.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rkh24.starexchange.TransactionViewModel
import com.rkh24.starexchange.api.model.Transaction
import com.rkh24.starexchange.ui.theme.PrimaryGrey

@ExperimentalMaterialApi
@Composable
fun TransactionList(transactions: List<Transaction>) {

    LazyColumn {
        items(transactions) { transaction ->
            val description = buildString {
                append("ID: ${transaction.id}\n")
                append("USD Amount: ${transaction.usdAmount}\n")
                append("LBP Amount: ${transaction.lbpAmount}\n")
                append("USD to LBP: ${transaction.usdToLbp}\n")
                append("User ID: ${transaction.userId}")
            }

            ExpandableCard(
                title = transaction.addedDate ?: "N/A",
                description = description
            )
            Spacer(modifier = Modifier.height(12.dp)) // Add spacing between cards
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TableUI() {
    val transactionsViewModel: TransactionViewModel = viewModel()
    val transactions by transactionsViewModel.transactions.observeAsState()
    val error by transactionsViewModel.error.observeAsState()

    LaunchedEffect(key1 = transactionsViewModel) {
        transactionsViewModel.fetchTransactions()
    }

    Column {
        Text(
            text = " ",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = PrimaryGrey
        )

        Spacer(modifier = Modifier.height(32.dp)) // Add spacing between title and transactions list

        when {
            transactions == null -> {
                // Loading state
                Text(text = "Loading transactions...")
            }
            error != null -> {
                // Error state
                Text(text = "Error: ${error!!.message}")
            }
            else -> {
                // Success state
                TransactionList(transactions = transactions ?: emptyList())
            }
        }
    }
}