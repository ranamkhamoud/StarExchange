package com.rkh24.starexchange.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rkh24.starexchange.TransactionViewModel
import com.rkh24.starexchange.api.model.Transaction
import com.rkh24.starexchange.ui.theme.LightGrey2
import com.rkh24.starexchange.ui.theme.PrimaryGrey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TransactionDialog(
    onDismiss: () -> Unit,
    transactionViewModel: TransactionViewModel
) {
    val context = LocalContext.current

    val input1Value = remember { mutableStateOf("") }
    val input2Value = remember { mutableStateOf("") }
    val toggleState = remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            elevation = 5.dp,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .border(2.dp, color = LightGrey2, shape = RoundedCornerShape(15.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {

                Text(
                    text = "Please fill in the details and select an option.",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    color = PrimaryGrey
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    TextField(
                        value = input1Value.value,
                        onValueChange = { input1Value.value = it },
                        label = { Text("USD amount") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black
                        )
                    )
                    TextField(
                        value = input2Value.value,
                        onValueChange = { input2Value.value = it },
                        label = { Text("LBP amount") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("BUY", color = Color.Black)

                        Switch(
                            checked = toggleState.value,
                            onCheckedChange = { toggleState.value = it }
                        )
                        Text("SELL", color = Color.Black)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(

                        onClick = {
                            val inputUsdAmount = input1Value.value.toFloatOrNull()
                            val inputLbpAmount = input2Value.value.toFloatOrNull()
                            val inputUsdToLbp = toggleState.value

                            if (inputUsdAmount != null && inputLbpAmount != null) {
                                val transaction = Transaction().apply {
                                    usdAmount = inputUsdAmount
                                    lbpAmount = inputLbpAmount
                                    usdToLbp = inputUsdToLbp
                                }
                                transactionViewModel.addTransaction(
                                    transaction,
                                    onResponse = {
                                        Toast.makeText(
                                            context,
                                            "Transaction added!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        onDismiss()
                                    },
                                    onFailure = {
                                        Toast.makeText(
                                            context,
                                            "Could not add transaction.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please fill in all fields.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = PrimaryGrey,
                            contentColor = White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Add",
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

