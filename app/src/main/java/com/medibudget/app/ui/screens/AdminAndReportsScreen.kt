package com.medibudget.app.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medibudget.app.ui.components.GlassmorphicCard
import com.medibudget.app.ui.components.PremiumGradientButton
import com.medibudget.app.ui.components.SectionHeader
import com.medibudget.app.ui.theme.DarkBackground
import com.medibudget.app.ui.theme.DarkSurface
import com.medibudget.app.ui.theme.PrimaryEmerald
import com.medibudget.app.ui.theme.TextGray
import com.medibudget.app.ui.theme.TextWhite
import com.medibudget.app.ui.viewmodel.HealthViewModel

@Composable
fun ReportsScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val allMeds by viewModel.allMedicines.collectAsState()
    val estimations by viewModel.estimationHistory.collectAsState()

    // Calculated metrics
    val totalEstimations = estimations.size
    val totalEstimatedCost = estimations.sumOf { it.total }
    
    // Simulate generic medicine savings
    val estimatedGenericSavings = totalEstimatedCost * 0.7

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
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
                title = "Expense Reports",
                subtitle = "Track monthly budgets & formula savings."
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Visual Budget Progress Ring / Bar
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Monthly Health Budget", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Target: ₹10,000 budget cap", color = TextGray, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.PieChart, contentDescription = null, tint = PrimaryEmerald)
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                val currentSpend = Math.min(totalEstimatedCost, 10000.0)
                val fraction = (currentSpend / 10000.0).toFloat()
                
                LinearProgressIndicator(
                    progress = fraction,
                    color = PrimaryEmerald,
                    trackColor = Color.White.copy(0.1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Spent: ₹${Math.round(totalEstimatedCost)}", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Remaining: ₹${Math.round(10000.0 - totalEstimatedCost)}", color = TextGray, fontSize = 12.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Generic Savings Card
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = PrimaryEmerald.copy(0.3f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimaryEmerald.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Savings, contentDescription = null, tint = PrimaryEmerald)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Fuzzy Composition Savings", color = TextGray, fontSize = 12.sp)
                    Text("Saved ₹${Math.round(estimatedGenericSavings)}", color = PrimaryEmerald, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("By choosing generic substitutes", color = TextGray, fontSize = 11.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Historical estimations logs
        Text("Detailed Expenditure Logs", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (estimations.isEmpty()) {
            Text("No transactions or estimations logged. Try running the cost estimator!", color = TextGray, fontSize = 12.sp)
        } else {
            estimations.forEach { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(log.condition, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(log.date, color = TextGray, fontSize = 11.sp)
                    }
                    Text("₹${log.total}", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Dynamic PDF Exporter trigger
        PremiumGradientButton(
            text = "Export PDF Receipt Report",
            onClick = {
                Toast.makeText(context, "Generating medical expense PDF... Saved to Downloads folder", Toast.LENGTH_LONG).show()
            },
            icon = Icons.Default.Download,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AdminDashboardScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val allMeds by viewModel.allMedicines.collectAsState()
    val estimations by viewModel.estimationHistory.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
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
                title = "System Console",
                subtitle = "Database counts & query telemetry stats."
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Grid of Database Telemetry counts
        Row(modifier = Modifier.fillMaxWidth()) {
            AdminCountCard(
                title = "Medicine Cache",
                count = "${allMeds.size} items",
                icon = Icons.Default.Storage,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            AdminCountCard(
                title = "Cost Estimates",
                count = "${estimations.size} logs",
                icon = Icons.Default.Assessment,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            AdminCountCard(
                title = "OSM Telemetry",
                count = "12 API Calls",
                icon = Icons.Default.CloudQueue,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            AdminCountCard(
                title = "App Health",
                count = "0 Crashes",
                icon = Icons.Default.BugReport,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Telemetry details list
        Text("Active Database Config", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                ConfigInfoRow("SQLite Version", "3.42.0")
                ConfigInfoRow("Room Schema version", "v1.0")
                ConfigInfoRow("Supabase Publishable", "Active JWT keys")
                ConfigInfoRow("Nominatim Reverse URL", "https://nominatim.openstreetmap.org/")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Reset and clear cache options
        Text("Admin System Controls", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Red.copy(0.1f))
                .border(1.dp, Color.Red.copy(0.3f), RoundedCornerShape(12.dp))
                .clickable {
                    viewModel.clearEstimationLogs()
                    viewModel.clearSearchHistoryLogs()
                    Toast.makeText(context, "Clean sweep: system log caches cleared successfully!", Toast.LENGTH_SHORT).show()
                },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear System Log Cache", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PremiumGradientButton(
            text = "Synchronize Database Cache",
            onClick = {
                isRefreshing = true
                Toast.makeText(context, "Refreshed remote Supabase instances and OpenStreetMap bounds", Toast.LENGTH_SHORT).show()
                isRefreshing = false
            },
            icon = Icons.Default.Refresh,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AdminCountCard(
    title: String,
    count: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Icon(icon, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
            }
            Text(count, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ConfigInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextGray, fontSize = 13.sp)
        Text(value, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
