package com.medibudget.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemeCheckerScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit
) {
    val activeSchemes by viewModel.activeSchemes.collectAsState()
    val context = LocalContext.current

    var selectedState by remember { mutableStateOf("Andhra Pradesh") }
    var incomeInput by remember { mutableStateOf("250000") }
    var hasRationCard by remember { mutableStateOf("yes") }
    var rationCardType by remember { mutableStateOf("white") }
    var employmentType by remember { mutableStateOf("private") }

    var stateExpanded by remember { mutableStateOf(false) }
    var rationExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var empExpanded by remember { mutableStateOf(false) }

    var eligibilityChecked by remember { mutableStateOf(false) }
    var eligibleSchemesList by remember { mutableStateOf<List<SchemeResult>>(emptyList()) }

    val states = listOf("Andhra Pradesh", "Telangana", "Tamil Nadu", "Karnataka", "Kerala", "Maharashtra", "Rajasthan", "Delhi")
    val rationCards = listOf("antyodaya" to "Antyodaya (AAY) - Poorest", "white" to "White / BPL Card", "yellow" to "Yellow Card", "pink" to "Pink / APL Card")
    val employmentTypes = listOf("government" to "Gov Employee", "private" to "Private Employee (Organized)", "unorganized" to "Daily Wage / Unorganized", "farmer" to "Farmer")

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
                title = "Schemes Checker",
                subtitle = "Verify central & state health scheme coverage."
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (!eligibilityChecked) {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // State Selector
                    ExposedDropdownMenuBox(
                        expanded = stateExpanded,
                        onExpandedChange = { stateExpanded = !stateExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedState,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Residing State", color = TextGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = Color.White.copy(0.1f)
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = stateExpanded,
                            onDismissRequest = { stateExpanded = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            states.forEach { state ->
                                DropdownMenuItem(
                                    text = { Text(state, color = TextWhite) },
                                    onClick = {
                                        selectedState = state
                                        stateExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Income Input
                    OutlinedTextField(
                        value = incomeInput,
                        onValueChange = { incomeInput = it },
                        label = { Text("Annual Household Income (₹)", color = TextGray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = Color.White.copy(0.1f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Ration Card Toggles
                    ExposedDropdownMenuBox(
                        expanded = rationExpanded,
                        onExpandedChange = { rationExpanded = !rationExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val cardLabel = if (hasRationCard == "yes") "Yes, I hold a ration card" else "No, I do not hold one"
                        OutlinedTextField(
                            value = cardLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ration Card Status", color = TextGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rationExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = Color.White.copy(0.1f)
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = rationExpanded,
                            onDismissRequest = { rationExpanded = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            DropdownMenuItem(text = { Text("Yes", color = TextWhite) }, onClick = { hasRationCard = "yes"; rationExpanded = false })
                            DropdownMenuItem(text = { Text("No", color = TextWhite) }, onClick = { hasRationCard = "no"; rationExpanded = false })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (hasRationCard == "yes") {
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = !typeExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val typeLabel = rationCards.firstOrNull { it.first == rationCardType }?.second ?: "Select Type"
                            OutlinedTextField(
                                value = typeLabel,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Ration Card Color/Type", color = TextGray) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = Color.White.copy(0.1f)
                                ),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false },
                                modifier = Modifier.background(DarkSurface)
                            ) {
                                rationCards.forEach { (key, label) ->
                                    DropdownMenuItem(text = { Text(label, color = TextWhite) }, onClick = { rationCardType = key; typeExpanded = false })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Employment Selection
                    ExposedDropdownMenuBox(
                        expanded = empExpanded,
                        onExpandedChange = { empExpanded = !empExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val empLabel = employmentTypes.firstOrNull { it.first == employmentType }?.second ?: "Select Job"
                        OutlinedTextField(
                            value = empLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Employment Sector", color = TextGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = empExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = Color.White.copy(0.1f)
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = empExpanded,
                            onDismissRequest = { empExpanded = false },
                            modifier = Modifier.background(DarkSurface)
                        ) {
                            employmentTypes.forEach { (key, label) ->
                                DropdownMenuItem(text = { Text(label, color = TextWhite) }, onClick = { employmentType = key; empExpanded = false })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    PremiumGradientButton(
                        text = "Verify Eligibility",
                        onClick = {
                            val annualIncome = incomeInput.toIntOrNull() ?: 0
                            val isBPL = rationCardType == "white" || rationCardType == "yellow" || rationCardType == "antyodaya"
                            val isGov = employmentType == "government"
                            val results = mutableListOf<SchemeResult>()

                            // 1. Ayushman Bharat Check
                            val pmjay = annualIncome <= 300000 && hasRationCard == "yes" && isBPL
                            results.add(
                                SchemeResult(
                                    name = "Ayushman Bharat PM-JAY",
                                    eligible = pmjay,
                                    coverage = "₹5,00,000 per family/year",
                                    reason = if (pmjay) "Income below ₹3L limit & valid BPL card holder." else "Requires BPL / Antyodaya category ration card."
                                )
                            )

                            // 2. ESI Check
                            val esi = employmentType == "private" && annualIncome <= 252000
                            results.add(
                                SchemeResult(
                                    name = "Employee State Insurance (ESI)",
                                    eligible = esi,
                                    coverage = "Full cashless medical hospitalization",
                                    reason = if (esi) "Organized private sector employee with monthly salary under ₹21,000 limit." else "Only applicable to organized private workers under salary criteria."
                                )
                            )

                            // 3. State Scheme Check
                            val stateEligible = when (selectedState) {
                                "Andhra Pradesh" -> isBPL && annualIncome <= 500000
                                "Telangana" -> isBPL && annualIncome <= 500000
                                "Tamil Nadu" -> annualIncome <= 72000
                                "Karnataka" -> isBPL
                                else -> true
                            }
                            val stateSchemeName = when (selectedState) {
                                "Andhra Pradesh" -> "YSR Aarogyasri (AP)"
                                "Telangana" -> "Aarogyasri Health Scheme (Telangana)"
                                "Tamil Nadu" -> "CMCHIS (Tamil Nadu)"
                                "Karnataka" -> "Vajpayee Arogyashri (Karnataka)"
                                else -> "Chief Minister State Health Scheme"
                            }
                            results.add(
                                SchemeResult(
                                    name = stateSchemeName,
                                    eligible = stateEligible,
                                    coverage = "₹2,00,000 - ₹5,00,000 cashless limits",
                                    reason = if (stateEligible) "Meets criteria set by the state of $selectedState." else "Income or category card bounds exceeded for $selectedState."
                                )
                            )

                            eligibleSchemesList = results
                            eligibilityChecked = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Display computed results
            Text("Your Eligible Health Schemes", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            eligibleSchemesList.forEach { result ->
                val cardBorder = if (result.eligible) PrimaryEmerald.copy(0.3f) else Color.White.copy(0.08f)
                val statusText = if (result.eligible) "ELIGIBLE" else "NOT ELIGIBLE"
                val statusColor = if (result.eligible) PrimaryEmerald else TextGray

                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    borderColor = cardBorder
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(result.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(statusText, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(result.reason, color = TextGray, fontSize = 12.sp, lineHeight = 18.sp)
                        
                        if (result.eligible) {
                            Divider(color = Color.White.copy(0.05f), modifier = Modifier.padding(vertical = 12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Coverage Limit: ${result.coverage}", color = PrimaryEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            PremiumGradientButton(
                text = "Re-check Eligibility Criteria",
                onClick = { eligibilityChecked = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private data class SchemeResult(
    val name: String,
    val eligible: Boolean,
    val coverage: String,
    val reason: String
)
