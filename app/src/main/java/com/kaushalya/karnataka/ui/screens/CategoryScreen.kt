package com.kaushalya.karnataka.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaushalya.karnataka.data.model.WorkerCategory
import com.kaushalya.karnataka.ui.components.*
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.CategoryViewModel

@Composable
fun CategoryScreen(
    categoryName: String,
    onWorkerClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CategoryViewModel = viewModel()
) {
    LaunchedEffect(categoryName) { viewModel.loadCategory(categoryName) }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Available", "Top Rated", "Price: Low")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ─── Header ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Green40, Green50)))
                .padding(top = 48.dp, start = 8.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Column {
                        Text(
                            text = if (categoryName == "ALL") "All Workers" else categoryName.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${viewModel.workers.size} workers available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Category chips (only when ALL)
                if (categoryName == "ALL") {
                    Spacer(Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        items(WorkerCategory.entries) { cat ->
                            val selected = viewModel.selectedCategory == cat
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.loadCategory(cat.name) },
                                label = { Text(cat.displayName) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getCategoryIcon(cat),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White,
                                    selectedLabelColor = Green40
                                )
                            )
                        }
                    }
                }
            }
        }

        // ─── Filter Row ───────────────────────────────────────────────────────
        LazyRow(
            modifier = Modifier.padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }

        // ─── Worker List ──────────────────────────────────────────────────────
        val displayedWorkers = when (selectedFilter) {
            "Available" -> viewModel.workers.filter { it.isAvailable }
            "Top Rated" -> viewModel.workers.sortedByDescending { it.rating }
            "Price: Low" -> viewModel.workers.sortedBy { it.pricePerHour }
            else -> viewModel.workers
        }

        if (displayedWorkers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.SearchOff,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No workers found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedWorkers, key = { it.id }) { worker ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        WorkerListItem(worker = worker, onClick = { onWorkerClick(worker.id) })
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
