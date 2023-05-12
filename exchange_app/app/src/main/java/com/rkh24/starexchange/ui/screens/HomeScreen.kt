package com.rkh24.starexchange.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rkh24.starexchange.*
import com.rkh24.starexchange.R
import com.rkh24.starexchange.api.model.ExchangeCard
import com.rkh24.starexchange.api.model.Transaction
import com.rkh24.starexchange.ui.theme.*


@Composable
fun HomeScreenUI(
    onNavigateToCalculator: () -> Unit,
    onNavigateToTransaction: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToTrends: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToNews : () -> Unit,
    onNavigateToHelp: () -> Unit,
    viewModel: ExchangeCardViewModel,
    userViewModel: GetUserViewModel,
    transactionViewModel: TransactionViewModel

    ) {
    val transactions = remember { mutableStateOf(emptyList<ExchangeCard>()) }
    LaunchedEffect(Unit) {
        viewModel.getExchangeTransactions(
            onResponse = { fetchedTransactions ->
                transactions.value = fetchedTransactions
            },
            onFailure = {
            }
        )
    }
    val currentUserId = remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        userViewModel.getUser(
            onResponse = { user ->
                currentUserId.value = user.id!!
            },
            onFailure = {
            }
        )
    }
    val currentUserName = remember { mutableStateOf("Null") }
    LaunchedEffect(Unit) {
        userViewModel.getUser(
            onResponse = { user ->
                currentUserName.value = user.username.toString()
            },
            onFailure = {

            }
        )
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        HeaderUI(onNavigateToHelp =onNavigateToHelp,currentUserName)
        CardUI()
        DataUI()
        ServicesUI(
            onNavigateToCalculator = onNavigateToCalculator,
            onNavigateToTransaction = onNavigateToTransaction,
            onNavigateToInsights = onNavigateToInsights,
            onNavigateToTrends= onNavigateToTrends,
            onNavigateToNews= onNavigateToNews
        )
        SendMoneyUI(
            exchangeData =transactions.value.toMutableList(),currentUserId)
            YourOfferingsUI(exchangeData =transactions.value.toMutableList(),currentUserId,viewModel=viewModel, transactionViewModel = transactionViewModel)
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Button(onClick = onLogout) {
            Text("Logout", color = Color.Black)
        }
    }


}



@Composable
fun SendMoneyUI(
    exchangeData: MutableList<ExchangeCard>,
    currentUserId: MutableState<Int>, // Pass the current user's ID
) {
    val otherTransactions = exchangeData.filter { it.requestUserId != currentUserId.value }
    val showDialog = remember { mutableStateOf(false) }
    val selectedExchangeCard = remember { mutableStateOf<ExchangeCard?>(null) }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Open Offerings",
            color = PrimaryGrey,
            fontFamily = poppins,
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
        LazyRow {
            items(otherTransactions) { item ->
                SendMoneyItemUI(item, onClick = {
                    selectedExchangeCard.value = item
                    showDialog.value = true
                }) // Show the dialog when an item is clicked
            }
        }
    }

    if (showDialog.value) {
        ExchangeCardDetailsDialog(
            exchangeCard = selectedExchangeCard.value,
            onDismiss = { showDialog.value = false }
        )
    }
}

@Composable
fun ExchangeCardDetailsDialog(exchangeCard: ExchangeCard?, onDismiss: () -> Unit) {
    if (exchangeCard != null) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = "Exchange Card Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color= StarBlue
                )
            },
            text = {
                Column {
                    Text(text = "Title: ${exchangeCard.exchangeId}",color= Color.Black)
                    Text(text = "Description: ${exchangeCard.requestUserId}",color= Color.Black)
                    Text(text = "Amount: ${exchangeCard.userEmail}",color=Color.Black)
                    // Add more fields as needed
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Close",color= Color.Black)
                    }
                }
            }
        )
    }
}



@Composable
fun YourOfferingsUI(
    exchangeData: MutableList<ExchangeCard>,
    currentUserId: MutableState<Int>, // Pass the current user's ID
    viewModel: ExchangeCardViewModel,
    transactionViewModel: TransactionViewModel
) {
    val userTransactions = exchangeData.filter { it.requestUserId == currentUserId.value }
    val selectedExchangeCard = remember { mutableStateOf<ExchangeCard?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Your Offerings",
            color = PrimaryGrey,
            fontFamily = poppins,
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        LazyRow {
            items(userTransactions) { item ->
                SendMoneyItemUI(item, onClick = {
                    selectedExchangeCard.value = item
                    showDialog.value = true
                }) // Show the dialog when an item is clicked
            }
        }

        if (showDialog.value) {
            YourExchangeCardDetailsDialog(
                exchangeCard = selectedExchangeCard.value,
                onDismiss = { showDialog.value = false },
                viewModel = viewModel,
                transactionViewModel = transactionViewModel
            )
        }
    }
}

