package com.app.mediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mediaplayer.R
import com.app.mediaplayer.adapters.AudioAdapter
import com.app.mediaplayer.adapters.BeatsAdapter
import com.app.mediaplayer.adapters.MediaAdapter
import com.app.mediaplayer.adapters.PlayQueueAdapter
import com.app.mediaplayer.models.BeatsItem
import com.app.mediaplayer.models.MediaItem
import com.app.mediaplayer.models.PlayQueueItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private val STORAGE_PERMISSION_CODE = 100

    // Views
    private lateinit var videoRecycler: RecyclerView
    private lateinit var audioRecycler: RecyclerView
    private lateinit var beatsRecycler: RecyclerView
    private lateinit var searchLayout: LinearLayout
    private lateinit var settingsLayout: ScrollView
    private lateinit var playerView: PlayerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var searchEditText: EditText
    private lateinit var searchRecycler: RecyclerView
    private lateinit var miniPlayerLayout: LinearLayout
    private lateinit var fullPlayerLayout: View
    private lateinit var searchBarLayout: View
    private lateinit var searchClear: ImageView

    // Adapters
    private lateinit var videoAdapter: MediaAdapter
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var beatsAdapter: BeatsAdapter
    private lateinit var searchAdapter: MediaAdapter
    private lateinit var queueAdapter: PlayQueueAdapter

    // Data
    private val videoList = mutableListOf<MediaItem>()
    private val audioList = mutableListOf<MediaItem>()
    private val beatsList = mutableListOf<BeatsItem>()
    private val searchList = mutableListOf<MediaItem>()
    private val playQueue = mutableListOf<PlayQueueItem>()

    // State
    private var currentPlayingIndex = -1
    private var isFullScreenPlayer = false
    private var currentMediaItem: MediaItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupAdapters()
        setupBottomNav()
        setupSearch()
        setupMiniPlayer()
        setupFullPlayer()
        checkPermissionAndLoadMedia()
        loadBeats()
    }

    private fun initializeViews() {
        videoRecycler = findViewById(R.id.video_recycler)
        audioRecycler = findViewById(R.id.audio_recycler)
        beatsRecycler = findViewById(R.id.beats_recycler)
        searchLayout = findViewById(R.id.search_layout)
        settingsLayout = findViewById(R.id.settings_layout)
        playerView = findViewById(R.id.player_view)
        bottomNav = findViewById(R.id.bottom_nav)
        searchEditText = findViewById(R.id.search_edit_text)
        searchRecycler = findViewById(R.id.search_recycler)
        miniPlayerLayout = findViewById(R.id.mini_player_layout)
        fullPlayerLayout = findViewById(R.id.full_player_layout)
        searchBarLayout = findViewById(R.id.search_bar_layout)
        searchClear = findViewById(R.id.search_clear)
    }

    private fun setupAdapters() {
        // Video Adapter
        videoRecycler.layoutManager = LinearLayoutManager(this)
        videoAdapter = MediaAdapter(videoList, 
            onItemClick = { media -> playMedia(media) },
            onOverflowClick = { media, view -> showMediaOverflowMenu(media, view) }
        )
        videoRecycler.adapter = videoAdapter

        // Audio Adapter
        audioRecycler.layoutManager = LinearLayoutManager(this)
        audioAdapter = AudioAdapter(audioList,
            onItemClick = { media -> playMedia(media) },
            onOverflowClick = { media, view -> showMediaOverflowMenu(media, view) }
        )
        audioRecycler.adapter = audioAdapter

        // Beats Adapter
        beatsRecycler.layoutManager = LinearLayoutManager(this)
        beatsAdapter = BeatsAdapter(beatsList) { beats -> playBeats(beats) }
        beatsRecycler.adapter = beatsAdapter

        // Search Adapter
        searchRecycler.layoutManager = LinearLayoutManager(this)
        searchAdapter = MediaAdapter(searchList,
            onItemClick = { media -> playMedia(media) },
            onOverflowClick = { media, view -> showMediaOverflowMenu(media, view) }
        )
        searchRecycler.adapter = searchAdapter

        // Queue Adapter
        val queueRecycler = findViewById<RecyclerView>(R.id.queue_recycler)
        queueRecycler.layoutManager = LinearLayoutManager(this)
        queueAdapter = PlayQueueAdapter(playQueue,
            onItemClick = { queueItem -> playQueueItem(queueItem) },
            onRemoveClick = { queueItem -> removeFromQueue(queueItem) }
        )
        queueRecycler.adapter = queueAdapter
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            hideAllContent()

            when (item.itemId) {
                R.id.nav_video -> videoRecycler.visibility = View.VISIBLE
                R.id.nav_audio -> audioRecycler.visibility = View.VISIBLE
                R.id.nav_beats -> beatsRecycler.visibility = View.VISIBLE
                R.id.nav_settings -> settingsLayout.visibility = View.VISIBLE
            }
            true
        }

        // Select video tab by default
        bottomNav.selectedItemId = R.id.nav_video
    }

    private fun hideAllContent() {
        videoRecycler.visibility = View.GONE
        audioRecycler.visibility = View.GONE
        beatsRecycler.visibility = View.GONE
        searchLayout.visibility = View.GONE
        settingsLayout.visibility = View.GONE
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                searchList.clear()
                if (query.isNotEmpty()) {
                    searchList.addAll(videoList.filter { it.title.lowercase().contains(query) })
                    searchList.addAll(audioList.filter { it.title.lowercase().contains(query) })
                }
                searchAdapter.notifyDataSetChanged()
                searchClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchClear.setOnClickListener {
            searchEditText.text.clear()
        }

        // Show search layout when search bar is clicked
        searchBarLayout.setOnClickListener {
            hideAllContent()
            searchLayout.visibility = View.VISIBLE
            bottomNav.selectedItemId = -1
        }
    }

    private fun setupMiniPlayer() {
        val miniPlayerIcon = findViewById<ImageView>(R.id.mini_player_icon)
        val miniPlayerTitle = findViewById<TextView>(R.id.mini_player_title)
        val miniPlayerSubtitle = findViewById<TextView>(R.id.mini_player_subtitle)
        val miniPlayerPlayPause = findViewById<ImageView>(R.id.mini_player_play_pause)
        val miniPlayerPrev = findViewById<ImageView>(R.id.mini_player_prev)
        val miniPlayerNext = findViewById<ImageView>(R.id.mini_player_next)
        val miniPlayerClose = findViewById<ImageView>(R.id.mini_player_close)

        miniPlayerPlayPause.setOnClickListener {
            togglePlayPause()
        }

        miniPlayerPrev.setOnClickListener {
            playPrevious()
        }

        miniPlayerNext.setOnClickListener {
            playNext()
        }

        miniPlayerClose.setOnClickListener {
            hideMiniPlayer()
        }

        miniPlayerLayout.setOnClickListener {
            showFullPlayer()
        }
    }

    private fun setupFullPlayer() {
        val fullPlayerClose = findViewById<ImageView>(R.id.full_player_close)
        val fullPlayerPlayPause = findViewById<ImageView>(R.id.full_player_play_pause)
        val fullPlayerPrev = findViewById<ImageView>(R.id.full_player_prev)
        val fullPlayerNext = findViewById<ImageView>(R.id.full_player_next)
        val fullPlayerShuffle = findViewById<ImageView>(R.id.full_player_shuffle)
        val fullPlayerRepeat = findViewById<ImageView>(R.id.full_player_repeat)
        val fullPlayerSeekbar = findViewById<SeekBar>(R.id.full_player_seekbar)
        val fullPlayerCurrentTime = findViewById<TextView>(R.id.full_player_current_time)
        val fullPlayerDuration = findViewById<TextView>(R.id.full_player_duration)
        val fullPlayerTitle = findViewById<TextView>(R.id.full_player_title)
        val fullPlayerSubtitle = findViewById<TextView>(R.id.full_player_subtitle)

        fullPlayerClose.setOnClickListener {
            hideFullPlayer()
        }

        fullPlayerPlayPause.setOnClickListener {
            togglePlayPause()
        }

        fullPlayerPrev.setOnClickListener {
            playPrevious()
        }

        fullPlayerNext.setOnClickListener {
            playNext()
        }

        fullPlayerShuffle.setOnClickListener {
            toggleShuffle()
        }

        fullPlayerRepeat.setOnClickListener {
            toggleRepeat()
        }

        fullPlayerSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                player?.seekTo((seekBar?.progress ?: 0) * (player?.duration ?: 0L) / 100)
            }
        })

        // Start thread to update seekbar
        Thread {
            while (true) {
                Thread.sleep(500)
                runOnUiThread {
                    if (player != null && player?.isPlaying == true) {
                        val currentPos = player?.currentPosition ?: 0L
                        val duration = player?.duration ?: 0L
                        if (duration > 0) {
                            fullPlayerSeekbar.progress = ((currentPos.toFloat() / duration) * 100).toInt()
                            fullPlayerCurrentTime.text = formatDuration(currentPos)
                            fullPlayerDuration.text = formatDuration(duration)
                        }
                    }
                }
            }
        }.start()
    }

    private fun checkPermissionAndLoadMedia() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, 
                arrayOf(
                    permission, 
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 
                STORAGE_PERMISSION_CODE
            )
        } else {
            loadMediaFiles()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadMediaFiles()
        } else {
            Toast.makeText(this, "Permission denied. Cannot load media.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMediaFiles() {
        videoList.clear()
        audioList.clear()

        // Load Videos
        loadVideos()
        
        // Load Audio
        loadAudio()
        
        videoAdapter.notifyDataSetChanged()
        audioAdapter.notifyDataSetChanged()
    }

    private fun loadVideos() {
        try {
            val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val videoProjection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.SIZE
            )
            contentResolver.query(videoUri, videoProjection, null, null, null)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol)
                    val duration = cursor.getLong(durationCol)
                    val artist = cursor.getString(artistCol) ?: "Unknown"
                    val size = cursor.getLong(sizeCol)
                    val uri = Uri.withAppendedPath(videoUri, id.toString())
                    videoList.add(MediaItem(id, name, uri, true, duration, artist, "Videos", size))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadAudio() {
        try {
            val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val audioProjection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.SIZE
            )
            contentResolver.query(audioUri, audioProjection, null, null, null)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol)
                    val duration = cursor.getLong(durationCol)
                    val artist = cursor.getString(artistCol) ?: "Unknown"
                    val album = cursor.getString(albumCol) ?: "Unknown"
                    val size = cursor.getLong(sizeCol)
                    val uri = Uri.withAppendedPath(audioUri, id.toString())
                    audioList.add(MediaItem(id, name, uri, false, duration, artist, album, size))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadBeats() {
        // Sample beats data
        beatsList.clear()
        
        // Add default beats
        val defaultBeats = listOf(
            BeatsItem(1, "Hip Hop Beat", Uri.EMPTY, 90, "Hip Hop"),
            BeatsItem(2, "EDM Beat", Uri.EMPTY, 128, "EDM"),
            BeatsItem(3, "Rock Beat", Uri.EMPTY, 110, "Rock"),
            BeatsItem(4, "Jazz Beat", Uri.EMPTY, 85, "Jazz"),
            BeatsItem(5, "Pop Beat", Uri.EMPTY, 100, "Pop"),
            BeatsItem(6, "Trap Beat", Uri.EMPTY, 140, "Trap"),
            BeatsItem(7, "Chill Beat", Uri.EMPTY, 75, "Chill"),
            BeatsItem(8, "Techno Beat", Uri.EMPTY, 130, "Techno")
        )
        
        beatsList.addAll(defaultBeats)
        beatsAdapter.notifyDataSetChanged()
    }

    private fun playMedia(media: MediaItem) {
        currentMediaItem = media
        
        // Add to queue
        playQueue.clear()
        playQueue.addAll(audioList.mapIndexed { index, item -> 
            PlayQueueItem(item, index, index == 0)
        })
        playQueue.addAll(videoList.mapIndexed { index, item -> 
            PlayQueueItem(item, audioList.size + index, false)
        })
        
        // Find current media in queue
        val queueIndex = playQueue.indexOfFirst { it.mediaItem.id == media.id }
        if (queueIndex >= 0) {
            currentPlayingIndex = queueIndex
            playQueue[queueIndex] = playQueue[queueIndex].copy(isPlaying = true)
            queueAdapter.updatePlayingStatus(queueIndex)
        }
        
        // Initialize player if needed
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
            playerView.player = player
        }
        
        val mediaItem = ExoMediaItem.fromUri(media.uri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        
        showMiniPlayer()
        updatePlayerUI()
    }

    private fun playBeats(beats: BeatsItem) {
        // For beats, we can play a simple tone or notification
        Toast.makeText(this, "Playing: ${beats.title} (${beats.bpm} BPM)", Toast.LENGTH_SHORT).show()
        
        // Show in mini player
        currentMediaItem = MediaItem(
            id = beats.id,
            title = beats.title,
            uri = beats.uri,
            isVideo = false,
            duration = 0L,
            artist = beats.category,
            album = "Beats",
            size = 0L
        )
        showMiniPlayer()
        updatePlayerUI()
    }

    private fun playQueueItem(queueItem: PlayQueueItem) {
        val index = playQueue.indexOf(queueItem)
        if (index >= 0) {
            currentPlayingIndex = index
            playMedia(queueItem.mediaItem)
        }
    }

    private fun removeFromQueue(queueItem: PlayQueueItem) {
        val index = playQueue.indexOf(queueItem)
        if (index >= 0) {
            playQueue.removeAt(index)
            queueAdapter.notifyDataSetChanged()
            
            if (currentPlayingIndex >= index) {
                currentPlayingIndex = (currentPlayingIndex - 1).coerceAtLeast(0)
            }
            
            if (playQueue.isEmpty()) {
                hideMiniPlayer()
            }
        }
    }

    private fun playNext() {
        if (playQueue.isNotEmpty() && currentPlayingIndex >= 0) {
            currentPlayingIndex = (currentPlayingIndex + 1) % playQueue.size
            playMedia(playQueue[currentPlayingIndex].mediaItem)
        }
    }

    private fun playPrevious() {
        if (playQueue.isNotEmpty() && currentPlayingIndex >= 0) {
            currentPlayingIndex = (currentPlayingIndex - 1 + playQueue.size) % playQueue.size
            playMedia(playQueue[currentPlayingIndex].mediaItem)
        }
    }

    private fun togglePlayPause() {
        if (player?.isPlaying == true) {
            player?.pause()
        } else {
            player?.play()
        }
        updatePlayerUI()
    }

    private fun toggleShuffle() {
        // Shuffle the queue
        if (playQueue.isNotEmpty()) {
            val currentItem = playQueue[currentPlayingIndex]
            playQueue.shuffle()
            currentPlayingIndex = playQueue.indexOf(currentItem)
            queueAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Queue shuffled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleRepeat() {
        // Toggle repeat mode
        val currentMode = player?.repeatMode
        when (currentMode) {
            ExoPlayer.REPEAT_MODE_OFF -> {
                player?.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                Toast.makeText(this, "Repeat: One", Toast.LENGTH_SHORT).show()
            }
            ExoPlayer.REPEAT_MODE_ONE -> {
                player?.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                Toast.makeText(this, "Repeat: All", Toast.LENGTH_SHORT).show()
            }
            ExoPlayer.REPEAT_MODE_ALL -> {
                player?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
                Toast.makeText(this, "Repeat: Off", Toast.LENGTH_SHORT).show()
            }
            else -> {
                player?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
            }
        }
    }

    private fun showMiniPlayer() {
        miniPlayerLayout.visibility = View.VISIBLE
        isFullScreenPlayer = false
        updatePlayerUI()
    }

    private fun hideMiniPlayer() {
        miniPlayerLayout.visibility = View.GONE
        player?.stop()
        player?.clearMediaItems()
        currentPlayingIndex = -1
        updatePlayerUI()
    }

    private fun showFullPlayer() {
        fullPlayerLayout.visibility = View.VISIBLE
        isFullScreenPlayer = true
        updatePlayerUI()
    }

    private fun hideFullPlayer() {
        fullPlayerLayout.visibility = View.GONE
        isFullScreenPlayer = false
        updatePlayerUI()
    }

    private fun updatePlayerUI() {
        val miniPlayerIcon = findViewById<ImageView>(R.id.mini_player_icon)
        val miniPlayerTitle = findViewById<TextView>(R.id.mini_player_title)
        val miniPlayerSubtitle = findViewById<TextView>(R.id.mini_player_subtitle)
        val miniPlayerPlayPause = findViewById<ImageView>(R.id.mini_player_play_pause)
        
        val fullPlayerIcon = findViewById<ImageView>(R.id.full_player_view)
        val fullPlayerTitle = findViewById<TextView>(R.id.full_player_title)
        val fullPlayerSubtitle = findViewById<TextView>(R.id.full_player_subtitle)
        val fullPlayerPlayPause = findViewById<ImageView>(R.id.full_player_play_pause)
        val fullPlayerSeekbar = findViewById<SeekBar>(R.id.full_player_seekbar)

        val currentMedia = currentMediaItem
        val isPlaying = player?.isPlaying == true

        if (currentMedia != null) {
            miniPlayerTitle.text = currentMedia.title
            miniPlayerSubtitle.text = currentMedia.artist
            fullPlayerTitle.text = currentMedia.title
            fullPlayerSubtitle.text = currentMedia.artist
        }

        if (isPlaying) {
            miniPlayerPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            fullPlayerPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            miniPlayerPlayPause.setImageResource(android.R.drawable.ic_media_play)
            fullPlayerPlayPause.setImageResource(android.R.drawable.ic_media_play)
        }
    }

    private fun showMediaOverflowMenu(media: MediaItem, view: View) {
        val popup = PopupMenu(this, view)
        val menu = popup.menu
        
        menu.add(Menu.NONE, 1, Menu.NONE, "Play")
        menu.add(Menu.NONE, 2, Menu.NONE, "Add to Queue")
        menu.add(Menu.NONE, 3, Menu.NONE, "Add to Favorites")
        menu.add(Menu.NONE, 4, Menu.NONE, "Share")
        menu.add(Menu.NONE, 5, Menu.NONE, "Delete")
        
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    playMedia(media)
                    true
                }
                2 -> {
                    addToQueue(media)
                    true
                }
                3 -> {
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                    true
                }
                4 -> {
                    Toast.makeText(this, "Share feature coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                5 -> {
                    deleteMedia(media)
                    true
                }
                else -> false
            }
        }
        
        popup.show()
    }

    private fun addToQueue(media: MediaItem) {
        val existingIndex = playQueue.indexOfFirst { it.mediaItem.id == media.id }
        if (existingIndex < 0) {
            playQueue.add(PlayQueueItem(media, playQueue.size, false))
            queueAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Added to queue", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Already in queue", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteMedia(media: MediaItem) {
        try {
            val uri = media.uri
            contentResolver.delete(uri, null, null)
            
            if (media.isVideo) {
                videoList.removeAll { it.id == media.id }
                videoAdapter.notifyDataSetChanged()
            } else {
                audioList.removeAll { it.id == media.id }
                audioAdapter.notifyDataSetChanged()
            }
            
            // Remove from queue if present
            playQueue.removeAll { it.mediaItem.id == media.id }
            queueAdapter.notifyDataSetChanged()
            
            // Stop player if deleted media was playing
            if (currentMediaItem?.id == media.id) {
                hideMiniPlayer()
            }
            
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot delete: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))
        
        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%d:%02d", minutes, seconds)
        }
    }

    override fun onBackPressed() {
        if (isFullScreenPlayer) {
            hideFullPlayer()
        } else if (miniPlayerLayout.visibility == View.VISIBLE) {
            hideMiniPlayer()
        } else if (searchLayout.visibility == View.VISIBLE) {
            searchLayout.visibility = View.GONE
            bottomNav.selectedItemId = R.id.nav_video
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
