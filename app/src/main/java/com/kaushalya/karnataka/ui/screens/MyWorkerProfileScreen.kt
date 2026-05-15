package com.kaushalya.karnataka.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.MyWorkerProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWorkerProfileScreen(
    viewModel: MyWorkerProfileViewModel,
    onBack: () -> Unit,
    onEditProfile: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris -> if (uris.isNotEmpty()) viewModel.addWorkPhotos(uris) }

    val profilePhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.updateProfilePhoto(it) } }

    LaunchedEffect(viewModel.profileSaveSuccess) {
        if (viewModel.profileSaveSuccess) {
            snackbarHostState.showSnackbar("Profile saved successfully ✅")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("My Worker Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditProfile) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green40,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
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
        ) {
            // ── Hero Header ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Green30, Green40, Green50)))
                    .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    // Profile photo
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .shadow(10.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                viewModel.profilePhotoUri != null -> {
                                    AsyncImage(
                                        model = viewModel.profilePhotoUri,
                                        contentDescription = "Profile photo",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                viewModel.profileImageUrl.isNotBlank() -> {
                                    AsyncImage(
                                        model = viewModel.profileImageUrl,
                                        contentDescription = "Profile photo",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else -> {
                                    Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(56.dp))
                                }
                            }
                        }
                        Surface(
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape,
                            color = Color.White,
                            onClick = { profilePhotoLauncher.launch("image/*") }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.CameraAlt, null, tint = Green40, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = viewModel.name.ifBlank { "Worker Name" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = viewModel.category.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(8.dp))
                    // Availability badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (viewModel.isAvailable) Color(0xFF2E9E55) else Color(0xFFB05E00)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (viewModel.isAvailable) "Available" else "Unavailable",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height((-16).dp))
            // ── Action Buttons ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onEditProfile,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green40)
                ) {
                    Icon(Icons.Filled.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit Profile", fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.AddAPhoto, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Add Photos", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Quick Stats ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard("Experience", "${viewModel.experience.ifBlank { "0" }} yrs", Icons.Filled.WorkHistory, Modifier.weight(1f))
                ProfileStatCard("Rate/hr", "₹${viewModel.pricePerHour.ifBlank { "0" }}", Icons.Filled.CurrencyRupee, Modifier.weight(1f))
                ProfileStatCard(
                    "Photos",
                    "${viewModel.workPhotoUris.size + viewModel.workGalleryUrls.size}",
                    Icons.Filled.PhotoLibrary,
                    Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── About ────────────────────────────────────────────────────────────
            ProfileSection(title = "About") {
                Text(
                    text = viewModel.bio.ifBlank { "No description added yet. Tap Edit Profile to add a description." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (viewModel.bio.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Contact & Location ───────────────────────────────────────────────
            ProfileSection(title = "Contact & Location") {
                ProfileInfoRow(Icons.Filled.Phone, "Phone", viewModel.phone.ifBlank { "Not added" })
                Spacer(Modifier.height(8.dp))
                ProfileInfoRow(Icons.Filled.Email, "Email", viewModel.email.ifBlank { "Not added" })
                Spacer(Modifier.height(8.dp))
                ProfileInfoRow(Icons.Filled.LocationOn, "Service Area", viewModel.location.ifBlank { "Not added" })
            }

            Spacer(Modifier.height(12.dp))

            // ── Skills ───────────────────────────────────────────────────────────
            ProfileSection(title = "Skills & Category") {
                // Category chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Green40.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Handyman, null, tint = Green40, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(viewModel.category.displayName, color = Green40, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
                    }
                }
                if (viewModel.skillsText.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    val skills = viewModel.skillsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        skills.take(5).forEach { skill ->
                            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                                Text(
                                    skill,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.height(6.dp))
                    Text("No skills listed yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Work Gallery ─────────────────────────────────────────────────────
            ProfileSection(title = "Work Gallery (${viewModel.workPhotoUris.size + viewModel.workGalleryUrls.size})") {
                val hasRemote = viewModel.workGalleryUrls.isNotEmpty()
                val hasLocal = viewModel.workPhotoUris.isNotEmpty()
                if (!hasRemote && !hasLocal) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.PhotoLibrary, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("No work photos yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { galleryLauncher.launch("image/*") }, shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Upload Work Photos")
                        }
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(viewModel.workGalleryUrls) { url ->
                            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.size(110.dp)) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Work photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        items(viewModel.workPhotoUris) { uri ->
                            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.size(110.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Work photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        item {
                            Card(
                                onClick = { galleryLauncher.launch("image/*") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(110.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Filled.AddAPhoto, null, tint = Green40, modifier = Modifier.size(28.dp))
                                        Spacer(Modifier.height(4.dp))
                                        Text("Add More", style = MaterialTheme.typography.labelSmall, color = Green40)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Ratings Placeholder ───────────────────────────────────────────────
            ProfileSection(title = "Ratings & Reviews") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("4.5", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Green40)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Row {
                            repeat(4) { Icon(Icons.Filled.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp)) }
                            Icon(Icons.Filled.StarHalf, null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                        }
                        Text("Based on 0 reviews (once booking is live)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.RateReview, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("No reviews yet", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("Reviews will appear here after completing bookings.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Green40.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Green40, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ProfileStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Green40, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Green40)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}