@Composable
fun YourExchangeCardDetailsDialog(
    exchangeCard: ExchangeCard?,
    onDismiss: () -> Unit,
    viewModel: ExchangeCardViewModel,
    transactionViewModel: TransactionViewModel
) {

    if (exchangeCard != null) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = "Exchange Card Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = StarBlue
                )
            },
            text = {
                Column {
                    Text(text = "Exchange Id: ${exchangeCard.exchangeId}", color = Color.Black)
                    Text(text = "Buy Amount: ${exchangeCard.buyAmount}", color = Color.Black)
                    Text(text = "Sell Amount: ${exchangeCard.sellAmount}", color = Color.Black)
                    Text(text = "USD to LBP: ${exchangeCard.usdToLbp}", color = Color.Black)
                    Text(text = "Email: ${exchangeCard.userEmail}", color = Color.Black)
                    // Add more fields as needed
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            exchangeCard.exchangeId?.let {
                                viewModel.deleteExchange(
                                    transactionId = it,
                                    onResponse = { onDismiss() },
                                    onFailure = { /* Handle failure here */ }
                                )
                            }
                        }
                    ) {
                        Text("Delete",color= Color.Black)
                    }
                    Button(
                        onClick = {
                            if(exchangeCard.usdToLbp==true) {
                                val transaction = Transaction().apply {
                                    usdAmount = exchangeCard.buyAmount
                                    lbpAmount = exchangeCard.sellAmount
                                    usdToLbp = exchangeCard.usdToLbp
                                }
                                transactionViewModel.addTransaction(
                                    transaction , onResponse = {
//                                        Toast.makeText(
//                                            LocalContext.current,
//                                            "Transaction added!",
//                                            Toast.LENGTH_LONG
//                                        ).show()
                                        onDismiss()
                                    },
                                    onFailure = { /* Handle failure here */ }
                                )
                            }
                            else {
                                val transaction = Transaction().apply {
                                    usdAmount = exchangeCard.sellAmount
                                    lbpAmount = exchangeCard.buyAmount
                                    usdToLbp = exchangeCard.usdToLbp
                                }

                                transactionViewModel.addTransaction(
                                    transaction , onResponse = {
//                                        Toast.makeText(
//                                            LocalContext.current,
//                                            "Transaction added!",
//                                            Toast.LENGTH_LONG
//                                        ).show()
                                        onDismiss()
                                    },
                                    onFailure = { /* Handle failure here */ }
                                )
                            }
                        }
                    ) {
                        Text("Add Transaction",color= Color.Black)
                    }
                }
            }
        )
    }
}

@Composable
fun SendMoneyItemUI(item: ExchangeCard, onClick: () -> Unit) {
    Card(
        elevation = 0.dp,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(end = 6.dp)
            .border(width = 0.dp, color = LightGrey2, shape = Shapes.medium)
            .padding(vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.size(width = 80.dp, height = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text="",
                color = PrimaryGrey,
                modifier = Modifier
                    .alpha(0.6f)
                    .padding(top = 6.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = poppins
            )
            Text(
                text = item.sellAmount.toString() + "<->"+item.buyAmount.toString(),
                color = PrimaryGrey,
                modifier = Modifier.alpha(0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = poppins
            )
        }
    }
}

@Composable
fun ServicesUI(onNavigateToCalculator: () -> Unit,onNavigateToTransaction: () -> Unit,onNavigateToInsights: () -> Unit, onNavigateToTrends: () -> Unit,onNavigateToNews: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Text(
            text = "Services",
            color = PrimaryGrey,
            fontFamily = FontFamily(Font(R.font.poppins)),
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ServiceUI(R.drawable.ic_money_send, "Insights", Service1,onClick = onNavigateToInsights)
            ServiceUI(R.drawable.ic_bill, "Trans", Service2, onClick = onNavigateToTransaction)
            ServiceUI(R.drawable.baseline_calculate_24, "Calculator", Service3, onClick = onNavigateToCalculator)
            ServiceUI(R.drawable.baseline_insights_24, "Trends", Service4, onClick=onNavigateToTrends)
            ServiceUI(R.drawable.baseline_library_books_24, "News", Service5, onClick=onNavigateToNews)

        }
    }
}

@Composable
fun ServiceUI(id: Int, text: String, color: Color, onClick: (() -> Unit)? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(
            onClick = { onClick?.invoke() },
            modifier = Modifier.size(60.dp),  //avoid the oval shape
            shape = CircleShape,
            border = BorderStroke(0.dp, color),
            contentPadding = PaddingValues(0.dp),  //avoid the little icon
            colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
        ) {
            Icon(painter = painterResource(id = id), contentDescription = "content description")
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = text,
            color = PrimaryGrey,
            modifier = Modifier.alpha(0.6f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(R.font.poppins))
        )
    }
}

