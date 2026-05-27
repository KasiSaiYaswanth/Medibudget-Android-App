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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.Stethoscope
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
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
import java.util.Locale

@Composable
fun SymptomChatScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEstimator: (String) -> Unit
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isChatLoading.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    var textInput by remember { mutableStateOf("") }
    var isRecordingVoice by remember { mutableStateOf(false) }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Custom Top Bar with Cost Estimator link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Text("AI Symptom Chat", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            if (chatMessages.isNotEmpty()) {
                IconButton(onClick = {
                    // Extract condition mapping key from chat history
                    val userSpeech = chatMessages
                        .filter { it.first == "user" }
                        .joinToString(". ") { it.second }
                        .lowercase(Locale.ROOT)
                    
                    val conditionKey = when {
                        userSpeech.contains("fever") || userSpeech.contains("cold") || userSpeech.contains("flu") -> "fever"
                        userSpeech.contains("heart") || userSpeech.contains("chest") -> "cardiac"
                        userSpeech.contains("broken") || userSpeech.contains("fracture") -> "fracture"
                        userSpeech.contains("tooth") || userSpeech.contains("dental") -> "dental"
                        userSpeech.contains("stomach") || userSpeech.contains("digestion") -> " appendix"
                        else -> "fever"
                    }
                    onNavigateToEstimator(conditionKey)
                }) {
                    Icon(Icons.Default.Calculate, contentDescription = "Estimate Costs", tint = PrimaryEmerald)
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Divider(color = Color.White.copy(0.08f))

        if (chatMessages.isEmpty()) {
            // Empty Intro View
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PrimaryEmerald.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Sparkles, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("How are you feeling today?", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Describe your symptoms in natural language. Our healthcare assistant parses symptoms to suggest specialists and project pricing estimates.",
                    color = TextGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Quick Symptoms Selector Grid
                Text("Quick presets:", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                QuickSymptomTile("I have a high fever", Icons.Default.Stethoscope) {
                    viewModel.sendChatMessage("I have a high fever and body chills")
                }
                Spacer(modifier = Modifier.height(12.dp))
                QuickSymptomTile("Chest discomfort or tightness", Icons.Default.HeartBroken) {
                    viewModel.sendChatMessage("I feel chest discomfort or pressure")
                }
                
                Spacer(modifier = Modifier.weight(1f))
                MedicalDisclaimer()
            }
        } else {
            // Chat Message Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                items(chatMessages) { (sender, text) ->
                    val isUser = sender == "user"
                    val bubbleColor = if (isUser) PrimaryEmerald else DarkSurface
                    val align = if (isUser) Alignment.End else Alignment.Start
                    val border = if (isUser) Color.Transparent else Color.White.copy(alpha = 0.05f)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.0.dp),
                        horizontalAlignment = align
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 0.dp,
                                        bottomEnd = if (isUser) 0.dp else 16.dp
                                    )
                                )
                                .background(bubbleColor)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = text,
                                color = TextWhite,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
                
                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PrimaryEmerald, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI is diagnosing...", color = TextGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Bottom Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                isRecordingVoice = !isRecordingVoice
                if (isRecordingVoice) {
                    Toast.makeText(context, "🎤 Voice recording overlay active. Speak symptoms now...", Toast.LENGTH_SHORT).show()
                    viewModel.sendChatMessage("I have a broken bone fracture in my knee joint")
                    isRecordingVoice = false
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice",
                    tint = if (isRecordingVoice) Color.Red else TextGray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Describe symptoms...", color = TextGray, fontSize = 14.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = PrimaryEmerald,
                    unfocusedBorderColor = Color.White.copy(0.1f)
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (textInput.isNotEmpty()) {
                        viewModel.sendChatMessage(textInput)
                        textInput = ""
                    }
                },
                enabled = textInput.isNotEmpty()
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = PrimaryEmerald)
            }
        }
    }
}

@Composable
fun QuickSymptomTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
