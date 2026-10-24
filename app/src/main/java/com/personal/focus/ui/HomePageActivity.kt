package com.personal.focus.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.focus.services.MyMonitorService
import com.personal.focus.ui.screens.BlocksScreen
import com.personal.focus.ui.screens.FocusScreen
import com.personal.focus.ui.screens.StatsScreen
import com.personal.focus.ui.theme.CabinetGroteskFamily
import com.personal.focus.ui.theme.NavBarColor
import com.personal.focus.ui.theme.Neutral0
import com.personal.focus.ui.theme.Neutral90
import com.personal.focus.ui.theme.PrimaryGreen
import com.personal.focus.ui.theme.FocusTheme

enum class FocusTab(val title: String, val badge: String) {
    FOCUS("Focus", "⚡"),
    BLOCKS("Blocks", "🛡"),
    STATS("Stats", "📊")
}

/**
 * Main Navigation Dashboard Activity for Focus
 */
open class HomePageActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.personal.focus.FocusApplication.ensureInitialized(this)

        // Ensure background watchdog service is active
        try {
            MyMonitorService.startService(this)
        } catch (e: Throwable) {
            // Failsafe on restricted permission state
        }

        setContent {
            FocusTheme {
                MainAppScaffold()
            }
        }
    }
}

@Composable
fun MainAppScaffold() {
    var selectedTab by remember { mutableStateOf(FocusTab.FOCUS) }

    Scaffold(
        containerColor = Neutral0,
        bottomBar = {
            FocusBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "tabTransition") { tab ->
                when (tab) {
                    FocusTab.FOCUS -> FocusScreen()
                    FocusTab.BLOCKS -> BlocksScreen()
                    FocusTab.STATS -> StatsScreen()
                }
            }
        }
    }
}

@Composable
fun FocusBottomNavigationBar(
    selectedTab: FocusTab,
    onTabSelected: (FocusTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(NavBarColor)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FocusTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tab.badge,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = tab.title,
                        fontFamily = CabinetGroteskFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PrimaryGreen else Neutral90,
                        fontSize = 15.sp
                    )
                }

                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 24.dp, height = 3.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen)
                    )
                }
            }
        }
    }
}
