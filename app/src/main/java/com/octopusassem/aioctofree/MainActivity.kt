package com.octopusassem.aioctofree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import com.octopusassem.aioctofree.ui.AppRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

private val Dark = darkColorScheme(
    primary = Color(0xFF46D9FF),
    onPrimary = Color(0xFF04111D),
    secondary = Color(0xFFC084FC),
    tertiary = Color(0xFFFF6B8A),
    background = Color(0xFF07111F),
    surface = Color(0xFF101A2B),
    surfaceVariant = Color(0xFF1B2940),
    onBackground = Color(0xFFE5E7EB),
    onSurface = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF34415C),
    error = Color(0xFFF87171),
)

private val Light = lightColorScheme(
    primary = Color(0xFF0075A8),
    onPrimary = Color.White,
    secondary = Color(0xFF6846D9),
    tertiary = Color(0xFFD73E67),
    background = Color(0xFFF2F8FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8F2FC),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFD2E1EF),
    error = Color(0xFFDC2626),
)

@Composable
fun AiOctofreeTheme(themeMode: String, content: @Composable () -> Unit) {
    val dark = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (dark) Dark else Light,
        content = content,
    )
}
