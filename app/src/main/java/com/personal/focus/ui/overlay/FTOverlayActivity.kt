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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.focus.FocusApplication
import com.personal.focus.data.repository.FocusStateRepository
import com.personal.focus.ui.HomePageActivity
import com.personal.focus.ui.theme.DarkSurface
import com.personal.focus.ui.theme.DimTransparent
import com.personal.focus.ui.theme.FocusTheme
import com.personal.focus.ui.theme.GiantTimerTextStyle
import com.personal.focus.ui.theme.NavBarColor
import com.personal.focus.ui.theme.Neutral100
import com.personal.focus.ui.theme.Neutral90
import com.personal.focus.ui.theme.PrimaryGreen
import com.personal.focus.ui.theme.SecondaryOrange

/**
 * Lock-in Focus Session Overlay presented when Home button navigation
 * is trapped during strict focus sessions.
 */
class FTOverlayActivity : ComponentActivity() {

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
        configureWindow()

        setContent {
            FocusTheme {
                FTOverlayScreen(
                    onDismiss = { returnToFocus() }
                )
            }
        }
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

    private fun returnToFocus() {
        try {
            val intent = Intent(this, HomePageActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
        } catch (_: Throwable) {
        }
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        returnToFocus()
    }
}

@Composable
fun FTOverlayScreen(
    onDismiss: () -> Unit
) {
    val session by FocusStateRepository.activeSession.collectAsState()
    val remainingMillis by FocusStateRepository.remainingTimeMillis.collectAsState()

    val minutes = (remainingMillis / 1000L) / 60
    val seconds = (remainingMillis / 1000L) % 60
    val formattedTimer = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DimTransparent)
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
                    text = "HOME SCREEN LOCKED",
                    color = SecondaryOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (session?.isInBreak == true) "Break In Progress" else "Ultra Focus Active",
                    color = Neutral100,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = formattedTimer,
                    style = GiantTimerTextStyle,
                    color = PrimaryGreen
                )

                if (session != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Round ${session?.currentRound} / ${session?.roundsPerCycle}",
                        color = Neutral90,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Home navigation and app switching are disabled during your focus commitment.",
                    color = Neutral90,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continue Focusing",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
