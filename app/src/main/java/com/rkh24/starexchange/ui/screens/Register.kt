package com.rkh24.starexchange.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rkh24.starexchange.RegistrationViewModel
import com.rkh24.starexchange.ui.theme.PrimaryGrey
import com.rkh24.starexchange.ui.theme.Service4
import com.rkh24.starexchange.ui.theme.Shapes

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onRegistrationCompleted: () -> Unit,
    onRegistrationFailed: (String) -> Unit,
    navigateToLogin: () -> Unit
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface(color = White, modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
//                Image(
//                    painterResource(), // Replace with your logo resource
//                    contentDescription = "Logo",
//                    modifier = Modifier
//                        .size(500.dp)
//                        .align(Alignment.CenterHorizontally)
//                )
                Spacer(modifier = Modifier.height(32.dp))
                Surface(
                    color = Service4,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().offset(y = (-32).dp)
                ) {
                    Column {
                        TextField(
                            value = username.value,
                            onValueChange = { newValue -> username.value = newValue },
                            label = { Text("Username", color = Color.Black) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = password.value,
                            onValueChange = { newValue -> password.value = newValue },
                            label = { Text("Password", color = Color.Black) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = confirmPassword.value,
                            onValueChange = { newValue -> confirmPassword.value = newValue },
                            label = { Text("Confirm Password", color = Color.Black) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (password.value == confirmPassword.value) {
                            viewModel.addUser(username.value, password.value,
                                onSuccess = {
                                    viewModel.authenticate(username.value, password.value,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Registration successful!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            onRegistrationCompleted()
                                        },
                                        onFailure = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                            onRegistrationFailed(error)
                                        }
                                    )
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    onRegistrationFailed(error)
                                }
                            )
                        } else {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG)
                                .show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                        .clip(Shapes.large)
                        .border(width = 0.dp, color = Color.Transparent, shape = Shapes.large),
                    colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryGrey),
                ) {
                    Text("Register", color = Color.Black)
                }
                Text(
                    text = errorMessage.value,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = { navigateToLogin() },
                    modifier = Modifier.padding(top = 8.dp)
                    .clip(Shapes.large)
                    .border(width = 0.dp, color = Color.Transparent, shape = Shapes.large),
                colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryGrey),
                ) {
                    Text("Back to Login", color = Color.Black)
                }
            }
        }
    }
}
