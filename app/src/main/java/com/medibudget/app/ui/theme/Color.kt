package com.medibudget.app.ui.theme

import androidx.compose.ui.graphics.Color

// Dark Premium HSL/RGB Colors
val DarkBackground = Color(0xFF0B0F19) // Very dark slate-navy
val DarkSurface = Color(0xFF152033)    // Sleek elevated navy card
val DarkSurfaceVariant = Color(0xFF1E2E4A) // Lighter navy highlights
val PrimaryEmerald = Color(0xFF10B981) // Vibrant medical emerald
val PrimaryTeal = Color(0xFF0D9488)    // Balanced dark teal
val PrimaryNeonAccent = Color(0xFF34D399) // Dynamic high-contrast green
val TextWhite = Color(0xFFF8FAFC)     // Crisp white text
val TextGray = Color(0xFF94A3B8)      // Muted slate gray text
val CustomRed = Color(0xFFEF4444)       // Critical SOS Red
val CustomGold = Color(0xFFF59E0B)      // Subheading Warning Gold
val CustomBlue = Color(0xFF3B82F6)      // Information / Blue

// Gradient Accents
val GradientTealToEmerald = listOf(PrimaryTeal, PrimaryEmerald)
val GradientNavyToSlate = listOf(DarkBackground, DarkSurface)
val GradientSOS = listOf(Color(0xFFDC2626), Color(0xFFEF4444), Color(0xFFF87171))
