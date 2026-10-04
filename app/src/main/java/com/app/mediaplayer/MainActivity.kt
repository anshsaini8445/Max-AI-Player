package com.app.mediaplayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val playerView = findViewById<PlayerView>(R.id.player_view)
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        player?.setMediaItem(MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"))
        player?.prepare()
        player?.playWhenReady = true

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_video -> Toast.makeText(this, "Video Library Active", Toast.LENGTH_SHORT).show()
                R.id.nav_audio -> Toast.makeText(this, "Audio Player Section", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Settings & Equalizer", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
    override fun onStop() { super.onStop(); player?.pause() }
    override fun onDestroy() { super.onDestroy(); player?.release() }
}
