package com.app.mediaplayer

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mediaplayer.adapters.VideoAdapter
import com.app.mediaplayer.models.MediaItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var videoRecycler: RecyclerView
    private lateinit var playerView: PlayerView
    private lateinit var bottomNav: BottomNavigationView

    private val videoList = mutableListOf<MediaItem>()
    private lateinit var videoAdapter: VideoAdapter
    private var player: ExoPlayer? = null

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoRecycler = findViewById(R.id.video_recycler)
        playerView = findViewById(R.id.player_view)
        bottomNav = findViewById(R.id.bottom_nav)

        setupRecyclerView()
        setupBottomNav()
        setupBackPress()
        checkPermissionAndLoadVideos()
    }

    private fun setupRecyclerView() {
        videoAdapter = VideoAdapter(videoList) { video ->
            playVideo(video)
        }
        videoRecycler.layoutManager = LinearLayoutManager(this)
        videoRecycler.adapter = videoAdapter
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_video -> {
                    if (playerView.visibility == View.VISIBLE) {
                        stopPlayer()
                    }
                    videoRecycler.visibility = View.VISIBLE
                    true
                }
                R.id.nav_music, R.id.nav_effects, R.id.nav_game, R.id.nav_me -> {
                    Toast.makeText(this, "${item.title} tab coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun checkPermissionAndLoadVideos() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadLocalVideos()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadLocalVideos()
        } else {
            Toast.makeText(this, "Storage permission is required to load videos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadLocalVideos() {
        videoList.clear()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        try {
            contentResolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn) ?: "Untitled"
                    val duration = cursor.getLong(durationColumn)
                    val size = cursor.getLong(sizeColumn)
                    val contentUri = ContentUris.withAppendedId(uri, id)

                    videoList.add(
                        MediaItem(
                            id = id,
                            title = name,
                            uri = contentUri,
                            isVideo = true,
                            duration = duration,
                            size = size
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        videoAdapter.notifyDataSetChanged()
    }

    private fun playVideo(video: MediaItem) {
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
            playerView.player = player
        }

        playerView.visibility = View.VISIBLE
        val mediaItem = ExoMediaItem.fromUri(video.uri)
        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    private fun stopPlayer() {
        player?.stop()
        playerView.visibility = View.GONE
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (playerView.visibility == View.VISIBLE) {
                    stopPlayer()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}