package com.xrable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xrable.ui.theme.BackgroundDark
import com.xrable.ui.theme.BluePrimary
import com.xrable.ui.theme.BorderMuted
import com.xrable.ui.theme.SuccessGreen
import com.xrable.ui.theme.SurfaceDark
import com.xrable.ui.theme.SurfaceDarker
import com.xrable.ui.theme.TextMuted
import com.xrable.ui.theme.TextPrimary
import com.xrable.ui.theme.TextSecondary
import com.xrable.ui.theme.XRABLETheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XRABLETheme {
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(2500)
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                } else {
                    MainApp()
                }
            }
        }
    }
}

private enum class ScreenTab(val label: String) {
    Connection("Connection"),
    Session("Session"),
    Modules("Modules"),
    System("System")
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        val transition = rememberInfiniteTransition(label = "splash")
        val dotAlpha by transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(900),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dotAlpha"
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SKADI",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontSize = 34.sp,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "powered by XRABLE",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(dotAlpha)
                        .background(BluePrimary, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun MainApp() {
    var selectedTab by remember { mutableStateOf(ScreenTab.Connection) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                ScreenTab.Connection -> ConnectionScreen()
                ScreenTab.Session -> SessionScreen()
                ScreenTab.Modules -> ModulesScreen()
                ScreenTab.System -> SystemScreen()
            }
        }
        BottomNav(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
    }
}

@Composable
private fun BottomNav(
    selectedTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDark)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScreenTab.values().forEach { tab ->
            val selected = tab == selectedTab
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable { onTabSelected(tab) },
                color = if (selected) BluePrimary else SurfaceDark,
                contentColor = if (selected) TextPrimary else TextSecondary,
                shape = RoundedCornerShape(24.dp),
                border = if (selected) null else BorderStroke(1.dp, BorderMuted)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        color = if (selected) TextPrimary else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectionScreen() {
    ScreenScaffold(
        title = "Device Connection",
        footerText = "Last Event: Session Ready"
    ) {
        InfoCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(SuccessGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Status: Connected",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Device: SKADI AR Glasses",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Protocol: XRABLE v0.1",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(26.dp))
        PrimaryButton(text = "Connect Device")
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryButton(text = "Disconnect")
    }
}

@Composable
private fun SessionScreen() {
    ScreenScaffold(title = "XR Session") {
        InfoCard {
            val annotated = buildAnnotatedString {
                append("Session State: ")
                withStyle(style = SpanStyle(color = SuccessGreen)) {
                    append("Active")
                }
            }
            Text(
                text = annotated,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Mode: Enterprise",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Latency: 12 ms",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(26.dp))
        PrimaryButton(text = "Start XR Session")
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryButton(text = "Stop XR Session")
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Events",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))
        InfoCard {
            Text(
                text = "• Device Ready",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "• Sensors Active",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun ModulesScreen() {
    ScreenScaffold(title = "Modules") {
        ModuleCard(title = "Navigation Assistant")
        Spacer(modifier = Modifier.height(16.dp))
        ModuleCard(title = "Voice Assistant")
        Spacer(modifier = Modifier.height(16.dp))
        ModuleCard(title = "AR Instructions")
    }
}

@Composable
private fun SystemScreen() {
    ScreenScaffold(title = "System Info") {
        InfoCard {
            Text(
                text = "Standard: XRABLE v0.1",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Device: SKADI",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Platform: Android",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mode: Demo",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(26.dp))
        PrimaryButton(text = "View Logs")
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryButton(text = "Export Diagnostics")
    }
}

@Composable
private fun ScreenScaffold(
    title: String,
    footerText: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 30.dp)
    ) {
        Text(
            text = "XRABLE",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(22.dp))
        content()
        if (footerText != null) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = footerText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceDarker,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
private fun ModuleCard(title: String) {
    InfoCard {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Status: Available",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
    }
}

@Composable
private fun PrimaryButton(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable { },
        shape = RoundedCornerShape(30.dp),
        color = BluePrimary
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SecondaryButton(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable { },
        shape = RoundedCornerShape(30.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, BorderMuted)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
