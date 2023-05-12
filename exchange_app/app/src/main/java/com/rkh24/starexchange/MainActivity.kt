package com.rkh24.starexchange

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rkh24.starexchange.api.Authentication
import com.rkh24.starexchange.ui.screens.*
import com.rkh24.starexchange.ui.theme.*
import kotlinx.coroutines.delay

sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
    object Registration : AppScreen("registration")
    object Home : AppScreen("home")
    object Calculator : AppScreen("calculator")
    object Transaction : AppScreen("transaction")
    object Insights : AppScreen("insights")
    object Trends : AppScreen("trends")
    object News: AppScreen("news")
    object Help: AppScreen("help")

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    val loginViewModel: LoginViewModel = viewModel()
    val registrationViewModel: RegistrationViewModel = viewModel()
    val exchangeCardViewModel: ExchangeCardViewModel= viewModel()
    val newsViewModel: NewsViewModel= viewModel()
    val userViewModel: GetUserViewModel= viewModel()
    val transactionViewModel:TransactionViewModel= viewModel()

            NavHost(navController = navController, startDestination = startDestination) {
        composable(AppScreen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                navController = navController,
                onLoginCompleted = {
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }},
                onLoginFailed = { error ->
                    Log.e("LoginError", "Error: $error")
                }
            )
        }

        composable(AppScreen.Registration.route) {
            RegistrationScreen(
                viewModel = registrationViewModel,
                onRegistrationCompleted = {
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Registration.route) { inclusive = true }
                    }
                },
                onRegistrationFailed = { error ->
                    Log.e("RegistrationError", "Error: $error")
                },
                navigateToLogin = {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Registration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Transaction.route) {
            Column {
                TopAppBar(
                    title = { Text(text = "Transaction", color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = null,tint= StarBlue)
                        }
                    }
                )
                TableUI()
            }
        }

        composable(AppScreen.Insights.route) {
            Column {
                TopAppBar(
                    title = { Text(text = "Insights", color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = "Back",tint= StarBlue)
                        }
                    }
                )
                InsightsUI()
            }
        }

        composable(AppScreen.Trends.route) {
            Column {
                TopAppBar(
                    title = { Text(text = "Trends", color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = null,tint= StarBlue)
                        }
                    }
                )
                TrendsUI()
            }
        }
        composable(AppScreen.Help.route) {
            Column {
                TopAppBar(
                    title = { Text(text = "Help Section", color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                contentDescription = null,tint= StarBlue
                            )
                        }
                    }
                )
                HelpUI()
            }
        }

        composable(AppScreen.News.route) {
            Column {
                TopAppBar(
                    title = { Text(text = "News", color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = null,tint= StarBlue)
                        }
                    }
                )
                NewsUI(newsViewModel)
            }
        }

        composable(AppScreen.Calculator.route) {
            Column {
                TopAppBar(
                    title = { Text(text = "Calculator", color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = null,tint= StarBlue)
                        }
                    }
                )
                CalculatorScreenUI()
            }
        }
        composable(AppScreen.Home.route) {
            HomeScreenUI(
                onNavigateToCalculator = { navController.navigate(AppScreen.Calculator.route) },
                onNavigateToTransaction = { navController.navigate(AppScreen.Transaction.route) },
                onNavigateToInsights = { navController.navigate(AppScreen.Insights.route) },
                onNavigateToTrends = { navController.navigate(AppScreen.Trends.route) },
                onNavigateToNews = { navController.navigate(AppScreen.News.route) },
                onNavigateToHelp = { navController.navigate(AppScreen.Help.route) },

                onLogout = {
                    Authentication.clearToken()
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                },
                viewModel=exchangeCardViewModel,
                userViewModel=userViewModel,
                transactionViewModel = transactionViewModel

            )
        }
    }
}
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Authentication.initialize(this) // Initialize Authentication
        ExchangeViewModelHolder.exchangeViewModel = ViewModelProvider(this)[ExchangeViewModel::class.java]

        setContent {
            ComposeDemo1Theme {
                Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val showSplash = remember { mutableStateOf(true) }
                    val startDestination = if (Authentication.getToken() == null) {
                        AppScreen.Login.route
                    } else {
                        AppScreen.Home.route
                    }

                    if (showSplash.value) {
                        SplashScreenUI()
                        LaunchedEffect(Unit) {
                            delay(2000) // Adjust the delay as needed
                            showSplash.value = false
                        }
                    } else {
                        AppNavHost(navController, startDestination)
                    }
                }
            }
        }
    }
}
