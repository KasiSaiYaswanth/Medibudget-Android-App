package com.medibudget.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import com.medibudget.app.data.local.entity.EstimationLogEntity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medibudget.app.ui.components.GlassmorphicCard
import com.medibudget.app.ui.components.MedicalDisclaimer
import com.medibudget.app.ui.components.PremiumGradientButton
import com.medibudget.app.ui.components.SectionHeader
import com.medibudget.app.ui.theme.CustomRed
import com.medibudget.app.ui.theme.DarkBackground
import com.medibudget.app.ui.theme.DarkSurface
import com.medibudget.app.ui.theme.GradientTealToEmerald
import com.medibudget.app.ui.theme.PrimaryEmerald
import com.medibudget.app.ui.theme.TextGray
import com.medibudget.app.ui.theme.TextWhite
import com.medibudget.app.ui.viewmodel.AuthViewModel
import com.medibudget.app.ui.viewmodel.HealthViewModel

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    healthViewModel: HealthViewModel,
    onNavigate: (String) -> Unit
) {
    val email = authViewModel.getUserEmail()
    val estimationHistory by healthViewModel.estimationHistory.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Hello,", color = TextGray, fontSize = 14.sp)
                    Text(
                        text = email.substringBefore("@"),
                        color = TextWhite,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .clickable { onNavigate("profile") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = PrimaryEmerald)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Feature Grid
        item {
            Text("Medical Cost Assistants", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                FeatureCard(
                    title = "AI Symptom Assistant",
                    desc = "Conversational Health Assistant",
                    icon = Icons.Default.MedicalServices,
                    onClick = { onNavigate("symptom_input") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                FeatureCard(
                    title = "Cost Estimator",
                    desc = "Compare facility charges",
                    icon = Icons.Default.Calculate,
                    onClick = { onNavigate("cost_estimator") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                FeatureCard(
                    title = "MediScan OCR",
                    desc = "Generic price alternatives finder",
                    icon = Icons.Default.QrCodeScanner,
                    onClick = { onNavigate("ocr_scanner") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                FeatureCard(
                    title = "Hospital Finder",
                    desc = "OSM Reverse GPS map finder",
                    icon = Icons.Default.Map,
                    onClick = { onNavigate("map_finder") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Critical SOS Section
        item {
            GlassmorphicCard(borderColor = CustomRed.copy(0.3f)) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = CustomRed,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "EMERGENCY MEDICAL SOS",
                        color = CustomRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap the emergency trigger button below if you need immediate local ambulance coordinates or quick life-saving first-aid guidelines.",
                        color = TextGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CustomRed)
                            .clickable { onNavigate("sos_mode") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("LAUNCH SOS EMERGENCY MODE", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Schemes & Insurance Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FeatureTileMini(
                    title = "Gov Schemes Check",
                    icon = Icons.Default.LocalHospital,
                    onClick = { onNavigate("scheme_checker") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                FeatureTileMini(
                    title = "Insurance Coverage",
                    icon = Icons.Default.LocalHospital,
                    onClick = { onNavigate("insurance_calc") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Recent Estimations History
        item {
            Text("Recent Estimates Logs", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (estimationHistory.isEmpty()) {
            item {
                Text(
                    "No estimations computed yet. Try entering a symptom or procedure key!",
                    color = TextGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        } else {
            items(estimationHistory.take(5)) { log ->
                EstimationHistoryRow(log)
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    desc: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() }
            .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryEmerald,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(desc, color = TextGray, fontSize = 10.sp, lineHeight = 14.sp)
            }
        }
    }
}

@Composable
fun FeatureTileMini(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .clickable { onClick() }
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EstimationHistoryRow(log: EstimationLogEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(log.condition, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${log.city} • ${log.hospitalType}", color = TextGray, fontSize = 12.sp)
        }
        Text("₹${log.total}", color = PrimaryEmerald, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onNavigate: (String) -> Unit,
    onLogoutComplete: () -> Unit
) {
    val email = viewModel.getUserEmail()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader(
            title = "Your Profile",
            subtitle = "Healthcare Identity Card"
        )
        Spacer(modifier = Modifier.height(24.dp))

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(GradientTealToEmerald)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        email.take(2).uppercase(),
                        color = TextWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(email, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Role: User", color = TextGray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions List
        ProfileMenuItem("Edit Personal Data", Icons.Default.Person) { onNavigate("edit_profile") }
        ProfileMenuItem("Security & Settings", Icons.Default.Settings) { onNavigate("security_settings") }
        ProfileMenuItem("Help & FAQ Support", Icons.Default.Help) { onNavigate("help_faq") }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(0.05f))
                .clickable {
                    viewModel.logout()
                    onLogoutComplete()
                },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = CustomRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out Session", color = CustomRed, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileMenuItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, color = TextWhite, fontSize = 15.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
    }
    Divider(color = Color.White.copy(0.05f))
}

@Composable
fun HelpFAQScreen(
    onNavigateBack: () -> Unit
) {
    val faqList = listOf(
        Pair("What is MediBudget?", "MediBudget is an AI-powered diagnostic and medical pricing comparison app. It helps users crosscheck clinical charges, consult generic alternatives, and locate nearby healthcare providers using offline databases."),
        Pair("Are the costing estimates accurate?", "Estimates are dynamic projections based on baseline surgical fees, adjusted by state multipliers and hospital tiers (Government vs Corporate). Standard billing practices vary between institutions."),
        Pair("How does the generic medicine scanner work?", "By taking a camera package photograph, our OCR parser uses Google ML Kit Text Recognition to match composition text, linking brand name items to 50-90% cheaper generics cached in the offline room storage."),
        Pair("Is my diagnostic history private?", "Yes, all symptom logs are secured with database row level security policies (RLS). You can also disable search history in security configurations.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader(
            title = "Help & FAQ Guidelines",
            subtitle = "Everything you need to know about healthcare cost estimations."
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(faqList) { (question, answer) ->
                var expanded by remember { mutableStateOf(false) }
                
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { expanded = !expanded }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                question,
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        AnimatedVisibility(visible = expanded) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(answer, color = TextGray, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        PremiumGradientButton(
            text = "Back to Profile",
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
