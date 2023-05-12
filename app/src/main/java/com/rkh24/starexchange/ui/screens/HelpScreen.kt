package com.rkh24.starexchange.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rkh24.starexchange.ui.theme.PrimaryGrey

@Composable
fun HelpUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to the Help section!\n\n" +
                    "To navigate the app, use the home page .\n\n" +
                    "Click on add offerings to post offers for other users.\n\n" +
                    "Click on add transactions to add a transaction you made.\n\n" +
                    "You can view services like insights, trends, news, your transactions.\n\n" +
                    "Click on the card to view posted offerings, or to edit yours." +
                    "If you have any further questions or issues, please contact our support team.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color= PrimaryGrey
        )

        Spacer(modifier = Modifier.height(16.dp))


        }
    }

