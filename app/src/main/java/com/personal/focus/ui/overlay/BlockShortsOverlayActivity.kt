package com.personal.focus.ui.overlay

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.focus.FocusApplication
import com.personal.focus.ui.theme.DarkSurface
import com.personal.focus.ui.theme.DimTransparent80
import com.personal.focus.ui.theme.FocusTheme
import com.personal.focus.ui.theme.NavBarColor
import com.personal.focus.ui.theme.Neutral100
import com.personal.focus.ui.theme.Neutral90
import com.personal.focus.ui.theme.PrimaryGreen

/**
 * High-friction overlay activity triggered when YouTube Shorts or infinite Home feeds
 * are detected in YouTube Study Mode.
 */
class BlockShortsOverlayActivity : ComponentActivity() {

    private val diversionTypeState = mutableStateOf(DIVERSION_SHORTS)

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            try {
                val config = Configuration(newBase.resources.configuration)
                config.fontScale = 1.0f
                super.attachBaseContext(newBase.createConfigurationContext(config))
                return
            } catch (_: Throwable) {
            }
        }
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FocusApplication.ensureInitialized(this)
        AppBlockOverlayManager.dismissOverlay()
        configureWindow()
        diversionTypeState.value = intent?.getStringExtra(EXTRA_DIVERSION_TYPE) ?: DIVERSION_SHORTS

        setContent {
            FocusTheme {
                val diversionType by diversionTypeState
                BlockShortsScreen(
                    diversionType = diversionType,
                    onDismiss = {
                        AppBlockOverlayManager.dismissOverlay()
                        finish()
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        AppBlockOverlayManager.dismissOverlay()
        diversionTypeState.value = intent.getStringExtra(EXTRA_DIVERSION_TYPE) ?: DIVERSION_SHORTS
    }

    private fun configureWindow() {
        try {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
            window.navigationBarColor = NavBarColor.toArgb()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
            } else {
                @Suppress("DEPRECATION")
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                )
            }
        } catch (_: Throwable) {
        }
    }

    companion object {
        const val EXTRA_DIVERSION_TYPE = "extra_diversion_type"
        const val DIVERSION_SHORTS = "DIVERSION_SHORTS"
        const val DIVERSION_HOME_FEED = "DIVERSION_HOME_FEED"
    }
}

@Composable
fun BlockShortsScreen(
    diversionType: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DimTransparent80)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (diversionType == BlockShortsOverlayActivity.DIVERSION_SHORTS) {
                        "Shorts Blocked"
                    } else {
                        "Feed Diverted"
                    },
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (diversionType == BlockShortsOverlayActivity.DIVERSION_SHORTS) {
                        "YouTube Study Mode is active. Protect your attention from addictive short-form algorithmic reels."
                    } else {
                        "YouTube Home recommendation feed is paused to prevent algorithmic rabbit holes during study hours."
                    },
                    color = Neutral100,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Search specifically for your study material or use whitelisted channels.",
                    color = Neutral90,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Back to Study",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
