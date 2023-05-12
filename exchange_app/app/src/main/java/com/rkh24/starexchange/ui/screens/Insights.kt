package com.rkh24.starexchange.ui.screens

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rkh24.starexchange.InsightsViewModel
import com.rkh24.starexchange.R
import com.rkh24.starexchange.data.InsightsResponse
import com.rkh24.starexchange.ui.theme.*
import java.time.LocalDate
import java.util.*



@Composable
fun Toolbar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .padding(horizontal = 100.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        }

        Text(
            text = "Pick a date range",
            color = PrimaryGrey,
            fontFamily = poppins,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

    }



@Composable
fun DataCard(
    modifier: Modifier = Modifier,
    icon: Int,
    mainText: String,
    subText: String,
    data: String,
) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .width(154.dp)
            .clip(Shapes.medium),
        elevation = 0.dp
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Row {
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(IconBackground)
                        .size(36.dp)
                        .padding(5.dp),
                    tint = White,
                    painter = painterResource(id = icon),
                    contentDescription = ""
                )
                Column(Modifier.padding(start = 10.dp)) {
                    Text(
                        text = mainText,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = subText,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.width(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data,
                    color = Color.Gray,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )

            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomDatePicker(
    selectedDate: MutableState<LocalDate>,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = LocalDate.now()
) {
    val context = LocalContext.current
    val dateSetListener = remember {
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate.value = LocalDate.of(year, month + 1, dayOfMonth)
        }
    }

    val onClick = {
        val calendar = Calendar.getInstance().apply {
            time = java.sql.Date.valueOf(selectedDate.value.toString())
        }

        val datePickerDialog = DatePickerDialog(
            context,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        minDate?.let { datePickerDialog.datePicker.minDate = it.toEpochDay() * 24 * 60 * 60 * 1000 }
        maxDate?.let { datePickerDialog.datePicker.maxDate = it.toEpochDay() * 24 * 60 * 60 * 1000 }
        datePickerDialog.show()
    }

    Button(onClick = onClick) {
        Text(text = selectedDate.value.toString(),color=Black)
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InsightsUI(insightsViewModel: InsightsViewModel = viewModel()) {
    val insightsResponse by insightsViewModel.insightsResponse
    val dateRange = remember { mutableStateOf(Pair(LocalDate.now().minusDays(6), LocalDate.now())) }
    val startDate = remember { mutableStateOf(dateRange.value.first) }
    val endDate = remember { mutableStateOf(dateRange.value.second) }

    LaunchedEffect(startDate.value, endDate.value) {
        dateRange.value = Pair(startDate.value, endDate.value)
    }

    if (insightsResponse != null) {
        Column {
            Toolbar()
            DateRangePicker(startDate = startDate, endDate = endDate)
            OverviewUI(insightsResponse!!, dateRange.value)
        }
    } else {
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateRangePicker(startDate: MutableState<LocalDate>, endDate: MutableState<LocalDate>) {
    Row { Modifier
        .fillMaxWidth()
        Spacer(modifier = Modifier.width(128.dp))

        CustomDatePicker(
            selectedDate = startDate,
            maxDate = endDate.value
        )
        Spacer(modifier = Modifier.width(16.dp))
        CustomDatePicker(
            selectedDate = endDate,
            minDate = startDate.value.plusDays(1),
            maxDate = LocalDate.now()
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewUI(insightsResponse: InsightsResponse, dateRange: Pair<LocalDate, LocalDate>) {
    val filteredData = insightsResponse.filterDataByDateRange(dateRange.first, dateRange.second)
    val averageHighestRate =filteredData.highest_rate.averageFloat()
    val averageLowestRate = filteredData.lowest_rate.averageFloat()
    val totNumberOfTransactions = filteredData.number_of_transactions.sumInt()
    val totNUmberOfTransactionSell = filteredData.transactions_to_sell_usd.sumInt()
    val totNUmberOfTransactionBuy = filteredData.transactions_to_buy_usd.sumInt()
    val sell=filteredData.usd_amount_sell.sumFloat()
    val buy=filteredData.usd_amount_buy.sumFloat()
    val total=buy+sell

    // Calculate other averages as needed

    // Display averages using UI components
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DataCard(
            icon = R.drawable.baseline_trending_up_24,
            mainText = "Highest Rate",
            subText = "Avg. in USD",
            data = averageHighestRate.toString(),

        )
        DataCard(
            icon = R.drawable.baseline_trending_down_24,
            mainText = "Lowest Rate",
            subText = "Avg. in USD",
            data = averageLowestRate.toString(),

        )
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DataCard(
            icon = R.drawable.baseline_import_export_24,
            mainText = "Number of Sell Transactions ",
            subText = "",
            data = totNUmberOfTransactionSell.toString(),

        )
        DataCard(
            icon = R.drawable.baseline_currency_exchange_24,
            mainText = "Volume of Sell Transactions",
            subText = "in USD",
            data = sell.toString(),

        )
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DataCard(
            icon = R.drawable.baseline_import_export_24,
            mainText = "Number of Buy Transactions",
            subText = "",
            data = totNUmberOfTransactionBuy.toString(),

        )
        DataCard(
            icon = R.drawable.baseline_currency_exchange_24,
            mainText = "Volume of Buy Transactions",
            subText = "in USD",
            data = buy.toString(),

        )
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DataCard(
            icon = R.drawable.baseline_import_export_24,
            mainText = "Number of Total Transactions",
            subText = "",
            data = totNumberOfTransactions.toString(),

        )
        DataCard(
            icon = R.drawable.baseline_currency_exchange_24,
            mainText = "Volume of Total Transactions",
            subText = "in USD",
            data = total.toString(),

        )
    }



}
@RequiresApi(Build.VERSION_CODES.O)
fun InsightsResponse.filterDataByDateRange(startDate: LocalDate, endDate: LocalDate): InsightsResponse {
    val dateRange = startDate..endDate

    fun <T> Map<String, T>?.filterByDateRange(): Map<String, T> {
        if (this == null) return emptyMap()
        return filterKeys { LocalDate.parse(it) in dateRange }
    }

    return InsightsResponse(
        highest_rate = highest_rate.filterByDateRange(),
        lowest_rate = lowest_rate.filterByDateRange(),
        number_of_transactions = number_of_transactions.filterByDateRange(),
        transactions_to_buy_usd = transactions_to_buy_usd.filterByDateRange(),
        transactions_to_sell_usd = transactions_to_sell_usd.filterByDateRange(),
        usd_amount_sell= usd_amount_sell.filterByDateRange(),
        usd_amount_buy = usd_amount_buy.filterByDateRange()
    )
}


fun Map<String, Float>.averageFloat(): Float {
    return if (isEmpty()) 0f else values.sum() / size
}
fun Map<String, Int>.sumInt(): Any {
    return if (isEmpty()) 0f else values.sum()
}
fun Map<String, Float>.sumFloat(): Float {
    return if (isEmpty()) 0f else values.sum()
}
