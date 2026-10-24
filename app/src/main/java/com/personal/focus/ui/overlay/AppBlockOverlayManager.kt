package com.personal.focus.ui.overlay

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.personal.focus.FocusApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * High-priority WindowManager Overlay Manager that renders an instant, full-screen
 * blocking barrier directly via WindowManager (TYPE_APPLICATION_OVERLAY).
 *
 * This completely bypasses Android 14+ Background Activity Launch (BAL) restrictions,
 * ensuring immediate interception whenever blocked apps are accessed.
 */
object AppBlockOverlayManager {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    @Volatile
    private var isShowing: Boolean = false

    @Volatile
    private var currentPackage: String? = null

    private var overlayView: View? = null
    private var windowManager: WindowManager? = null

    private var reasonTextView: TextView? = null
    private var timerTextView: TextView? = null
    private var timerContainer: LinearLayout? = null

    fun isShowing(): Boolean = isShowing

    fun getCurrentPackage(): String? = currentPackage

    /**
     * Instantly draws the full-screen blocking overlay via WindowManager.
     * Can be invoked safely from background threads or services.
     */
    fun showOverlay(context: Context, targetPackage: String, blockReason: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            return
        }

        mainHandler.post {
            try {
                val targetContext = if (context is android.app.Service || context is android.app.Activity) context else context.applicationContext
                val wm = (context.getSystemService(Context.WINDOW_SERVICE)
                    ?: context.applicationContext.getSystemService(Context.WINDOW_SERVICE)) as? WindowManager
                    ?: return@post
                windowManager = wm

                if (isShowing && overlayView != null) {
                    // Already showing - update existing overlay view text
                    currentPackage = targetPackage
                    updateTexts(targetContext, targetPackage, blockReason)
                    return@post
                }

                currentPackage = targetPackage
                val view = createOverlayView(targetContext, targetPackage, blockReason)
                overlayView = view

                val layoutParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    } else {
                        @Suppress("DEPRECATION")
                        WindowManager.LayoutParams.TYPE_PHONE
                    },
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    PixelFormat.TRANSLUCENT
                ).apply {
                    gravity = Gravity.CENTER
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                    }
                }

                wm.addView(view, layoutParams)
                isShowing = true
                startCountdownTicker()
            } catch (e: Exception) {
                android.util.Log.e("AppBlockOverlayManager", "Failed to add WindowManager overlay view", e)
            }
        }
    }

    /**
     * Safely dismisses the overlay and cleans up view hierarchy.
     */
    fun dismissOverlay() {
        mainHandler.post {
            stopCountdownTicker()
            try {
                if (isShowing && overlayView != null && windowManager != null) {
                    windowManager?.removeViewImmediate(overlayView)
                }
            } catch (e: Exception) {
                try {
                    windowManager?.removeView(overlayView)
                } catch (ignored: Exception) {}
            } finally {
                overlayView = null
                isShowing = false
                currentPackage = null
                reasonTextView = null
                timerTextView = null
                timerContainer = null
            }
        }
    }

    private fun createOverlayView(context: Context, targetPackage: String, blockReason: String): View {
        val root = FrameLayout(context).apply {
            setBackgroundColor(Color.parseColor("#E60D0F11")) // 90% opaque dark
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    returnToHomeScreen(context)
                    true
                } else {
                    false
                }
            }
        }

        // Inner Card Container
        val cardLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            val bgDrawable = GradientDrawable().apply {
                cornerRadius = dpToPx(context, 24f)
                setColor(Color.parseColor("#16191E"))
            }
            background = bgDrawable
            val pad = dpToPx(context, 24f).toInt()
            setPadding(pad, pad, pad, pad)
        }

        val cardParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
            val margin = dpToPx(context, 24f).toInt()
            setMargins(margin, margin, margin, margin)
        }

        // Title: "App Blocked"
        val titleView = TextView(context).apply {
            text = "App Blocked"
            setTextColor(Color.parseColor("#00E575")) // PrimaryGreen
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        cardLayout.addView(titleView)

        // Reason Text
        val reasonView = TextView(context).apply {
            setTextColor(Color.parseColor("#FFFFFF"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            gravity = Gravity.CENTER
            val topMargin = dpToPx(context, 12f).toInt()
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, topMargin, 0, 0) }
            layoutParams = lp
        }
        reasonTextView = reasonView
        cardLayout.addView(reasonView)

        // Timer Container
        val tContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            val topMargin = dpToPx(context, 20f).toInt()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, topMargin, 0, 0) }
        }

        val timerLabel = TextView(context).apply {
            text = "Time Remaining"
            setTextColor(Color.parseColor("#8E95A3"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            gravity = Gravity.CENTER
        }
        tContainer.addView(timerLabel)

        val tView = TextView(context).apply {
            text = "00:00"
            setTextColor(Color.parseColor("#00E575"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        timerTextView = tView
        tContainer.addView(tView)
        timerContainer = tContainer
        cardLayout.addView(tContainer)

        // Return to Focus Button
        val returnButton = Button(context).apply {
            text = "Return to Focus"
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            val btnBg = GradientDrawable().apply {
                cornerRadius = dpToPx(context, 12f)
                setColor(Color.parseColor("#00E575"))
            }
            background = btnBg
            val topMargin = dpToPx(context, 24f).toInt()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(context, 52f).toInt()
            ).apply { setMargins(0, topMargin, 0, 0) }
            setOnClickListener {
                returnToHomeScreen(context)
            }
        }
        cardLayout.addView(returnButton)

        root.addView(cardLayout, cardParams)
        updateTexts(context, targetPackage, blockReason)
        return root
    }

    private fun updateTexts(context: Context, targetPackage: String, blockReason: String) {
        val description = when (blockReason) {
            OverlayActivity.REASON_FOCUS_ACTIVE -> "This app is restricted during your active Ultra Focus session."
            OverlayActivity.REASON_LIMIT_EXCEEDED -> "Daily screentime limit reached for $targetPackage."
            OverlayActivity.REASON_APP_BLOCKED -> "$targetPackage is blocked by your active app rules."
            else -> blockReason
        }
        reasonTextView?.text = description
        updateTimerDisplay()
    }

    private fun updateTimerDisplay() {
        val remainingMillis = com.personal.focus.data.repository.FocusStateRepository.remainingTimeMillis.value
            .takeIf { it > 0L }
            ?: FocusApplication.instanceOrNull?.focusTimerRepository?.remainingTimeMillis?.value
            ?: 0L
        if (remainingMillis > 0L) {
            val minutes = (remainingMillis / 1000L) / 60
            val seconds = (remainingMillis / 1000L) % 60
            timerTextView?.text = String.format("%02d:%02d", minutes, seconds)
            timerContainer?.visibility = View.VISIBLE
        } else {
            timerContainer?.visibility = View.GONE
        }
    }

    private fun startCountdownTicker() {
        stopCountdownTicker()
        timerJob = scope.launch {
            while (isActive && isShowing) {
                updateTimerDisplay()
                delay(1000L)
            }
        }
    }

    private fun stopCountdownTicker() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun returnToHomeScreen(context: Context) {
        dismissOverlay()
        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(homeIntent)
        } catch (e: Exception) {
            // Failsafe
        }
    }

    private fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }
}
