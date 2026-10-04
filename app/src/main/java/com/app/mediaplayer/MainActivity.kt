package com.app.mediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private val STORAGE_PERMISSION_CODE = 100

    private val videoList = mutableListOf<MediaItemModel>()
    private val audioList = mutableListOf<MediaItemModel>()
    private val allMediaList = mutableListOf<MediaItemModel>()

    private lateinit var videoAdapter: MediaAdapter
    private lateinit var audioAdapter: MediaAdapter
    private lateinit var searchAdapter: MediaAdapter

    private lateinit var playerView: PlayerView
    private lateinit var videoRecycler: RecyclerView
    private lateinit var audioRecycler: RecyclerView
    private lateinit var searchLayout: LinearLayout
    private lateinit var settingsLayout: LinearLayout

    private lateinit var searchEditText: EditText
    private lateinit var searchRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initPlayer()
        checkPermissionsAndLoad()
        setupBottomNav()
        setupSearch()
    }

    private fun initViews() {
        playerView = findViewById(R.id.player_view)
        videoRecycler = findViewById(R.id.video_recycler)
        audioRecycler = findViewById(R.id.audio_recycler)
        searchLayout = findViewById(R.id.search_layout)
        settingsLayout = findViewById(R.id.settings_layout)

        searchEditText = findViewById(R.id.search_edit_text)
        searchRecycler = findViewById(R.id.search_recycler)

        videoRecycler.layoutManager = LinearLayoutManager(this)
        audioRecycler.layoutManager = LinearLayoutManager(this)
        searchRecycler.layoutManager = LinearLayoutManager(this)

        videoAdapter = MediaAdapter(videoList) { media -> playMedia(media) }
        audioAdapter = MediaAdapter(audioList) { media -> playMedia(media) }
        searchAdapter = MediaAdapter(emptyList()) { media -> playMedia(media) }

        videoRecycler.adapter = videoAdapter
        audioRecycler.adapter = audioAdapter
        searchRecycler.adapter = searchAdapter
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
    }

    private fun checkPermissionsAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission, Manifest.permission.READ_MEDIA_AUDIO), STORAGE_PERMISSION_CODE)
        } else {
            loadMediaFiles()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMediaFiles()
            } else {
                Toast.makeText(this, "Storage permission is required to play local files", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMediaFiles() {
        videoList.clear()
        audioList.clear()
        allMediaList.clear()

        // Load Videos
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videoProjection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME
        )
        contentResolver.query(videoUri, videoProjection, null, null, null)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val name = cursor.getString(nameCol)
                val uri = Uri.withAppendedPath(videoUri, id.toString())
                val item = MediaItemModel(id, name, uri, true)
                videoList.add(item)
                allMediaList.add(item)
            }
        }

        // Load Audio
        val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val audioProjection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME
        )
        contentResolver.query(audioUri, audioProjection, null, null, null)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val name = cursor.getString(nameCol)
                val uri = Uri.withAppendedPath(audioUri, id.toString())
                val item = MediaItemModel(id, name, uri, false)
                audioList.add(item)
                allMediaList.add(item)
            }
        }

        videoAdapter.updateList(videoList)
        audioAdapter.updateList(audioList)
    }

    private fun playMedia(media: MediaItemModel) {
        hideAllTabs()
        playerView.visibility = View.VISIBLE
        player?.setMediaItem(MediaItem.fromUri(media.uri))
        player?.prepare()
        player?.playWhenReady = true
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            hideAllTabs()
            when (item.itemId) {
                R.id.nav_video -> {
                    videoRecycler.visibility = View.VISIBLE
                    true
                }
                R.id.nav_audio -> {
                    audioRecycler.visibility = View.VISIBLE
                    true
                }
                R.id.nav_search -> {
                    searchLayout.visibility = View.VISIBLE
                    true
                }
                R.id.nav_settings -> {
                    settingsLayout.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }
    }

    private fun hideAllTabs() {
        player?.stop()
        playerView.visibility = View.GONE
        videoRecycler.visibility = View.GONE
        audioRecycler.visibility = View.GONE
        searchLayout.visibility = View.GONE
        settingsLayout.visibility = View.GONE
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                val filtered = if (query.isEmpty()) {
                    emptyList()
                } else {
                    allMediaList.filter { it.title.lowercase().contains(query) }
                }
                searchAdapter.updateList(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onBackPressed() {
        if (playerView.visibility == View.VISIBLE) {
            player?.stop()
            playerView.visibility = View.GONE
            videoRecycler.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
