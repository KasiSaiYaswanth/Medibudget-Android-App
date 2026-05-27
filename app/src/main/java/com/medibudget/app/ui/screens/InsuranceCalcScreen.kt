package com.medibudget.app.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.medibudget.app.ui.theme.CustomBlue

@Composable
fun InsuranceCalcScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    // Inputs state variables
    var totalBill by remember { mutableStateOf("150000") }
    var deductible by remember { mutableStateOf("10000") }
    var copayPercent by remember { mutableFloatStateOf(10f) }
    var roomRentLimit by remember { mutableStateOf("5000") }
    var roomRentActual by remember { mutableStateOf("7000") }
    var hospitalDays by remember { mutableStateOf("4") }
    
    // Calculations state variables
    var showResults by remember { mutableStateOf(false) }
    var calcApprovedClaim by remember { mutableStateOf(0.0) }
    var calcOutOfPocket by remember { mutableStateOf(0.0) }
    var calcRoomRentDeduction by remember { mutableStateOf(0.0) }
    var calcCopayDeduction by remember { mutableStateOf(0.0) }

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
                title = "Insurance Coverage",
                subtitle = "Calculate copay deductions & rent caps."
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Card containing form fields
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                
                // Total medical bill field
                Text("Total Estimated Bill (₹)", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = totalBill,
                    onValueChange = { totalBill = it },
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

                // Deductible amount
                Text("Policy Deductible (₹)", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = deductible,
                    onValueChange = { deductible = it },
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

                // Copay Percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Policy Copay (%)", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${copayPercent.toInt()}%", color = PrimaryEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = copayPercent,
                    onValueChange = { copayPercent = it },
                    valueRange = 0f..50f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryEmerald,
                        activeTrackColor = PrimaryEmerald,
                        inactiveTrackColor = DarkSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Room Rent cap configurations
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Room Limit / Day (₹)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = roomRentLimit,
                            onValueChange = { roomRentLimit = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = Color.White.copy(0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Actual Room Rent (₹)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = roomRentActual,
                            onValueChange = { roomRentActual = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = Color.White.copy(0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Days Hospitalized
                Text("Number of Hospital Days", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = hospitalDays,
                    onValueChange = { hospitalDays = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color.White.copy(0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                PremiumGradientButton(
                    text = "Calculate Claim Coverage",
                    onClick = {
                        val bill = totalBill.toDoubleOrNull() ?: 0.0
                        val ded = deductible.toDoubleOrNull() ?: 0.0
                        val rentLimit = roomRentLimit.toDoubleOrNull() ?: 0.0
                        val rentActual = roomRentActual.toDoubleOrNull() ?: 0.0
                        val days = hospitalDays.toDoubleOrNull() ?: 1.0

                        if (bill <= 0.0) {
                            Toast.makeText(context, "Please enter a valid bill amount.", Toast.LENGTH_SHORT).show()
                            return@PremiumGradientButton
                        }

                        // Deductible reduces initial claimable pool
                        val baseClaimable = maxOf(0.0, bill - ded)
                        
                        // Room Rent Capping Deduction
                        val rentDiff = rentActual - rentLimit
                        val rentDeduction = if (rentDiff > 0.0) rentDiff * days else 0.0
                        
                        // Remaining after room rent capping
                        val claimableAfterRoom = maxOf(0.0, baseClaimable - rentDeduction)
                        
                        // Copay calculation
                        val copayAmt = claimableAfterRoom * (copayPercent / 100.0)
                        
                        // Final Approved Claim
                        val approved = maxOf(0.0, claimableAfterRoom - copayAmt)
                        
                        // Out of Pocket expense
                        val outOfPocket = bill - approved

                        calcApprovedClaim = approved
                        calcOutOfPocket = outOfPocket
                        calcRoomRentDeduction = rentDeduction
                        calcCopayDeduction = copayAmt
                        showResults = true
                        
                        Toast.makeText(context, "Calculations simulated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // High-Fidelity Results Card
        if (showResults) {
            GlassmorphicCard(
                borderColor = PrimaryEmerald.copy(0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SIMULATION RESULTS", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryEmerald.copy(0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("PROCESSED", color = PrimaryEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Claims Meters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Approved Claim Amount", color = TextGray, fontSize = 12.sp)
                            Text("₹${String.format("%.2f", calcApprovedClaim)}", color = PrimaryEmerald, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Your Out-of-Pocket", color = TextGray, fontSize = 12.sp)
                            Text("₹${String.format("%.2f", calcOutOfPocket)}", color = Color.Red, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = Color.White.copy(0.08f), modifier = Modifier.padding(vertical = 16.dp))

                    Text("CLAIM DEDUCTION BREAKDOWN", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    DeductionRow("Base Policy Deductible", "- ₹${deductible}", CustomBlue)
                    DeductionRow("Room Rent Capping Penalty", "- ₹${String.format("%.2f", calcRoomRentDeduction)}", Color.Red)
                    DeductionRow("Policy Copay Deduction", "- ₹${String.format("%.2f", calcCopayDeduction)}", Color.Yellow)

                    Divider(color = Color.White.copy(0.08f), modifier = Modifier.padding(vertical = 16.dp))

                    // Summary Banner Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryEmerald.copy(0.08f))
                            .border(1.dp, PrimaryEmerald.copy(0.2f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Your insurance covers ${String.format("%.1f", (calcApprovedClaim / (totalBill.toDoubleOrNull() ?: 1.0)) * 100)}% of this medical expense, saving you ₹${String.format("%.0f", calcApprovedClaim)} in total costs.",
                                color = TextWhite,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DeductionRow(
    label: String,
    amount: String,
    badgeColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = TextGray, fontSize = 12.sp)
        }
        Text(amount, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
