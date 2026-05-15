package com.kaushalya.karnataka.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.kaushalya.karnataka.ui.screens.*
import com.kaushalya.karnataka.ui.theme.Green40
import com.kaushalya.karnataka.viewmodel.AppRole
import com.kaushalya.karnataka.viewmodel.AuthViewModel
import com.kaushalya.karnataka.viewmodel.BookingHistoryViewModel
import com.kaushalya.karnataka.viewmodel.MyWorkerProfileViewModel
import com.kaushalya.karnataka.viewmodel.NotificationsViewModel
import com.kaushalya.karnataka.viewmodel.SettingsViewModel
import com.kaushalya.karnataka.viewmodel.WorkerDashboardViewModel

@Composable
fun AppNavGraph(
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    // Shared MyWorkerProfileViewModel — lives for the whole nav graph lifetime
    val myWorkerProfileViewModel: MyWorkerProfileViewModel = viewModel()

    val isWorkerMode = authViewModel.activeRole == AppRole.WORKER

    val bottomNavScreens = if (isWorkerMode) {
        listOf(
            Screen.WorkerDashboard.route,
            Screen.BookingHistory.route,
            Screen.WorkerEarnings.route,
            Screen.Notifications.route,
            Screen.Settings.route
        )
    } else {
        listOf(
            Screen.Home.route,
            "category/ALL",
            Screen.BookingHistory.route,
            Screen.Notifications.route,
            Screen.Settings.route
        )
    }

    val showBottomBar = bottomNavScreens.any { currentRoute?.startsWith(it.split("/")[0]) == true } &&
            currentRoute != Screen.Splash.route &&
            currentRoute != Screen.Login.route &&
            currentRoute != Screen.Register.route &&
            currentRoute != Screen.RoleSelection.route &&
            currentRoute != Screen.BecomeWorker.route &&
            currentRoute != Screen.MyWorkerProfile.route &&
            currentRoute != Screen.WorkerEditProfile.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavBar(
                    currentRoute = currentRoute,
                    isWorker = isWorkerMode,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                // Splash — handles session persistence
                composable(Screen.Splash.route) {
                    SplashScreen(
                        sessionReady = authViewModel.sessionReady,
                        isLoggedIn = authViewModel.isLoggedIn,
                        onAlreadyLoggedIn = {
                            // User already authenticated — route based on role
                            val dest = when {
                                authViewModel.activeRole == AppRole.WORKER &&
                                authViewModel.hasWorkerProfile -> Screen.WorkerDashboard.route
                                authViewModel.activeRole == AppRole.WORKER -> Screen.BecomeWorker.route
                                else -> Screen.Home.route
                            }
                            navController.navigate(dest) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        },
                        onSplashDone = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Login
                composable(Screen.Login.route) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            navController.navigate(Screen.RoleSelection.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                    )
                }

                // Register
                composable(Screen.Register.route) {
                    RegisterScreen(
                        authViewModel = authViewModel,
                        onRegisterSuccess = {
                            navController.navigate(Screen.RoleSelection.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = { navController.popBackStack() }
                    )
                }

                // Role Selection
                composable(Screen.RoleSelection.route) {
                    RoleSelectionScreen(
                        authViewModel = authViewModel,
                        onSelectCustomer = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onSelectWorker = {
                            if (authViewModel.hasWorkerProfile) {
                                navController.navigate(Screen.WorkerDashboard.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(Screen.BecomeWorker.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }

                // Become a Worker — now passes myWorkerProfileViewModel
                composable(Screen.BecomeWorker.route) {
                    BecomeWorkerScreen(
                        authViewModel = authViewModel,
                        workerProfileViewModel = myWorkerProfileViewModel,
                        onSuccess = {
                            navController.navigate(Screen.WorkerDashboard.route) {
                                popUpTo(Screen.BecomeWorker.route) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                // Home
                composable(Screen.Home.route) {
                    HomeScreen(
                        currentUserName = authViewModel.currentUser?.name.orEmpty(),
                        onWorkerClick = { navController.navigate(Screen.WorkerProfile.createRoute(it)) },
                        onCategoryClick = { navController.navigate(Screen.Category.createRoute(it)) }
                    )
                }

                // Category
                composable(
                    route = Screen.Category.route,
                    arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
                ) { backStack ->
                    val catName = backStack.arguments?.getString("categoryName") ?: "ALL"
                    CategoryScreen(
                        categoryName = catName,
                        onWorkerClick = { navController.navigate(Screen.WorkerProfile.createRoute(it)) },
                        onBack = { navController.popBackStack() }
                    )
                }

                // Worker Profile (customer view of another worker)
                composable(
                    route = Screen.WorkerProfile.route,
                    arguments = listOf(navArgument("workerId") { type = NavType.StringType })
                ) { backStack ->
                    val workerId = backStack.arguments?.getString("workerId") ?: ""
                    WorkerProfileScreen(
                        workerId = workerId,
                        onBookNow = { navController.navigate(Screen.Booking.createRoute(it)) },
                        onBack = { navController.popBackStack() }
                    )
                }

                // Booking
                composable(
                    route = Screen.Booking.route,
                    arguments = listOf(navArgument("workerId") { type = NavType.StringType })
                ) { backStack ->
                    val workerId = backStack.arguments?.getString("workerId") ?: ""
                    BookingScreen(
                        workerId = workerId,
                        onBookingConfirmed = {
                            val homeRoute = if (isWorkerMode) Screen.WorkerDashboard.route else Screen.Home.route
                            navController.navigate(Screen.BookingHistory.route) {
                                popUpTo(homeRoute)
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                // Booking History
                composable(Screen.BookingHistory.route) {
                    val bookingHistVm: BookingHistoryViewModel = viewModel()
                    val uid = authViewModel.currentUser?.uid ?: ""
                    LaunchedEffect(uid) {
                        if (uid.isNotBlank()) {
                            bookingHistVm.loadBookings(uid, isWorkerMode)
                        }
                    }
                    BookingHistoryScreen(viewModel = bookingHistVm)
                }

                // Worker Dashboard
                composable(Screen.WorkerDashboard.route) {
                    val workerDashVm: WorkerDashboardViewModel = viewModel()
                    val dashUid = authViewModel.currentUser?.uid ?: ""
                    LaunchedEffect(dashUid) {
                        if (dashUid.isNotBlank()) workerDashVm.loadDashboard(dashUid)
                    }
                    WorkerDashboardScreen(
                        viewModel = workerDashVm,
                        hasWorkerProfile = authViewModel.hasWorkerProfile,
                        onBecomeWorker = { navController.navigate(Screen.BecomeWorker.route) },
                        onViewMyProfile = { navController.navigate(Screen.MyWorkerProfile.route) },
                        onViewEarnings = { navController.navigate(Screen.WorkerEarnings.route) }
                    )
                }

                // My Worker Profile (worker's own profile view)
                composable(Screen.MyWorkerProfile.route) {
                    val profileUid = authViewModel.currentUser?.uid ?: ""
                    LaunchedEffect(profileUid) {
                        if (profileUid.isNotBlank()) {
                            myWorkerProfileViewModel.observeProfile(profileUid)
                        }
                    }
                    MyWorkerProfileScreen(
                        viewModel = myWorkerProfileViewModel,
                        onBack = { navController.popBackStack() },
                        onEditProfile = {
                            navController.navigate(Screen.WorkerEditProfile.route)
                        }
                    )
                }

                // Worker Edit Profile
                composable(Screen.WorkerEditProfile.route) {
                    WorkerEditProfileScreen(
                        viewModel = myWorkerProfileViewModel,
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() },
                        uid = authViewModel.currentUser?.uid ?: ""
                    )
                }

                // Notifications
                composable(Screen.Notifications.route) {
                    val notifVm: NotificationsViewModel = viewModel()
                    val notifUid = authViewModel.currentUser?.uid ?: ""
                    LaunchedEffect(notifUid, isWorkerMode) {
                        if (notifUid.isNotBlank()) notifVm.loadNotifications(notifUid, isWorkerMode)
                    }
                    NotificationsScreen(viewModel = notifVm)
                }

                // Worker Earnings
                composable(Screen.WorkerEarnings.route) {
                    val workerDashVm: WorkerDashboardViewModel = viewModel()
                    WorkerEarningsScreen(
                        viewModel = workerDashVm,
                        onBack = { navController.popBackStack() }
                    )
                }

                // Settings
                composable(Screen.Settings.route) {
                    val cu = authViewModel.currentUser
                    SettingsScreen(
                        profileName = cu?.name.orEmpty(),
                        profileEmail = cu?.email.orEmpty(),
                        profilePhone = cu?.phone.orEmpty(),
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                        onChangePassword = { navController.navigate(Screen.ChangePassword.route) },
                        onNotificationPrefs = { navController.navigate(Screen.NotificationPreferences.route) },
                        onHelpFaq = { navController.navigate(Screen.HelpFaq.route) },
                        onSwitchRole = {
                            val newRole = if (authViewModel.activeRole == AppRole.WORKER) AppRole.CUSTOMER else AppRole.WORKER
                            authViewModel.switchRole()
                            val dest = if (newRole == AppRole.WORKER) {
                                if (authViewModel.hasWorkerProfile) Screen.WorkerDashboard.route
                                else Screen.BecomeWorker.route
                            } else Screen.Home.route
                            navController.navigate(dest) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        currentRole = authViewModel.activeRole,
                        viewModel = settingsViewModel
                    )
                }

                // Edit Profile (customer)
                composable(Screen.EditProfile.route) {
                    EditProfileScreen(onBack = { navController.popBackStack() })
                }

                // Change Password
                composable(Screen.ChangePassword.route) {
                    ChangePasswordScreen(onBack = { navController.popBackStack() })
                }

                // Notification Preferences
                composable(Screen.NotificationPreferences.route) {
                    NotificationPreferencesScreen(onBack = { navController.popBackStack() })
                }

                // Help & FAQ
                composable(Screen.HelpFaq.route) {
                    HelpFaqScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────

data class BottomNavEntry(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavEntry(Screen.Home.route, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavEntry("category/ALL", "Services", Icons.Filled.GridView, Icons.Outlined.GridView),
    BottomNavEntry(Screen.BookingHistory.route, "Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomNavEntry(Screen.Notifications.route, "Alerts", Icons.Filled.Notifications, Icons.Outlined.Notifications),
    BottomNavEntry(Screen.Settings.route, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)

private val workerBottomNavItems = listOf(
    BottomNavEntry(Screen.WorkerDashboard.route, "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    BottomNavEntry(Screen.BookingHistory.route, "Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomNavEntry(Screen.WorkerEarnings.route, "Earnings", Icons.Filled.CurrencyRupee, Icons.Outlined.CurrencyRupee),
    BottomNavEntry(Screen.Notifications.route, "Alerts", Icons.Filled.Notifications, Icons.Outlined.Notifications),
    BottomNavEntry(Screen.Settings.route, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)

@Composable
fun AppBottomNavBar(
    currentRoute: String?,
    isWorker: Boolean,
    onNavigate: (String) -> Unit
) {
    val items = if (isWorker) workerBottomNavItems else bottomNavItems

    NavigationBar(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            .shadow(elevation = 20.dp, shape = RoundedCornerShape(32.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute?.startsWith(item.route.split("/")[0]) == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    if (selected) {
                        Text(item.label, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
