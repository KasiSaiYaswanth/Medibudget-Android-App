package com.medibudget.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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
fun OCRScannerScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    onAlternativeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scannedMedicine by viewModel.scannedMedicine.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
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
                title = "MediScan OCR",
                subtitle = "Capture packaging labels to find generic substitutes."
            )
        }

        if (scannedMedicine != null) {
            // Display matching generic mapping alternatives
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryEmerald,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Medicine Package Identified!",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("DETECTED PRODUCT", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(scannedMedicine!!.name, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Chemical Composition: ${scannedMedicine!!.composition}", color = TextGray, fontSize = 13.sp)
                        Text("Market price range: ${scannedMedicine!!.priceRange}", color = Color.Red.copy(0.8f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        
                        Divider(color = Color.White.copy(0.08f), modifier = Modifier.padding(vertical = 16.dp))

                        Text("GENERIC ALTERNATIVE EQUIVALENT", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Paracetamol Generic 500mg", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Expected generic cost: ₹10 - ₹25 (80% cheaper)", color = PrimaryEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                PremiumGradientButton(
                    text = "View Detailed Composition",
                    onClick = { onAlternativeSelected(scannedMedicine!!.id) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .clickable { viewModel.clearScannedMedicine() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Scan Another Package", color = TextWhite)
                }
            }
        } else {
            // Camera Preview Frame
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            ) {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val selector = CameraSelector.DEFAULT_BACK_CAMERA
                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Camera access permission is required for scan actions.", color = TextGray)
                    }
                }

                // Focus area indicator
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .border(3.dp, PrimaryEmerald, RoundedCornerShape(16.dp))
                        .align(Alignment.Center)
                )

                // Simulated Capture button
                IconButton(
                    onClick = {
                        // Simulate OCR package recognition callback
                        Toast.makeText(context, "Analyzing packaging... matching formula database", Toast.LENGTH_SHORT).show()
                        viewModel.performOcrMatch(listOf("Dolo", "Dolo 650", "Paracetamol"))
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PrimaryEmerald)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Capture", tint = TextWhite)
                }
            }
        }
    }
}

@Composable
fun GenericFinderScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    onMedicineSelected: (String) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
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
                title = "Generic Database",
                subtitle = "Fuzzy medicine formulas search."
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.searchMedicines(it)
            },
            placeholder = { Text("Search medicine name or generic composition...", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = PrimaryEmerald,
                unfocusedBorderColor = Color.White.copy(0.1f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (searchResults.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = TextGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Type a medicine brand name above (e.g. Crocin, Dolo, Saridon)", color = TextGray, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(searchResults) { med ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .clickable { onMedicineSelected(med.id) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(med.name, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(med.category, color = TextGray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PrimaryEmerald)
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineDetailScreen(
    viewModel: HealthViewModel,
    id: String?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val allMeds by viewModel.allMedicines.collectAsState()
    val med = allMeds.firstOrNull { it.id == id }

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
                title = "Drug Composition",
                subtitle = "Detailed drug breakdown & warnings."
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (med == null) {
            Text("Medicine details not cached.", color = TextGray)
        } else {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(med.name, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(med.category, color = TextGray, fontSize = 12.sp)
                        }
                    }
                    Divider(color = Color.White.copy(0.08f), modifier = Modifier.padding(vertical = 16.dp))

                    Text("ACTIVE INGREDIENTS / CHEMICAL FORMULA", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(med.genericName, color = TextWhite, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("THERAPEUTIC USES", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(med.usesRaw.replace(",", "\n• "), color = TextWhite, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("STANDARD DOSAGE METHOD", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(med.dosage, color = TextWhite, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("POTENTIAL SIDE EFFECTS", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(med.sideEffectsRaw.replace(",", "\n• "), color = TextWhite, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Warnings Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Red.copy(alpha = 0.1f))
                    .border(1.dp, Color.Red.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("CRITICAL CAUTION WARNINGS", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(med.warningsRaw.replace(",", "\n⚠️ "), color = Color.Red, fontSize = 11.sp, lineHeight = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            PremiumGradientButton(
                text = "Add to Scan History",
                onClick = {
                    Toast.makeText(context, "Medicine logged to profile history!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
