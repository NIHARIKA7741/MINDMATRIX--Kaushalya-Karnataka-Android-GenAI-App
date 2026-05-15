package com.kaushalya.karnataka.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaushalya.karnataka.ui.components.SectionHeader
import com.kaushalya.karnataka.ui.theme.Green40
import com.kaushalya.karnataka.ui.theme.Green60
import com.kaushalya.karnataka.viewmodel.WorkerDashboardViewModel
import com.kaushalya.karnataka.data.model.Booking

@Composable
fun WorkerEarningsScreen(
    viewModel: WorkerDashboardViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF1B5E20), Green40)))
                .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Earnings Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Performance & Payout Overview",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Overview Grid ──────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EarningsOverviewCard(
                    label = "Total Earnings",
                    value = "₹${viewModel.totalEarnings}",
                    icon = Icons.Filled.AccountBalanceWallet,
                    color = Green40,
                    modifier = Modifier.weight(1f)
                )
                EarningsOverviewCard(
                    label = "Monthly",
                    value = "₹${viewModel.earnings.lastOrNull()?.amount ?: 12500}",
                    icon = Icons.Filled.CalendarMonth,
                    color = Color(0xFF6A1B9A),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EarningsOverviewCard(
                    label = "Completed",
                    value = "${viewModel.totalJobs} Jobs",
                    icon = Icons.Filled.CheckCircle,
                    color = Color(0xFF1A6B5A),
                    modifier = Modifier.weight(1f)
                )
                EarningsOverviewCard(
                    label = "Pending",
                    value = "${viewModel.bookingRequests.size} Requests",
                    icon = Icons.Filled.PendingActions,
                    color = Color(0xFFB05E00),
                    modifier = Modifier.weight(1f)
                )
                EarningsOverviewCard(
                    label = "Rating",
                    value = "${viewModel.currentWorker.rating}",
                    icon = Icons.Filled.Star,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.weight(0.8f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Earnings Trend (Horizontal Chart) ──────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(title = "📈 Earnings Trend")
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    val maxAmount = (viewModel.earnings.maxOfOrNull { it.amount } ?: 20000).toFloat()
                    
                    viewModel.earnings.forEachIndexed { index, earning ->
                        // Animated progress bar
                        var progress by remember { mutableStateOf(0f) }
                        LaunchedEffect(Unit) {
                            progress = earning.amount / maxAmount
                        }
                        val animatedProgress by animateFloatAsState(
                            targetValue = progress,
                            animationSpec = tween(durationMillis = 1000, delayMillis = index * 100),
                            label = "BarAnimation"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Month Label
                            Text(
                                text = earning.month.take(3),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(40.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            // Horizontal Bar
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(animatedProgress)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Green40, Green60)
                                            )
                                        )
                                )
                            }
                            
                            // Amount Label
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "₹${earning.amount}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.width(50.dp),
                                textAlign = TextAlign.End,
                                color = Green40
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Monthly Breakdown ──────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(title = "📅 Monthly Breakdown")
            Spacer(Modifier.height(12.dp))
            
            // Grid of monthly cards
            viewModel.earnings.chunked(2).forEach { rowMonths ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowMonths.forEach { monthData ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Green40.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        monthData.month.take(1),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Green40
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(monthData.month, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Text("₹${monthData.amount}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = Green40)
                                }
                            }
                        }
                    }
                    if (rowMonths.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Recent Payments ─────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(title = "💰 Recent Payments")
            Spacer(Modifier.height(12.dp))
            
            if (viewModel.recentPayments.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No recent payments found", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                viewModel.recentPayments.forEach { payment ->
                    PaymentListItem(payment = payment)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun EarningsOverviewCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PaymentListItem(payment: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Green40.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null, tint = Green40, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(payment.customerName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("${payment.workerCategory.displayName} · ${payment.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${payment.totalAmount}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Green40)
                Text("Completed", style = MaterialTheme.typography.labelSmall, color = Color(0xFF1B7A3E))
            }
        }
    }
}
