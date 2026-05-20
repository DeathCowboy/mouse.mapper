package com.example.mousemapper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class MouseMappingService : AccessibilityService() {

    private val TAG = "MouseMapperService"

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Mouse Mapping Service Connected Successfully!")
    }

    // This intercepts physical hardware buttons (including mouse/keyboard inputs)
    override fun onKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val action = event.action

        // We only want to trigger our touch mapping on a key DOWN (press) action
        if (action == KeyEvent.ACTION_DOWN) {
            Log.d(TAG, "Intercepted hardware keycode: $keyCode")

            // Example Mapping: Let's assume Keycode 100 is your mouse side-button.
            // When pressed, it will fake a screen touch at coordinates X=500, Y=1200.
            if (keyCode == 100) { 
                triggerVirtualTouch(500f, 1200f)
                return true // Return true to consume the event so nothing else handles it
            }
        }

        // Return false to let standard system buttons (like Volume/Power) pass through normally
        return super.onKeyEvent(event) 
    }

    // The engine that simulates the physical fingertip touch
    private fun triggerVirtualTouch(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
        }
        
        // Duration of 50ms simulates a swift, clean tap
        val stroke = GestureDescription.StrokeDescription(path, 0, 50)
        val gestureBuilder = GestureDescription.Builder().apply {
            addStroke(stroke)
        }

        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d(TAG, "Virtual touch successfully executed at ($x, $y)")
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.e(TAG, "Virtual touch gesture was cancelled.")
            }
        }, null)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We can leave this blank. It's used for tracking UI changes (like window swaps), 
        // but we are strictly focusing on raw hardware KeyEvents.
    }

    override fun onInterrupt() {
        Log.w(TAG, "Service Interrupted")
    }
}
