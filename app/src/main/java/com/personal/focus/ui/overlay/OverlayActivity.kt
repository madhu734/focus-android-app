package com.personal.focus.ui.overlay

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
import com.personal.focus.ui.theme.DarkSurface
import com.personal.focus.ui.theme.DimTransparent
import com.personal.focus.ui.theme.GiantTimerTextStyle
import com.personal.focus.ui.theme.NavBarColor
import com.personal.focus.ui.theme.Neutral100
import com.personal.focus.ui.theme.Neutral90
import com.personal.focus.ui.theme.PrimaryGreen
import com.personal.focus.ui.theme.FocusTheme

/**
 * High-priority translucent overlay activity intercepting blocked app launches.
 */
class OverlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppBlockOverlayManager.dismissOverlay()
        configureWindow()

        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: "Distracting App"
        val blockReason = intent.getStringExtra(EXTRA_BLOCK_REASON) ?: REASON_FOCUS_ACTIVE

        setContent {
            FocusTheme {
                OverlayScreen(
                    packageName = packageName,
                    blockReason = blockReason,
                    onReturnHome = { returnToHomeScreen() }
                )
            }
        }
    }

    private fun configureWindow() {
        val configuration = Configuration(resources.configuration)
        configuration.fontScale = 1.0f
        applyOverrideConfiguration(configuration)

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
    }

    private fun returnToHomeScreen() {
        AppBlockOverlayManager.dismissOverlay()
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        returnToHomeScreen()
    }

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_BLOCK_REASON = "extra_block_reason"

        const val REASON_FOCUS_ACTIVE = "FOCUS_SESSION_ACTIVE"
        const val REASON_LIMIT_EXCEEDED = "DAILY_LIMIT_EXCEEDED"
        const val REASON_APP_BLOCKED = "APP_BLOCKED"
    }
}

@Composable
fun OverlayScreen(
    packageName: String,
    blockReason: String,
    onReturnHome: () -> Unit
) {
    val app = try { FocusApplication.instance } catch (e: Exception) { null }
    val remainingMillis by (app?.focusTimerRepository?.remainingTimeMillis?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0L) })

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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "App Blocked",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when (blockReason) {
                        OverlayActivity.REASON_FOCUS_ACTIVE -> "This app is restricted during your active Ultra Focus session."
                        OverlayActivity.REASON_LIMIT_EXCEEDED -> "Daily screentime limit reached for $packageName."
                        OverlayActivity.REASON_APP_BLOCKED -> "$packageName is blocked by your active app rules."
                        else -> blockReason
                    },
                    color = Neutral100,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                if (remainingMillis > 0L) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Time Remaining",
                        color = Neutral90,
                        fontSize = 13.sp
                    )
                    Text(
                        text = formattedTimer,
                        style = GiantTimerTextStyle,
                        color = PrimaryGreen
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onReturnHome,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Return to Focus",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
