package com.medibudget.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.medibudget.app.ui.screens.BiometricsSetupScreen
import com.medibudget.app.ui.screens.CostBreakdownScreen
import com.medibudget.app.ui.screens.CostEstimatorScreen
import com.medibudget.app.ui.screens.DashboardScreen
import com.medibudget.app.ui.screens.ForgotPasswordScreen
import com.medibudget.app.ui.screens.GenericFinderScreen
import com.medibudget.app.ui.screens.HelpFAQScreen
import com.medibudget.app.ui.screens.LoginScreen
import com.medibudget.app.ui.screens.MapFinderScreen
import com.medibudget.app.ui.screens.MedicineDetailScreen
import com.medibudget.app.ui.screens.OCRScannerScreen
import com.medibudget.app.ui.screens.OnboardingScreen
import com.medibudget.app.ui.screens.ProfileScreen
import com.medibudget.app.ui.screens.SOSModeScreen
import com.medibudget.app.ui.screens.SchemeCheckerScreen
import com.medibudget.app.ui.screens.SignupScreen
import com.medibudget.app.ui.screens.SplashScreen
import com.medibudget.app.ui.viewmodel.AuthViewModel
import com.medibudget.app.ui.viewmodel.HealthViewModel

import com.medibudget.app.ui.screens.AdminDashboardScreen
import com.medibudget.app.ui.screens.ReportsScreen
import com.medibudget.app.ui.screens.InsuranceCalcScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    healthViewModel: HealthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Splash.route
    ) {
        // Splash Screen
        composable(ScreenRoute.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    if (authViewModel.isUserLoggedIn()) {
                        navController.navigate(ScreenRoute.Dashboard.route) {
                            popUpTo(ScreenRoute.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(ScreenRoute.Onboarding.route) {
                            popUpTo(ScreenRoute.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Onboarding Screen
        composable(ScreenRoute.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(ScreenRoute.Login.route) {
                        popUpTo(ScreenRoute.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(ScreenRoute.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignup = { navController.navigate(ScreenRoute.Signup.route) },
                onNavigateToForgot = { navController.navigate(ScreenRoute.ForgotPassword.route) },
                onAuthSuccess = {
                    navController.navigate(ScreenRoute.BiometricsSetup.route) {
                        popUpTo(ScreenRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Signup Screen
        composable(ScreenRoute.Signup.route) {
            SignupScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(ScreenRoute.Login.route) },
                onAuthSuccess = {
                    navController.navigate(ScreenRoute.Login.route) {
                        popUpTo(ScreenRoute.Signup.route) { inclusive = true }
                    }
                }
            )
        }

        // Forgot Password Screen
        composable(ScreenRoute.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Biometrics Setup Screen
        composable(ScreenRoute.BiometricsSetup.route) {
            BiometricsSetupScreen(
                viewModel = authViewModel,
                onNavigateToDashboard = {
                    navController.navigate(ScreenRoute.Dashboard.route) {
                        popUpTo(ScreenRoute.BiometricsSetup.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Home Dashboard
        composable(ScreenRoute.Dashboard.route) {
            DashboardScreen(
                authViewModel = authViewModel,
                healthViewModel = healthViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Help FAQ Screen
        composable(ScreenRoute.HelpFAQ.route) {
            HelpFAQScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Profile Screen
        composable(ScreenRoute.Profile.route) {
            ProfileScreen(
                viewModel = authViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onLogoutComplete = {
                    navController.navigate(ScreenRoute.Login.route) {
                        popUpTo(ScreenRoute.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        // Symptom Chat Input Screen
        composable(ScreenRoute.SymptomInput.route) {
            com.medibudget.app.ui.screens.SymptomChatScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEstimator = { conditionKey ->
                    navController.navigate(ScreenRoute.CostEstimator.route + "?condition=$conditionKey")
                }
            )
        }

        // Cost Estimator Input Screen
        composable(
            route = ScreenRoute.CostEstimator.route + "?condition={condition}",
            arguments = listOf(navArgument("condition") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val condition = backStackEntry.arguments?.getString("condition")
            CostEstimatorScreen(
                viewModel = healthViewModel,
                initialCondition = condition,
                onNavigateBack = { navController.popBackStack() },
                onCalculateClick = { cond, city, fac ->
                    navController.navigate(ScreenRoute.CostBreakdown.createRoute(cond, city, fac))
                }
            )
        }

        // Cost Breakdown Screen
        composable(
            route = ScreenRoute.CostBreakdown.route,
            arguments = listOf(
                navArgument("condition") { type = NavType.StringType },
                navArgument("city") { type = NavType.StringType },
                navArgument("facility") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cond = backStackEntry.arguments?.getString("condition") ?: "fever"
            val city = backStackEntry.arguments?.getString("city") ?: "Mumbai"
            val fac = backStackEntry.arguments?.getString("facility") ?: "private"
            
            CostBreakdownScreen(
                viewModel = healthViewModel,
                condition = cond,
                city = city,
                facility = fac,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // OCR Scanner Screen
        composable(ScreenRoute.OCRScanner.route) {
            OCRScannerScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() },
                onAlternativeSelected = { id ->
                    navController.navigate(ScreenRoute.MedicineDetail.createRoute(id))
                }
            )
        }

        // Generic Alternatives Search Finder
        composable(ScreenRoute.GenericFinder.route) { backStackEntry ->
            GenericFinderScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() },
                onMedicineSelected = { id ->
                    navController.navigate(ScreenRoute.MedicineDetail.createRoute(id))
                }
            )
        }

        // Medicine Detail Screen
        composable(
            route = ScreenRoute.MedicineDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            MedicineDetailScreen(
                viewModel = healthViewModel,
                id = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Map Finder Hospital Map Screen
        composable(ScreenRoute.MapFinder.route) {
            MapFinderScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Insurance Coverage Calculator Screen
        composable(ScreenRoute.InsuranceCalc.route) {
            InsuranceCalcScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Government Scheme Checker Screen
        composable(ScreenRoute.SchemeChecker.route) {
            SchemeCheckerScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Emergency SOS Mode Screen
        composable(ScreenRoute.SOSMode.route) {
            SOSModeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Admin & Expense Reports Screen
        composable(ScreenRoute.ReportsList.route) {
            ReportsScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoute.AdminDashboard.route) {
            AdminDashboardScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

