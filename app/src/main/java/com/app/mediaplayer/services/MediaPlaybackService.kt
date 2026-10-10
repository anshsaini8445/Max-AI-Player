package com.app.mediaplayer.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.app.mediaplayer.MainActivity
import com.app.mediaplayer.R

class MediaPlaybackService : Service() {

    private var player: ExoPlayer? = null
    private val CHANNEL_ID = "media_playback_channel"
    private val NOTIFICATION_ID = 1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        player = ExoPlayer.Builder(this).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }
        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            ACTION_PLAY -> {
                val uri = intent.getStringExtra(EXTRA_URI)
                uri?.let { playMedia(it) }
            }
            ACTION_PAUSE -> pauseMedia()
            ACTION_STOP -> stopMedia()
            ACTION_NEXT -> playNext()
            ACTION_PREV -> playPrevious()
        }
    }

    private fun playMedia(uri: String) {
        try {
            val mediaItem = MediaItem.fromUri(uri)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
            showNotification()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pauseMedia() {
        player?.pause()
        updateNotification()
    }

    private fun stopMedia() {
        player?.stop()
        player?.clearMediaItems()
        stopForeground(true)
    }

    private fun playNext() {
        // Implement next track logic
    }

    private fun playPrevious() {
        // Implement previous track logic
    }

    private fun showNotification() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateNotification() {
        val notification = createNotification()
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playPauseIntent = Intent(this, MediaPlaybackService::class.java).apply {
            action = if (player?.isPlaying == true) ACTION_PAUSE else ACTION_PLAY
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, MediaPlaybackService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Max AI Player")
            .setContentText("Now Playing")
            .setSmallIcon(R.drawable.ic_max_logo)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_media_previous, "Previous", null)
            .addAction(
                if (player?.isPlaying == true) 
                    R.drawable.ic_media_pause 
                else 
                    R.drawable.ic_media_play,
                if (player?.isPlaying == true) "Pause" else "Play",
                playPausePendingIntent
            )
            .addAction(R.drawable.ic_media_next, "Next", null)
            .addAction(R.drawable.ic_notification_clear_all, "Stop", stopPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media playback notifications"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    companion object {
        const val ACTION_PLAY = "com.app.mediaplayer.ACTION_PLAY"
        const val ACTION_PAUSE = "com.app.mediaplayer.ACTION_PAUSE"
        const val ACTION_STOP = "com.app.mediaplayer.ACTION_STOP"
        const val ACTION_NEXT = "com.app.mediaplayer.ACTION_NEXT"
        const val ACTION_PREV = "com.app.mediaplayer.ACTION_PREV"
        const val EXTRA_URI = "com.app.mediaplayer.EXTRA_URI"
    }
}
