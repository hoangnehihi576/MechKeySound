package com.example.mechkeysound

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.SoundPool
import android.preference.PreferenceManager
import android.view.InputDevice
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class MechKeySoundService : AccessibilityService() {

    private lateinit var soundPool: SoundPool
    private val soundIds = mutableListOf<Int>()
    private lateinit var prefs: SharedPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()

        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        serviceInfo = info

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(attrs)
            .build()

        soundIds.add(soundPool.load(this, R.raw.key1, 1))
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val isVirtual = event.deviceId == -1 ||
                event.source and InputDevice.SOURCE_KEYBOARD == 0
        if (event.action == KeyEvent.ACTION_DOWN && !isVirtual) {
            playRandomSound()
        }
        return false
    }

    private fun playRandomSound() {
        if (soundIds.isEmpty()) return
        val id = soundIds.random()
        val volumePercent = prefs.getInt("volume", 80)
        val volume = volumePercent / 100f
        soundPool.play(id, volume, volume, 1, 0, 1f)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        if (::soundPool.isInitialized) soundPool.release()
    }
}
