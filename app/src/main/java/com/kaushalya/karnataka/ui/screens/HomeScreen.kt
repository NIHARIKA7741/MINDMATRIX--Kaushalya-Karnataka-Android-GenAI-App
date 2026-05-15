package com.kaushalya.karnataka.ui.screens

import androidx.compose.animation.core.*
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
import com.kaushalya.karnataka.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    currentUserName: String = "",
    onWorkerClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    var bannerIndex by remember { mutableIntStateOf(0) }

    // Auto-scroll banners
    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            bannerIndex = (bannerIndex + 1) % homeViewModel.promoBanners.size
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ─── Header ──────────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Green40, Green50, Green95))
                    )
                    .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val firstName = currentUserName.trim().split(" ").firstOrNull().orEmpty()
                            val helloTarget = firstName.ifBlank { "there" }
                            Text(
                                text = "Hello, $helloTarget 👋",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Find skilled workers near you",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = homeViewModel.searchQuery,
                        onValueChange = homeViewModel::onSearchQueryChange,
                        placeholder = { Text("Search workers, skills...") },
                        leadingIcon = { Icon(Icons.Filled.Search, null) },
                        trailingIcon = {
                            if (homeViewModel.searchQuery.isNotBlank()) {
                                IconButton(onClick = homeViewModel::clearSearch) {
                                    Icon(Icons.Filled.Close, null)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        }

        // ─── Search Results ───────────────────────────────────────────────────
        if (homeViewModel.isSearchActive) {
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Search Results (${homeViewModel.searchResults.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            items(
                items = homeViewModel.searchResults,
                key = { it.id }
            ) { worker ->
                WorkerListItem(
                    worker = worker,
                    onClick = { onWorkerClick(worker.id) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        } else {
            // ─── Promo Banners ────────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                    val banner = homeViewModel.promoBanners[bannerIndex]
                    PromoBannerCard(banner = banner)
                    Spacer(Modifier.height(8.dp))
                    // Page dots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        homeViewModel.promoBanners.forEachIndexed { i, _ ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (i == bannerIndex) 20.dp else 8.dp, 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i == bannerIndex) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline
                                    )
                            )
                        }
                    }
                }
            }

            // ─── Categories ───────────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    SectionHeader(title = "Categories", onSeeAll = { onCategoryClick("ALL") })
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(WorkerCategory.entries) { category ->
                            CategoryChip(
                                category = category,
                                onClick = { onCategoryClick(category.name) }
                            )
                        }
                    }
                }
            }

            // ─── Featured Workers ─────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader(title = "⭐ Featured Workers", onSeeAll = { onCategoryClick("ALL") })
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(end = 4.dp)
                    ) {
                        items(
                            items = homeViewModel.featuredWorkers,
                            key = { it.id }
                        ) { worker ->
                            WorkerCard(worker = worker, onClick = { onWorkerClick(worker.id) })
                        }
                    }
                }
            }

            // ─── Nearby Workers ───────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    SectionHeader(
                        title = "📍 Nearby Workers",
                        onSeeAll = { onCategoryClick("ALL") }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
            items(
                items = homeViewModel.nearbyWorkers,
                key = { it.id }
            ) { worker ->
                WorkerListItem(
                    worker = worker,
                    onClick = { onWorkerClick(worker.id) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }
    }
}
