package com.rkh24.starexchange.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rkh24.starexchange.AppScreen
import com.rkh24.starexchange.LoginViewModel
import com.rkh24.starexchange.R
import com.rkh24.starexchange.ui.theme.LightGray
import com.rkh24.starexchange.ui.theme.PrimaryGrey
import com.rkh24.starexchange.ui.theme.Shapes

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navController: NavController,
    onLoginCompleted: () -> Unit,
    onLoginFailed: (String) -> Unit
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface( modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Image(
                    painterResource(R.drawable.logo), // Replace with your logo resource
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(500.dp) 
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(25.dp))
                Surface(color = LightGray, modifier = Modifier.fillMaxWidth().wrapContentHeight().offset(y = (-32).dp)) {
                    Column {
                        TextField(
                            value = username.value,
                            onValueChange = { newValue -> username.value = newValue },
                            label = { Text("Username", color = Color.Black) }, // Change input text to black
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black, // Change input text to black
                                cursorColor = Color.Black // Change cursor color to black
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = password.value,
                            onValueChange = { newValue -> password.value = newValue },
                            label = { Text("Password", color = Color.Black) }, // Change input text to black
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black, // Change input text to black
                                cursorColor = Color.Black // Change cursor color to black
                            ),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                                    Icon(
                                        painterResource(if (passwordVisibility.value) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24), // Replace with your icon resources
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.authenticate(username.value, password.value,
                            onSuccess = {
                                Toast.makeText(context, "Logged in!", Toast.LENGTH_LONG).show()
                                onLoginCompleted()
                            },
                            onFailure = {
                                Toast.makeText(context, "Wrong password or username", Toast.LENGTH_LONG).show()
                                onLoginFailed(errorMessage.value)
                            }
                        )
                    },
                    modifier = Modifier
                        .clip(Shapes.large)
                        .fillMaxWidth()
                        .border(width = 0.dp, color = Color.Transparent, shape = Shapes.large),
                    colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryGrey),
                ) {
                    Text("Log in", color = Color.Black)
                }
                Text(
                    text = errorMessage.value,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = {
                        navController.navigate(AppScreen.Registration.route)
                    },
                    modifier = Modifier
                        .clip(Shapes.large)
                        .border(width = 0.dp, color = Color.Transparent, shape = Shapes.large),
                    colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryGrey),) {
                    Text("Register", color = Color.White)
                }
            }
        }
    }
}

