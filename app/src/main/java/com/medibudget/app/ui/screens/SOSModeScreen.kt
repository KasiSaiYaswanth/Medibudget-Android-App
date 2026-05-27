package com.medibudget.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medibudget.app.ui.components.GlassmorphicCard
import com.medibudget.app.ui.components.SectionHeader
import com.medibudget.app.ui.theme.CustomRed
import com.medibudget.app.ui.theme.DarkBackground
import com.medibudget.app.ui.theme.DarkSurface
import com.medibudget.app.ui.theme.PrimaryEmerald
import com.medibudget.app.ui.theme.TextGray
import com.medibudget.app.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun SOSModeScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var countdownState by remember { mutableIntStateOf(3) }
    var sosTriggered by remember { mutableStateOf(false) }

    // Pulsing expand animation for the emergency button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_sos")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LaunchedEffect(key1 = true) {
        while (countdownState > 0 && !sosTriggered) {
            delay(1000)
            countdownState -= 1
        }
        if (countdownState == 0) {
            sosTriggered = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            SectionHeader(
                title = "Emergency SOS",
                subtitle = "Life-saving coordination console."
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        if (!sosTriggered) {
            // Countdown Safe Mode Screen
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "TRIGGERING RESCUE SIGNAL IN",
                    color = TextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    countdownState.toString(),
                    color = CustomRed,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(CustomRed)
                        .clickable { sosTriggered = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("TRIGGER\nNOW", color = TextWhite, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(40.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(16.dp))
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("CANCEL EMERGENCY SIGNAL", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // SOS Triggered Panel
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CustomRed.copy(0.15f))
                        .border(1.dp, CustomRed.copy(0.4f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = CustomRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("RESCUE SIGNAL BROADCAST ACTIVE", color = CustomRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Current coordinates (17.68° N, 83.21° E) sent to contacts.", color = TextGray, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Action Call Buttons
                Row(modifier = Modifier.fillMaxWidth()) {
                    EmergencyCallTile("Ambulance (108)", Icons.Default.Call, CustomRed, modifier = Modifier.weight(1f)) {
                        Toast.makeText(context, "Dialling Ambulance 108...", Toast.LENGTH_SHORT).show()
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    EmergencyCallTile("Trauma Clinic Map", Icons.Default.Map, PrimaryEmerald, modifier = Modifier.weight(1f)) {
                        Toast.makeText(context, "Searching nearest emergency trauma clinics...", Toast.LENGTH_SHORT).show()
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Swipeable First-Aid Guides list
                Text("Swipe First-Aid Action Cards", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(12.dp))

                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = PrimaryEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CPR Resuscitation Guide (Adult)", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Divider(color = Color.White.copy(0.08f), modifier = Modifier.padding(vertical = 12.dp))
                        Text(
                            "1. Push hard and fast in the center of the chest (100-120 compressions per minute).\n" +
                            "2. Deliver 30 chest compressions followed by 2 quick rescue breaths.\n" +
                            "3. Keep repeating cycles until professional emergency paramedics arrive.",
                            color = TextGray,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryEmerald)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("RESOLVE EMERGENCY MODE", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmergencyCallTile(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
