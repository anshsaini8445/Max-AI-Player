package com.app.mediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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

    private lateinit var videoRecycler: RecyclerView
    private lateinit var audioRecycler: RecyclerView
    private lateinit var searchLayout: LinearLayout
    private lateinit var settingsLayout: ScrollViewCompatPlaceholder
    private lateinit var playerView: PlayerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var searchEditText: EditText
    private lateinit var searchRecycler: RecyclerView

    private val videoList = mutableListOf<MediaItemModel>()
    private val audioList = mutableListOf<MediaItemModel>()
    private val searchList = mutableListOf<MediaItemModel>()

    private lateinit var videoAdapter: MediaAdapter
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var searchAdapter: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoRecycler = findViewById(R.id.video_recycler)
        audioRecycler = findViewById(R.id.audio_recycler)
        searchLayout = findViewById(R.id.search_layout)
        settingsLayout = findViewById(R.id.settings_layout)
        playerView = findViewById(R.id.player_view)
        bottomNav = findViewById(R.id.bottom_nav)
        searchEditText = findViewById(R.id.search_edit_text)
        searchRecycler = findViewById(R.id.search_recycler)

        setupAdapters()
        checkPermissionAndLoadMedia()
        setupBottomNav()
        setupSearch()
    }

    private fun setupAdapters() {
        videoRecycler.layoutManager = LinearLayoutManager(this)
        videoAdapter = MediaAdapter(videoList) { media -> playMedia(media) }
        videoRecycler.adapter = videoAdapter

        audioRecycler.layoutManager = LinearLayoutManager(this)
        audioAdapter = AudioAdapter(audioList) { media -> playMedia(media) }
        audioRecycler.adapter = audioAdapter

        searchRecycler.layoutManager = LinearLayoutManager(this)
        searchAdapter = MediaAdapter(searchList) { media -> playMedia(media) }
        searchRecycler.adapter = searchAdapter
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            videoRecycler.visibility = View.GONE
            audioRecycler.visibility = View.GONE
            searchLayout.visibility = View.GONE
            settingsLayout.visibility = View.GONE
            playerView.visibility = View.GONE

            when (item.itemId) {
                R.id.nav_video -> videoRecycler.visibility = View.VISIBLE
                R.id.nav_audio -> audioRecycler.visibility = View.VISIBLE
                R.id.nav_search -> searchLayout.visibility = View.VISIBLE
                R.id.nav_settings -> settingsLayout.visibility = View.VISIBLE
            }
            true
        }
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
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkPermissionAndLoadMedia() {
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
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videoProjection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME)
        contentResolver.query(videoUri, videoProjection, null, null, null)?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val name = it.getString(nameCol)
                val uri = Uri.withAppendedPath(videoUri, id.toString())
                videoList.add(MediaItemModel(id, name, uri, true))
            }
        }
        videoAdapter.notifyDataSetChanged()

        // Load Audio
        val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val audioProjection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME)
        contentResolver.query(audioUri, audioProjection, null, null, null)?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val name = it.getString(nameCol)
                val uri = Uri.withAppendedPath(audioUri, id.toString())
                audioList.add(MediaItemModel(id, name, uri, false))
            }
        }
        audioAdapter.notifyDataSetChanged()
    }

    private fun playMedia(media: MediaItemModel) {
        playerView.visibility = View.VISIBLE
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
            playerView.player = player
        }
        val mediaItem = MediaItem.fromUri(media.uri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    override fun onBackPressed() {
        if (playerView.visibility == View.VISIBLE) {
            player?.stop()
            playerView.visibility = View.GONE
            bottomNav.selectedItemId = R.id.nav_video
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}

typealias ScrollViewCompatPlaceholder = android.widget.ScrollView

class AudioAdapter(
    private var audioList: List<MediaItemModel>,
    private val onItemClick: (MediaItemModel) -> Unit
) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.audio_title)
        val artistView: TextView = itemView.findViewById(R.id.audio_artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audio = audioList[position]
        holder.titleView.text = audio.title
        holder.artistView.text = "Local Audio"
        holder.itemView.setOnClickListener { onItemClick(audio) }
    }

    override fun getItemCount() = audioList.size
}