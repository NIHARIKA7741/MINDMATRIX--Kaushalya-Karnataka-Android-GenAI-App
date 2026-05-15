package com.kaushalya.karnataka.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kaushalya.karnataka.data.model.WorkerCategory
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.AppRole
import com.kaushalya.karnataka.viewmodel.AuthViewModel
import com.kaushalya.karnataka.viewmodel.MyWorkerProfileViewModel

// ─── Role Selection Screen ────────────────────────────────────────────────────

@Composable
fun RoleSelectionScreen(
    authViewModel: AuthViewModel,
    onSelectCustomer: () -> Unit,
    onSelectWorker: () -> Unit
) {
    val userName = authViewModel.currentUser?.name?.split(" ")?.first() ?: "there"
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authViewModel.errorMessage) {
        authViewModel.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Green40, Green50, MaterialTheme.colorScheme.background)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome, $userName! 👋", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text("How would you like to continue?", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            Spacer(Modifier.height(48.dp))

            RoleCard(
                icon = Icons.Filled.PersonSearch,
                title = "Continue as Customer",
                subtitle = "Find and book skilled workers near you",
                accentColor = Color.White,
                containerColor = Color.White.copy(alpha = 0.18f),
                onClick = { authViewModel.selectRole(AppRole.CUSTOMER) { onSelectCustomer() } }
            )
            Spacer(Modifier.height(16.dp))
            RoleCard(
                icon = Icons.Filled.Handyman,
                title = "Continue as Worker",
                subtitle = "Manage bookings, earnings and your profile",
                accentColor = Green40,
                containerColor = Color.White,
                onClick = { authViewModel.selectRole(AppRole.WORKER) { onSelectWorker() } }
            )
            Spacer(Modifier.height(40.dp))
            Text("You can switch roles anytime from Settings", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.65f), textAlign = TextAlign.Center)
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )

        // Loading Overlay
        if (authViewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
private fun RoleCard(
    icon: ImageVector, title: String, subtitle: String,
    accentColor: Color, containerColor: Color, onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (containerColor == Color.White) Green40 else Color.White)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = if (containerColor == Color.White) MaterialTheme.colorScheme.onSurfaceVariant else Color.White.copy(alpha = 0.8f))
            }
            Icon(Icons.Filled.ChevronRight, null, tint = if (containerColor == Color.White) Green40 else Color.White.copy(alpha = 0.7f))
        }
    }
}

// ─── Become a Worker Screen ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeWorkerScreen(
    authViewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    workerProfileViewModel: MyWorkerProfileViewModel? = null
) {
    var name by remember { mutableStateOf(authViewModel.currentUser?.name ?: "") }
    var phone by remember { mutableStateOf(authViewModel.currentUser?.phone ?: "") }
    var email by remember { mutableStateOf(authViewModel.currentUser?.email ?: "") }
    var selectedCategory by remember { mutableStateOf(WorkerCategory.ELECTRICIAN) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var skillsText by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var pricePerHour by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var aboutText by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    var workPhotoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }

    val profilePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { profilePhotoUri = it } }
    val workPhotosLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris -> if (uris.isNotEmpty()) workPhotoUris = uris }

    val isFormValid = name.isNotBlank() && phone.isNotBlank() && experience.isNotBlank() && pricePerHour.isNotBlank() && location.isNotBlank()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Become a Worker", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green40, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Intro card
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Handyman, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Set up your Worker Profile", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Fill in your details to start receiving booking requests from customers.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Profile photo
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer).border(2.dp, Green40, CircleShape), contentAlignment = Alignment.Center) {
                        if (profilePhotoUri != null) {
                            AsyncImage(model = profilePhotoUri, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        } else {
                            Icon(Icons.Filled.Person, null, tint = Green40, modifier = Modifier.size(40.dp))
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Profile Photo", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(if (profilePhotoUri != null) "Photo selected ✓" else "Add a professional photo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(onClick = { profilePhotoLauncher.launch("image/*") }, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Filled.Upload, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Upload")
                    }
                }
            }

            Text("Personal & Professional Details", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            BwField("Full Name", name, Icons.Filled.Person, KeyboardType.Text) { name = it }
            BwField("Phone Number", phone, Icons.Filled.Phone, KeyboardType.Phone) { phone = it }
            BwField("Email Address", email, Icons.Filled.Email, KeyboardType.Email) { email = it }

            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                OutlinedTextField(
                    value = selectedCategory.displayName, onValueChange = {}, readOnly = true,
                    label = { Text("Skill Category") },
                    leadingIcon = { Icon(Icons.Filled.Handyman, null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    WorkerCategory.entries.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat.displayName) }, onClick = { selectedCategory = cat; categoryExpanded = false })
                    }
                }
            }

            OutlinedTextField(
                value = skillsText, onValueChange = { skillsText = it }, label = { Text("Skills") },
                placeholder = { Text("e.g. Wiring, MCB Repair, Solar Setup") },
                leadingIcon = { Icon(Icons.Filled.Build, null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                singleLine = true, supportingText = { Text("Separate with commas") }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BwField("Experience (yrs)", experience, Icons.Filled.WorkHistory, KeyboardType.Number, Modifier.weight(1f)) { experience = it }
                BwField("Rate/hr (₹)", pricePerHour, Icons.Filled.CurrencyRupee, KeyboardType.Number, Modifier.weight(1f)) { pricePerHour = it }
            }

            BwField("Service Location", location, Icons.Filled.LocationOn, KeyboardType.Text, placeholder = "e.g. Indiranagar, Bangalore") { location = it }

            OutlinedTextField(
                value = aboutText, onValueChange = { aboutText = it }, label = { Text("About / Description") },
                placeholder = { Text("Describe your experience and services...") },
                leadingIcon = { Icon(Icons.Filled.Description, null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), minLines = 3, maxLines = 5
            )

            // Availability
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, null, tint = if (isAvailable) Green40 else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Available for Bookings", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text(if (isAvailable) "Customers can book you right away" else "You will appear as unavailable", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
                }
            }

            // Work gallery
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Work Gallery", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(if (workPhotoUris.isEmpty()) "Upload photos of your past work" else "${workPhotoUris.size} photo(s) selected ✓", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedButton(onClick = { workPhotosLauncher.launch("image/*") }, shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Filled.PhotoLibrary, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Select")
                        }
                    }
                    if (workPhotoUris.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            workPhotoUris.take(4).forEach { uri ->
                                AsyncImage(model = uri, contentDescription = null, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(10.dp)), contentScale = ContentScale.Crop)
                            }
                            if (workPhotoUris.size > 4) {
                                Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                                    Text("+${workPhotoUris.size - 4}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Submit
            Button(
                onClick = {
                    val uid = authViewModel.currentUser?.uid ?: ""
                    workerProfileViewModel?.saveFromRegistration(
                        uid, name, phone, email, selectedCategory, skillsText,
                        experience, pricePerHour, location, aboutText,
                        isAvailable, profilePhotoUri, workPhotoUris
                    )
                    authViewModel.completeWorkerRegistration()
                    onSuccess()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(containerColor = Green40)
            ) {
                Icon(Icons.Filled.Handyman, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Create Worker Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BwField(
    label: String, value: String, icon: ImageVector,
    keyboardType: KeyboardType, modifier: Modifier = Modifier.fillMaxWidth(),
    placeholder: String = "", onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        modifier = modifier, shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType), singleLine = true
    )
}
