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

/**
 * AccessibilityService bắt mọi sự kiện phím vật lý (Bluetooth / USB / OTG)
 * gửi tới thiết bị, rồi phát một âm thanh phím cơ ngẫu nhiên mỗi khi có phím
 * được nhấn xuống. Sự kiện bàn phím ảo (Gboard...) sẽ bị bỏ qua vì
 * deviceId của bàn phím ảo là KeyCharacterMap.VIRTUAL_KEYBOARD (-1).
 *
 * Cần được người dùng bật thủ công trong:
 * Settings > Accessibility > Đã tải xuống > MechKeySound
 */
class MechKeySoundService : AccessibilityService() {

    private lateinit var soundPool: SoundPool
    private val soundIds = mutableListOf<Int>()
    private lateinit var prefs: SharedPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()

        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        // Bắt buộc để hệ thống cho phép service này nhận KeyEvent
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

        // Bỏ file âm thanh của bạn vào res/raw với tên key1.mp3, key2.mp3, key3.mp3, key4.mp3
        // (xem README.md để biết chỗ lấy sound pack mechanical keyboard miễn phí, hợp pháp)
        val resIds = listOf(R.raw.key1, R.raw.key2, R.raw.key3, R.raw.key4)
        for (id in resIds) {
            soundIds.add(soundPool.load(this, id, 1))
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // Chỉ phát tiếng khi phím đến từ thiết bị vật lý thật (Bluetooth/USB),
        // không phải bàn phím ảo trên màn hình.
        val isVirtual = event.deviceId == -1 ||
                event.source and InputDevice.SOURCE_KEYBOARD == 0
        if (event.action == KeyEvent.ACTION_DOWN && !isVirtual) {
            playRandomSound()
        }
        // return false: không "nuốt" phím, để hệ thống vẫn xử lý gõ chữ bình thường
        return false
    }

    private fun playRandomSound() {
        if (soundIds.isEmpty()) return
        val id = soundIds.random()
        val volumePercent = prefs.getInt("volume", 80)
        val volume = volumePercent / 100f
        soundPool.play(id, volume, volume, 1, 0, 1f)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Không cần xử lý gì ở đây, ta chỉ quan tâm onKeyEvent
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        if (::soundPool.isInitialized) soundPool.release()
    }
}
