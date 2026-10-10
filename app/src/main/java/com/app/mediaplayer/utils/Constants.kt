package com.app.mediaplayer.utils

object Constants {
    const val PRIVACY_FOLDER_NAME = "MaxAI_Private"
    const val BEATS_FOLDER_NAME = "Beats"
    const val DATABASE_NAME = "max_ai_player_db"
    const val PREF_NAME = "max_ai_player_prefs"
    
    // Settings Keys
    const val KEY_DARK_THEME = "dark_theme"
    const val KEY_BACKGROUND_PLAY = "background_play"
    const val KEY_HARDWARE_ACCEL = "hardware_accel"
    const val KEY_AUTO_PLAY = "auto_play"
    const val KEY_PRIVATE_FOLDER = "private_folder"
    const val KEY_HIDE_NOTIFICATION = "hide_notification"
    const val KEY_LOCK_SCREEN_CONTROLS = "lock_screen_controls"
    
    // Notification
    const val NOTIFICATION_CHANNEL_ID = "max_ai_player_channel"
    const val NOTIFICATION_ID = 1001
    
    // Request Codes
    const val REQUEST_CODE_STORAGE = 100
    const val REQUEST_CODE_AUDIO = 101
    const val REQUEST_CODE_VIDEO = 102
    
    // Media Types
    const val MEDIA_TYPE_AUDIO = 0
    const val MEDIA_TYPE_VIDEO = 1
    const val MEDIA_TYPE_BEATS = 2
}
