package com.kaushalya.karnataka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kaushalya.karnataka.data.model.WorkerCategory
import com.kaushalya.karnataka.ui.theme.Green40
import com.kaushalya.karnataka.viewmodel.MyWorkerProfileViewModel
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerEditProfileScreen(
    viewModel: MyWorkerProfileViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    uid: String = ""
) {
    var categoryExpanded by remember { mutableStateOf(false) }

    // Initialise edit fields when screen loads
    LaunchedEffect(Unit) { viewModel.startEdit() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.cancelEdit(); onBack() }) {
                        Icon(Icons.Filled.ArrowBack, null)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Personal Details", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            EditField("Full Name", viewModel.editName, Icons.Filled.Person) { viewModel.editName = it }
            EditField("Phone Number", viewModel.editPhone, Icons.Filled.Phone, KeyboardType.Phone) { viewModel.editPhone = it }

            // Category dropdown
            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                OutlinedTextField(
                    value = viewModel.editCategory.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Skill Category") },
                    leadingIcon = { Icon(Icons.Filled.Handyman, null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    WorkerCategory.entries.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat.displayName) }, onClick = {
                            viewModel.editCategory = cat; categoryExpanded = false
                        })
                    }
                }
            }

            OutlinedTextField(
                value = viewModel.editSkillsText,
                onValueChange = { viewModel.editSkillsText = it },
                label = { Text("Skills") },
                placeholder = { Text("e.g. Wiring, MCB Repair") },
                leadingIcon = { Icon(Icons.Filled.Build, null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                supportingText = { Text("Separate with commas") }
            )

            Text("Professional Details", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EditField("Experience (yrs)", viewModel.editExperience, Icons.Filled.WorkHistory, KeyboardType.Number, Modifier.weight(1f)) { viewModel.editExperience = it }
                EditField("Rate/hr (₹)", viewModel.editPricePerHour, Icons.Filled.CurrencyRupee, KeyboardType.Number, Modifier.weight(1f)) { viewModel.editPricePerHour = it }
            }

            EditField("Service Location", viewModel.editLocation, Icons.Filled.LocationOn) { viewModel.editLocation = it }

            OutlinedTextField(
                value = viewModel.editBio,
                onValueChange = { viewModel.editBio = it },
                label = { Text("About / Description") },
                leadingIcon = { Icon(Icons.Filled.Description, null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                minLines = 3,
                maxLines = 5
            )

            // Availability
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, null,
                            tint = if (viewModel.editIsAvailable) Green40 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Available for Bookings", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text(
                                if (viewModel.editIsAvailable) "Customers can book you" else "You appear unavailable",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(checked = viewModel.editIsAvailable, onCheckedChange = { viewModel.editIsAvailable = it })
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.saveEdits(uid); onSaved() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green40)
            ) {
                Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EditField(
    label: String,
    value: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}
