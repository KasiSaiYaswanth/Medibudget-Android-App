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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medibudget.app.ui.components.GlassmorphicCard
import com.medibudget.app.ui.components.SectionHeader
import com.medibudget.app.ui.theme.DarkBackground
import com.medibudget.app.ui.theme.DarkSurface
import com.medibudget.app.ui.theme.PrimaryEmerald
import com.medibudget.app.ui.theme.TextGray
import com.medibudget.app.ui.theme.TextWhite
import com.medibudget.app.ui.viewmodel.HealthViewModel

@Composable
fun MapFinderScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val allHospitals by viewModel.hospitals.collectAsState()
    var selectedHospitalId by remember { mutableStateOf<String?>(null) }
    val selectedHospital = allHospitals.firstOrNull { it.id == selectedHospitalId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            SectionHeader(
                title = "Hospital Finder",
                subtitle = "OSM Reverse GPS map tracking."
            )
        }

        // Simulated High-Fidelity Map Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(DarkSurface.copy(alpha = 0.5f))
                .border(1.dp, Color.White.copy(0.08f))
        ) {
            // Simulated Map Grid & Markers
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Simulated OpenStreetMap leaf canvas",
                    color = TextGray.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Render custom interactive hospital markers on canvas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MapMarkerItem("Apollo Hospital", true) { selectedHospitalId = allHospitals.firstOrNull { it.name.contains("Apollo") }?.id }
                    MapMarkerItem("Fortis Hospital", false) { selectedHospitalId = allHospitals.firstOrNull { it.name.contains("Fortis") }?.id }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MapMarkerItem("AIIMS Govt Hospital", false) { selectedHospitalId = allHospitals.firstOrNull { it.name.contains("AIIMS") }?.id }
                    MapMarkerItem("Manipal Clinic", true) { selectedHospitalId = allHospitals.firstOrNull { it.name.contains("Manipal") }?.id }
                }
            }

            // Floating indicator for current GPS coordinates
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryEmerald.copy(0.15f))
                    .border(1.dp, PrimaryEmerald.copy(0.3f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("GPS Coordinates: 17.6868° N, 83.2185° E", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Selected Hospital Bottom Card Sheet
        if (selectedHospital != null) {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(selectedHospital.name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("${selectedHospital.city}, ${selectedHospital.state}", color = TextGray, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryEmerald.copy(0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                selectedHospital.pricingTier.uppercase(),
                                color = PrimaryEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Divider(color = Color.White.copy(0.08f), modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Category: ${selectedHospital.category}", color = TextWhite, fontSize = 12.sp)
                        }
                        Text("Base Consultation: ₹${selectedHospital.consultationCost}", color = PrimaryEmerald, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .clickable {
                                    Toast.makeText(context, "Navigating route in OsmMaps... GPS coordinates synched", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Navigate Route", color = TextWhite, fontSize = 13.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryEmerald)
                                .clickable {
                                    Toast.makeText(context, "Initiating contact: dialling +91 99887 76655", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Contact Hospital", color = TextWhite, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // Hint Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Tap on any custom map marker above to details comparative pricing, facility category, and phone details.",
                    color = TextGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MapMarkerItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = Icons.Default.LocalHospital,
            contentDescription = name,
            tint = if (isSelected) PrimaryEmerald else Color.Red,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
