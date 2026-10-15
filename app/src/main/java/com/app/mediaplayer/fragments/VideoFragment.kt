package com.app.mediaplayer.fragments

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mediaplayer.R
import com.app.mediaplayer.adapters.VideoAdapter
import com.app.mediaplayer.models.MediaItem
import com.app.mediaplayer.utils.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoFragment : Fragment() {

    private lateinit var rvVideos: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var emptyStateText: TextView
    private lateinit var btnGrantPermission: Button

    private var videoAdapter: VideoAdapter? = null
    private val videoList = mutableListOf<MediaItem>()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadVideos()
        } else {
            showPermissionDeniedState()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        initViews(view)
        setupRecyclerView()
        checkAndRequestPermission()
        return view
    }

    private fun initViews(view: View) {
        rvVideos = view.findViewById(R.id.rv_videos)
        progressBar = view.findViewById(R.id.video_progress)
        emptyStateContainer = view.findViewById(R.id.empty_state_container)
        emptyStateText = view.findViewById(R.id.empty_state_text)
        btnGrantPermission = view.findViewById(R.id.btn_grant_permission)

        btnGrantPermission.setOnClickListener {
            checkAndRequestPermission()
        }
    }

    private fun setupRecyclerView() {
        videoAdapter = VideoAdapter(videoList) { selectedVideo ->
            Toast.makeText(requireContext(), selectedVideo.title, Toast.LENGTH_SHORT).show()
        }
        rvVideos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(30)
        }
    }

    private fun checkAndRequestPermission() {
        val context = requireContext()
        if (PermissionUtils.checkVideoPermission(context)) {
            loadVideos()
        } else {
            permissionLauncher.launch(PermissionUtils.getVideoPermission())
        }
    }

    private fun loadVideos() {
        progressBar.visibility = View.VISIBLE
        emptyStateContainer.visibility = View.GONE
        btnGrantPermission.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val scannedVideos = withContext(Dispatchers.IO) {
                scanLocalVideos()
            }

            progressBar.visibility = View.GONE
            videoList.clear()
            videoList.addAll(scannedVideos)
            videoAdapter?.notifyDataSetChanged()

            if (videoList.isEmpty()) {
                emptyStateText.text = getString(R.string.msg_no_media)
                emptyStateContainer.visibility = View.VISIBLE
                btnGrantPermission.visibility = View.GONE
            } else {
                emptyStateContainer.visibility = View.GONE
            }
        }
    }

    private fun scanLocalVideos(): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        val currentContext = context ?: return list

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.ARTIST
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        try {
            val cursor = currentContext.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val titleCol = it.getColumnIndex(MediaStore.Video.Media.TITLE)
                val nameCol = it.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val durationCol = it.getColumnIndex(MediaStore.Video.Media.DURATION)
                val sizeCol = it.getColumnIndex(MediaStore.Video.Media.SIZE)
                val artistCol = it.getColumnIndex(MediaStore.Video.Media.ARTIST)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    var title = if (titleCol != -1) it.getString(titleCol) else null
                    if (title.isNullOrEmpty() && nameCol != -1) {
                        title = it.getString(nameCol)
                    }
                    if (title.isNullOrEmpty()) {
                        title = "Video_$id"
                    }

                    val duration = if (durationCol != -1) it.getLong(durationCol) else 0L
                    val size = if (sizeCol != -1) it.getLong(sizeCol) else 0L
                    val artist = if (artistCol != -1) it.getString(artistCol) ?: "Unknown" else "Unknown"

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    list.add(
                        MediaItem(
                            id = id,
                            title = title,
                            uri = contentUri,
                            isVideo = true,
                            duration = duration,
                            artist = artist,
                            album = "Video",
                            size = size
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    private fun showPermissionDeniedState() {
        progressBar.visibility = View.GONE
        emptyStateText.text = getString(R.string.msg_permission_denied)
        emptyStateContainer.visibility = View.VISIBLE
        btnGrantPermission.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance(): VideoFragment = VideoFragment()
    }
}
