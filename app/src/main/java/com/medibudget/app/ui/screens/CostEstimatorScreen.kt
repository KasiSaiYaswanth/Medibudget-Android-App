package com.medibudget.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medibudget.app.ui.components.GlassmorphicCard
import com.medibudget.app.ui.components.MedicalDisclaimer
import com.medibudget.app.ui.components.PremiumGradientButton
import com.medibudget.app.ui.components.SectionHeader
import com.medibudget.app.ui.theme.DarkBackground
import com.medibudget.app.ui.theme.DarkSurface
import com.medibudget.app.ui.theme.PrimaryEmerald
import com.medibudget.app.ui.theme.TextGray
import com.medibudget.app.ui.theme.TextWhite
import com.medibudget.app.ui.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostEstimatorScreen(
    viewModel: HealthViewModel,
    initialCondition: String?,
    onNavigateBack: () -> Unit,
    onCalculateClick: (String, String, String) -> Unit
) {
    val gpsCity by viewModel.gpsCity.collectAsState()
    val context = LocalContext.current

    val conditions = listOf(
        "fever" to "Viral Fever / Influenza",
        "fracture" to "Broken Bone / Fracture",
        "cardiac" to "Heart Health Evaluation",
        "bypass" to "Coronary Bypass Surgery",
        "angioplasty" to "Angioplasty Stenting",
        "dental" to "Dental Root Canal",
        "eye" to "Cataract Eye Surgery",
        "maternity" to "Normal Delivery",
        "csection" to "Caesarean C-Section",
        "kidney" to "Kidney Dialysis Cycle",
        "transplant" to "Organ Transplant",
        "skin" to "Dermatology Treatment",
        "cancer" to "Chemotherapy Cycle",
        "appendix" to "Appendectomy Surgery",
        "hernia" to "Hernia Repair Surgery",
        "knee" to "Knee Joint Replacement",
        "spine" to "Spine Surgery",
        "diabetes" to "Diabetes Control",
        "thyroid" to "Thyroid Consultation",
        "neuro" to "Neurology Consultation"
    )

    val cities = listOf("Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Pune", "Ahmedabad", "Visakhapatnam")
    val facilities = listOf(
        "government" to "Government General Ward",
        "trust" to "Charitable Trust Clinic",
        "private" to "Private Specialty Hospital",
        "corporate" to "Super-Specialty Corporate Hospital"
    )

    var conditionKey by remember { mutableStateOf(initialCondition ?: "fever") }
    var selectedCity by remember { mutableStateOf(gpsCity) }
    var selectedFacility by remember { mutableStateOf("private") }

    var condExpanded by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }
    var facExpanded by remember { mutableStateOf(false) }

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
                title = "Cost Intelligence",
                subtitle = "Calculate clinical estimates inside Indian states."
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // GPS Location quick fill indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryEmerald.copy(0.1f))
                        .clickable {
                            viewModel.updateLocation(17.6868, 83.2185) // Simulated coordinates for Visakhapatnam
                            selectedCity = "Visakhapatnam"
                            Toast.makeText(context, "Location updated via OpenStreetMap geocoder!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Current geocoded city: $gpsCity. Click to refresh.",
                        color = PrimaryEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Condition Selector
                ExposedDropdownMenuBox(
                    expanded = condExpanded,
                    onExpandedChange = { condExpanded = !condExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val matchingLabel = conditions.firstOrNull { it.first == conditionKey }?.second ?: "Select Diagnosis"
                    OutlinedTextField(
                        value = matchingLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Diagnostic Condition", color = TextGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = condExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = Color.White.copy(0.1f)
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = condExpanded,
                        onDismissRequest = { condExpanded = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        conditions.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label, color = TextWhite) },
                                onClick = {
                                    conditionKey = key
                                    condExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // City Selector
                ExposedDropdownMenuBox(
                    expanded = cityExpanded,
                    onExpandedChange = { cityExpanded = !cityExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select City", color = TextGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = Color.White.copy(0.1f)
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = cityExpanded,
                        onDismissRequest = { cityExpanded = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city, color = TextWhite) },
                                onClick = {
                                    selectedCity = city
                                    cityExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Facility category selector
                ExposedDropdownMenuBox(
                    expanded = facExpanded,
                    onExpandedChange = { facExpanded = !facExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val matchingLabel = facilities.firstOrNull { it.first == selectedFacility }?.second ?: "Select Facility"
                    OutlinedTextField(
                        value = matchingLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hospital Tier", color = TextGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = Color.White.copy(0.1f)
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = facExpanded,
                        onDismissRequest = { facExpanded = false },
                        modifier = Modifier.background(DarkSurface)
                    ) {
                        facilities.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label, color = TextWhite) },
                                onClick = {
                                    selectedFacility = key
                                    facExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                PremiumGradientButton(
                    text = "Calculate Forecast",
                    onClick = { onCalculateClick(conditionKey, selectedCity, selectedFacility) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        MedicalDisclaimer()
    }
}

@Composable
fun CostBreakdownScreen(
    viewModel: HealthViewModel,
    condition: String,
    city: String,
    facility: String,
    onNavigateBack: () -> Unit
) {
    val estimationResult by viewModel.estimationResult.collectAsState()

    // Calculate dynamically when the screen opens
    LaunchedEffect(condition, city, facility) {
        viewModel.calculateEstimate(condition, city, facility)
    }

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
                title = "Cost Projections",
                subtitle = "Calculated clinical breakdowns."
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (estimationResult == null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Computing estimations...", color = TextGray)
            }
        } else {
            val log = estimationResult!!
            
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(log.condition, color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("City multiplier: ${log.cityMultiplier}x (${log.city})", color = TextGray, fontSize = 12.sp)
                    Text("Facility multiplier: ${log.hospitalMultiplier}x (${log.hospitalType})", color = TextGray, fontSize = 12.sp)
                    Divider(color = Color.White.copy(0.08f), modifier = Modifier.padding(vertical = 16.dp))

                    CostBreakdownRow("Doctor Consultation Fee", log.consultation)
                    CostBreakdownRow("Diagnostic Tests & Labs", log.tests)
                    CostBreakdownRow("Prescription Medicines", log.medicines)
                    CostBreakdownRow("Surgical Ward & Operation", log.treatment)

                    Divider(color = Color.White.copy(0.08f), modifier = Modifier.padding(vertical = 16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Gross Treatment Total", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("₹${log.total}", color = PrimaryEmerald, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Out-of-pocket calculations card
            Text("Out-of-Pocket Insurance Comparison", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Star Health Coverage (80%)", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Deductible: ₹1,500. Insurer pays: ₹${Math.round(log.total * 0.8 * 100.0) / 100.0}", color = TextGray, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.White.copy(0.05f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("YOUR ESTIMATED OUT-OF-POCKET", color = TextWhite, fontSize = 12.sp)
                        Text("₹${Math.round((log.total * 0.2 + 1500) * 100.0) / 100.0}", color = PrimaryEmerald, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            PremiumGradientButton(
                text = "Recalculate Estimate",
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CostBreakdownRow(
    label: String,
    value: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text("₹$value", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