@Composable
fun DataUI(exchangeViewModel: ExchangeViewModel = viewModel()) {
    val buyRate: Float? by exchangeViewModel.buyRate.observeAsState()
    val sellRate: Float? by exchangeViewModel.sellRate.observeAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 30.dp), Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = buyRate?.toString() ?: "null",
                color = PrimaryGrey,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.poppins)),
                lineHeight = 20.sp
            )
            Text(
                text = "Buy USD",
                color = PrimaryGrey,
                fontFamily = FontFamily(Font(R.font.poppins)),
                modifier = Modifier.alpha(0.6f),
                fontSize = 14.sp
            )
        }
        Column {
            Text(
                text = sellRate?.toString() ?: "null",
                color = PrimaryGrey,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.poppins)),
                lineHeight = 20.sp
            )
            Text(
                text = "Sell USD",
                color = PrimaryGrey,
                fontFamily = FontFamily(Font(R.font.poppins)),
                modifier = Modifier.alpha(0.6f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CardUI() {
    val showDialog1 = remember { mutableStateOf(false) }
    val showDialog2 = remember { mutableStateOf(false) }

    Box {
        Card(
            backgroundColor = CardRed,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(160.dp),
        ) {
            Row {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Click to Add",
                        color = Color.White,
                        modifier = Modifier
                            .alpha(0.6f)
                            .padding(top = 10.dp),
                        fontFamily = FontFamily(
                            Font(R.font.poppins)
                        )
                    )
                    Text(
                        text = "Transaction",
                        color = Color.White,
                        fontFamily = FontFamily(
                            Font(R.font.poppins)
                        ),
                        fontSize = 25.sp
                    )
                    Button(
                        onClick = { showDialog1.value = true },
                        modifier = Modifier
                            .clip(Shapes.large)
                            .border(width = 0.dp, color = Color.Transparent, shape = Shapes.large),
                        colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryGrey),
                    ) {
                        Text(
                            text = "Add Transaction",
                            fontSize = 12.sp,
                            modifier = Modifier.align(alignment = CenterVertically)
                        )
                    }
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Click to Add",
                        color = Color.White,
                        modifier = Modifier
                            .alpha(0.6f)
                            .padding(top = 10.dp),
                        fontFamily = FontFamily(
                            Font(R.font.poppins)
                        )
                    )
                    Text(
                        text = "Offering",
                        color = Color.White,
                        fontFamily = FontFamily(
                            Font(R.font.poppins)
                        ),
                        fontSize = 25.sp
                    )
                Button(
                    onClick = { showDialog2.value = true },
                    modifier = Modifier
                        .clip(Shapes.large)
                        .border(width = 0.dp, color = Color.Transparent, shape = Shapes.large),
                    colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryGrey),
                ) {
                    Text(
                        text = "Add Offering",
                        fontSize = 12.sp,
                        modifier = Modifier.align(alignment = CenterVertically)
                    )
                } }

            }
        }

        if (showDialog1.value) {
            val transactionViewModel = viewModel<TransactionViewModel>()

            TransactionDialog(
                onDismiss = {
                    // Handle dismiss action here
                    println("Dialog dismissed")
                    showDialog1.value = false
                },
                transactionViewModel = transactionViewModel
            )
        }
        if (showDialog2.value) {
            val exchangeCardViewModel = viewModel<ExchangeCardViewModel>()

            OfferingDialog(
                onDismiss = {
                    // Handle dismiss action here
                    println("Dialog dismissed")
                    showDialog1.value = false
                },
                exchangeCard = exchangeCardViewModel
            )
        }
    }
}

@Composable
fun HeaderUI(onNavigateToHelp: () -> Unit, Name: MutableState<String>) {
    Box(
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(IconBackground)
                    .size(20.dp)
                    .clickable(onClick = { onNavigateToHelp()}),
                tint = whiteBackground,
                painter = painterResource(id = R.drawable.baseline_help_outline_24),
                contentDescription = ""
            )
            Text(
                text = "Welcome Back "+ Name.value,
                color = PrimaryGrey,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth())
    }
}

