package com.kaushalya.karnataka.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Category : Screen("category/{categoryName}") {
        fun createRoute(categoryName: String) = "category/$categoryName"
    }
    object WorkerProfile : Screen("worker_profile/{workerId}") {
        fun createRoute(workerId: String) = "worker_profile/$workerId"
    }
    object Booking : Screen("booking/{workerId}") {
        fun createRoute(workerId: String) = "booking/$workerId"
    }
    object BookingHistory : Screen("booking_history")
    object WorkerDashboard : Screen("worker_dashboard")
    object WorkerEarnings : Screen("worker_earnings")
    object WorkerEditProfile : Screen("worker_edit_profile")
    object MyWorkerProfile : Screen("my_worker_profile")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
    object EditProfile : Screen("edit_profile")
    object ChangePassword : Screen("change_password")
    object NotificationPreferences : Screen("notification_preferences")
    object HelpFaq : Screen("help_faq")
    object RoleSelection : Screen("role_selection")
    object BecomeWorker : Screen("become_worker")
}

// Bottom Navigation Items for Customer
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val iconName: String
) {
    object Home : BottomNavItem("home", "Home", "home")
    object Categories : BottomNavItem("category/ALL", "Categories", "grid_view")
    object BookingHistory : BottomNavItem("booking_history", "Bookings", "calendar_month")
    object Notifications : BottomNavItem("notifications", "Alerts", "notifications")
    object Settings : BottomNavItem("settings", "Settings", "settings")
}

// Bottom Navigation Items for Worker
sealed class WorkerBottomNavItem(
    val route: String,
    val label: String,
    val iconName: String
) {
    object Dashboard : WorkerBottomNavItem("worker_dashboard", "Dashboard", "dashboard")
    object Bookings : WorkerBottomNavItem("booking_history", "Bookings", "calendar_month")
    object Notifications : WorkerBottomNavItem("notifications", "Alerts", "notifications")
    object Settings : WorkerBottomNavItem("settings", "Settings", "settings")
}
