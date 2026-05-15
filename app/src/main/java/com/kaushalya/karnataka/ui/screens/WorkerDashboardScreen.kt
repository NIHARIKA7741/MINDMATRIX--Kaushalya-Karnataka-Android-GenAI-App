package com.kaushalya.karnataka.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kaushalya.karnataka.ui.components.*
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.WorkerDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDashboardScreen(
    viewModel: WorkerDashboardViewModel = viewModel(),
    hasWorkerProfile: Boolean = true,
    onBecomeWorker: () -> Unit = {},
    onViewMyProfile: () -> Unit = {},
    onViewEarnings: () -> Unit = {}
) {
    // ── No profile → become-a-worker prompt ──────────────────────────────────
    if (!hasWorkerProfile) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp).verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(40.dp))
                Box(
                    modifier = Modifier.size(120.dp).clip(CircleShape).background(Brush.verticalGradient(listOf(Green30, Green40))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Handyman, null, tint = Color.White, modifier = Modifier.size(60.dp))
                }
                Spacer(Modifier.height(24.dp))
                Text("Start Earning Today!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text("Create your worker profile to receive booking requests.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = onBecomeWorker,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green40)
                ) {
                    Icon(Icons.Filled.Handyman, null, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Become a Worker", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(20.dp))
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        listOf("Set your own rates & schedule", "Get bookings from nearby customers", "Build your professional portfolio").forEach { text ->
                            Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, null, tint = Green40, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(10.dp))
                                Text(text, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
        return
    }

    // ── Worker dashboard (profile exists) ─────────────────────────────────────
    val worker = viewModel.currentWorker
    var showEditProfile by remember { mutableStateOf(false) }
    var showUpdateLocation by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            viewModel.pendingProfilePhotoUri = uri
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { }

    LaunchedEffect(viewModel.profileSaved) {
        if (viewModel.profileSaved) {
            snackbarHostState.showSnackbar("Profile updated ✅")
            selectedPhotoUri = null
        }
    }

    LaunchedEffect(viewModel.profileSaveError) {
        val err = viewModel.profileSaveError ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(err)
        viewModel.clearProfileSaveError()
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Green30, Green40, Green50)))
                        .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                AsyncImage(
                                    model = selectedPhotoUri ?: worker.profileImageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(72.dp).shadow(8.dp, CircleShape).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(modifier = Modifier.align(Alignment.BottomEnd).size(22.dp), shape = CircleShape, color = Green40, onClick = { showPhotoOptions = true }) {
                                    Icon(Icons.Filled.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Welcome, ${viewModel.editName.split(" ").first()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(worker.category.displayName, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                                RatingBar(rating = worker.rating, starSize = 14.dp)
                            }
                            IconButton(onClick = { showEditProfile = true }, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                                Icon(Icons.Filled.Edit, null, tint = Color.White)
                            }
                        }
                    }
                }
            }

            // ── My Worker Profile card ───────────────────────────────────────────
            item {
                Card(
                    onClick = onViewMyProfile,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Green40.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Green40.copy(alpha = 0.3f))
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Green40.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.AccountCircle, null, tint = Green40, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("My Worker Profile", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Green40)
                            Text("View & manage your public profile", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Filled.ChevronRight, null, tint = Green40)
                    }
                }
            }

            // Stats
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Total Earnings", "₹${viewModel.totalEarnings / 1000}K", Icons.Filled.CurrencyRupee, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    StatCard("Jobs Done", "${viewModel.totalJobs}", Icons.Filled.CheckCircle, Color(0xFF1A6B5A), Modifier.weight(1f))
                    StatCard("Rating", "${worker.rating}", Icons.Filled.Star, Color(0xFFB05E00), Modifier.weight(1f))
                }
            }

            // Quick Actions
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    SectionHeader(title = "Quick Actions")
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickActionCard(icon = Icons.Filled.AddAPhoto, label = "Upload Photos", color = Color(0xFF1565C0), onClick = { showPhotoOptions = true }, modifier = Modifier.weight(1f))
                        QuickActionCard(icon = Icons.Filled.ManageAccounts, label = "Edit Profile", color = Green40, onClick = { showEditProfile = true }, modifier = Modifier.weight(1f))
                        QuickActionCard(icon = Icons.Filled.ShareLocation, label = "Update Area", color = Color(0xFF6A1B9A), onClick = { showUpdateLocation = true }, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Photo preview
            if (selectedPhotoUri != null) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                        SectionHeader(title = "📸 Uploaded Photo Preview")
                        Spacer(Modifier.height(10.dp))
                        Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            AsyncImage(model = selectedPhotoUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                    }
                }
            }

            // Booking Requests
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    SectionHeader(title = "🔔 Booking Requests (${viewModel.bookingRequests.size})")
                    Spacer(Modifier.height(12.dp))
                }
            }

            if (viewModel.bookingRequests.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No pending booking requests", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                items(viewModel.bookingRequests) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(booking.customerName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("${booking.date} at ${booking.time}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(booking.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                BookingStatusBadge(status = booking.status)
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.rejectBooking(booking.id) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                                ) { Text("Decline") }
                                Button(
                                    onClick = { viewModel.acceptBooking(booking.id) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) { Text("Accept") }
                            }
                        }
                    }
                }
            }

            // Earnings Dashboard Navigation
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    SectionHeader(title = "💰 Earnings & Payouts")
                    Spacer(Modifier.height(12.dp))
                    Card(
                        onClick = onViewEarnings,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Green40.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Green40),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.AccountBalanceWallet, null, tint = Color.White, modifier = Modifier.size(30.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Total Earnings", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${viewModel.totalEarnings}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Green40)
                                Text("View detailed dashboard", style = MaterialTheme.typography.bodySmall, color = Green40.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Filled.ArrowForward, null, tint = Green40)
                        }
                    }
                }
            }
        }
    }

    // Photo options sheet
    if (showPhotoOptions) {
        ModalBottomSheet(onDismissRequest = { showPhotoOptions = false }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Upload Work Photo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(onClick = { showPhotoOptions = false; galleryLauncher.launch("image/*") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0).copy(alpha = 0.1f))) {
                        Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.PhotoLibrary, null, tint = Color(0xFF1565C0), modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(10.dp))
                            Text("Gallery", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                        }
                    }
                    Card(onClick = { showPhotoOptions = false; cameraLauncher.launch(null) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Green40.copy(alpha = 0.1f))) {
                        Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CameraAlt, null, tint = Green40, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(10.dp))
                            Text("Camera", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Green40)
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    // Edit Profile sheet
    if (showEditProfile) {
        ModalBottomSheet(onDismissRequest = { showEditProfile = false }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState())) {
                Text("Edit Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Changes sync to your Firestore profile", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(20.dp))
                DashboardField("Full Name", viewModel.editName, Icons.Filled.Person) { viewModel.editName = it }
                Spacer(Modifier.height(12.dp))
                DashboardField("Phone Number", viewModel.editPhone, Icons.Filled.Phone) { viewModel.editPhone = it }
                Spacer(Modifier.height(12.dp))
                DashboardField("Experience (years)", viewModel.editExperience, Icons.Filled.WorkHistory) { viewModel.editExperience = it }
                Spacer(Modifier.height(12.dp))
                DashboardField("Price per Hour (₹)", viewModel.editPricePerHour, Icons.Filled.CurrencyRupee) { viewModel.editPricePerHour = it }
                Spacer(Modifier.height(12.dp))
                DashboardField("Location", viewModel.editLocation, Icons.Filled.LocationOn) { viewModel.editLocation = it }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.editBio, onValueChange = { viewModel.editBio = it }, label = { Text("About / Description") }, leadingIcon = { Icon(Icons.Filled.Description, null, tint = Green40) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), minLines = 3, maxLines = 5)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Available for bookings", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Switch(checked = viewModel.editAvailable, onCheckedChange = { viewModel.editAvailable = it })
                }
                Spacer(Modifier.height(20.dp))
                Button(onClick = { viewModel.saveProfile(); showEditProfile = false }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Green40)) {
                    Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Update Location sheet
    if (showUpdateLocation) {
        ModalBottomSheet(onDismissRequest = { showUpdateLocation = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text("Update Service Area", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(value = viewModel.editLocation, onValueChange = { viewModel.editLocation = it }, label = { Text("Your Service Area") }, placeholder = { Text("e.g. Indiranagar, Bangalore") }, leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = Green40) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), singleLine = true)
                Spacer(Modifier.height(20.dp))
                Button(onClick = { viewModel.saveProfile(); showUpdateLocation = false }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))) {
                    Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Save Location", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DashboardField(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onValueChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, leadingIcon = { Icon(icon, null, tint = Green40) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), singleLine = true)
}

@Composable
private fun QuickActionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}
