package com.kaushalya.karnataka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaushalya.karnataka.ui.theme.Green40
import com.kaushalya.karnataka.ui.theme.Green50

// ─── Shared scaffold for all placeholder screens ──────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green40,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            content = content
        )
    }
}

@Composable
private fun ComingSoonBanner(icon: ImageVector, title: String, description: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(listOf(Green40, Green50))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PlaceholderField(label: String, placeholder: String, icon: ImageVector) {
    var value by remember { mutableStateOf("") }
    OutlinedTextField(
        value = value,
        onValueChange = { value = it },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = Green40)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )
}

// ─── 1. Edit Profile Screen ───────────────────────────────────────────────────

@Composable
fun EditProfileScreen(onBack: () -> Unit, onSave: () -> Unit = onBack) {
    PlaceholderScaffold(title = "Edit Profile", onBack = onBack) {
        // Avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = Green40
                    )
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp),
                    shape = CircleShape,
                    color = Green40
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }

        PlaceholderField("Full Name", "Your name", Icons.Filled.Person)
        Spacer(Modifier.height(14.dp))
        PlaceholderField("Email Address", "you@example.com", Icons.Filled.Email)
        Spacer(Modifier.height(14.dp))
        PlaceholderField("Phone Number", "+91 …", Icons.Filled.Phone)
        Spacer(Modifier.height(14.dp))
        PlaceholderField("City / Area", "City, Karnataka", Icons.Filled.LocationOn)
        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green40)
        ) {
            Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Changes", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
        ComingSoonBanner(
            icon = Icons.Filled.Person,
            title = "Profile Sync",
            description = "Your profile will be synced to our servers once backend is connected."
        )
    }
}

// ─── 2. Change Password Screen ────────────────────────────────────────────────

@Composable
fun ChangePasswordScreen(onBack: () -> Unit, onSave: () -> Unit = onBack) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    PlaceholderScaffold(title = "Change Password", onBack = onBack) {
        ComingSoonBanner(
            icon = Icons.Filled.Lock,
            title = "Secure Password Change",
            description = "Password updates will be securely handled through authenticated API calls."
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, null, tint = Green40) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            leadingIcon = { Icon(Icons.Filled.LockOpen, null, tint = Green40) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm New Password") },
            leadingIcon = { Icon(Icons.Filled.LockReset, null, tint = Green40) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green40)
        ) {
            Icon(Icons.Filled.Security, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Update Password", fontWeight = FontWeight.Bold)
        }
    }
}

// ─── 3. Notification Preferences Screen ──────────────────────────────────────

@Composable
fun NotificationPreferencesScreen(onBack: () -> Unit, onSave: () -> Unit = onBack) {
    var bookingAlerts by remember { mutableStateOf(true) }
    var promotionalAlerts by remember { mutableStateOf(false) }
    var paymentAlerts by remember { mutableStateOf(true) }
    var reminderAlerts by remember { mutableStateOf(true) }
    var smsAlerts by remember { mutableStateOf(false) }

    PlaceholderScaffold(title = "Notification Preferences", onBack = onBack) {
        ComingSoonBanner(
            icon = Icons.Filled.Notifications,
            title = "Notification Settings",
            description = "These preferences will be saved to your account profile via backend API."
        )
        Spacer(Modifier.height(28.dp))

        Text(
            "Alert Types",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Green40,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                NotifToggleRow(
                    icon = Icons.Filled.CalendarMonth,
                    title = "Booking Alerts",
                    subtitle = "Confirmations, cancellations and updates",
                    checked = bookingAlerts,
                    onToggle = { bookingAlerts = it }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                NotifToggleRow(
                    icon = Icons.Filled.LocalOffer,
                    title = "Promotions & Offers",
                    subtitle = "Discounts and special deals",
                    checked = promotionalAlerts,
                    onToggle = { promotionalAlerts = it }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                NotifToggleRow(
                    icon = Icons.Filled.Payment,
                    title = "Payment Alerts",
                    subtitle = "Transaction confirmations",
                    checked = paymentAlerts,
                    onToggle = { paymentAlerts = it }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                NotifToggleRow(
                    icon = Icons.Filled.Alarm,
                    title = "Service Reminders",
                    subtitle = "Upcoming appointment reminders",
                    checked = reminderAlerts,
                    onToggle = { reminderAlerts = it }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                NotifToggleRow(
                    icon = Icons.Filled.Sms,
                    title = "SMS Notifications",
                    subtitle = "Receive alerts via SMS",
                    checked = smsAlerts,
                    onToggle = { smsAlerts = it }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green40)
        ) {
            Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Preferences", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NotifToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Green40, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

// ─── 4. Help & FAQ Screen ─────────────────────────────────────────────────────

@Composable
fun HelpFaqScreen(onBack: () -> Unit) {
    val faqs = listOf(
        "How do I book a worker?" to "Go to Home → Browse workers or a category → Tap a worker card → Press 'Book Service'. Fill in your preferred date, time, and address.",
        "How do I cancel a booking?" to "Go to Bookings tab → Tap the booking you want to cancel → Press 'Cancel Booking'. Cancellations are free up to 2 hours before the appointment.",
        "How do I pay for a service?" to "Payment functionality will be available in the upcoming version. Currently, the app logs bookings for demo purposes.",
        "How do I contact a worker?" to "Open any Worker Profile screen and use the 'Call' or 'WhatsApp' buttons to reach them directly.",
        "Are workers verified?" to "Yes — all workers listed on Kaushalya Karnataka are manually verified by our team before being listed on the platform.",
        "How are ratings calculated?" to "Ratings are calculated from verified customer reviews submitted after each completed booking.",
        "Can I track my service in real time?" to "Live tracking will be available soon as part of our platform expansion.",
        "What if the worker doesn't show up?" to "Please contact support at support@kaushalya.in. We ensure a full refund or instant rescheduling."
    )
    var expandedIndex by remember { mutableStateOf(-1) }

    PlaceholderScaffold(title = "Help & FAQ", onBack = onBack) {
        ComingSoonBanner(
            icon = Icons.Filled.HelpOutline,
            title = "Help Center",
            description = "Live chat support and ticket submission will be available after backend integration."
        )

        Spacer(Modifier.height(28.dp))

        Text(
            "Frequently Asked Questions",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Green40,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        faqs.forEachIndexed { index, (question, answer) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                onClick = { expandedIndex = if (expandedIndex == index) -1 else index }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.QuestionAnswer,
                            contentDescription = null,
                            tint = Green40,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            question,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            if (expandedIndex == index) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (expandedIndex == index) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(10.dp))
                        Text(
                            answer,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Email, null, tint = Green40, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Still need help?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "Email us at support@kaushalya.in",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
