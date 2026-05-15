package com.kaushalya.karnataka.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import com.kaushalya.karnataka.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    sessionReady: Boolean,
    isLoggedIn: Boolean,
    onSplashDone: () -> Unit,
    onAlreadyLoggedIn: (() -> Unit)? = null
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    var animationsDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        alpha.animateTo(1f, animationSpec = tween(500))
        delay(300)
        taglineAlpha.animateTo(1f, animationSpec = tween(600))
        delay(800)
        animationsDone = true
    }

    // Wait for Firebase Auth + Firestore restore before routing (avoids wrong "logged out" on cold start).
    LaunchedEffect(sessionReady, animationsDone, isLoggedIn) {
        if (!animationsDone || !sessionReady) return@LaunchedEffect
        if (isLoggedIn && onAlreadyLoggedIn != null) {
            onAlreadyLoggedIn()
        } else {
            onSplashDone()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Green40, Green50, Green60)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ElectricBolt,
                        contentDescription = "Logo",
                        tint = Green40,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // App Name
            Text(
                text = "Kaushalya",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.graphicsLayer { this.alpha = alpha.value }
            )
            Text(
                text = "Karnataka",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.graphicsLayer { this.alpha = alpha.value }
            )

            Spacer(Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Skilled Workers at Your Doorstep",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer { this.alpha = taglineAlpha.value }
                    .padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(64.dp))

            // Loading indicator
            CircularProgressIndicator(
                color = Color.White.copy(alpha = 0.7f),
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(32.dp)
                    .graphicsLayer { this.alpha = taglineAlpha.value }
            )
        }

        // Bottom text
        Text(
            text = "ಕೌಶಲ್ಯ ಕರ್ನಾಟಕ",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .graphicsLayer { this.alpha = taglineAlpha.value }
        )
    }
}
