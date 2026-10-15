package com.app.mediaplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.mediaplayer.R
import com.app.mediaplayer.models.MediaItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.util.Locale

class VideoAdapter(
    private var videoList: List<MediaItem>,
    private val onItemClick: (MediaItem) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumb: ImageView = view.findViewById(R.id.video_thumb)
        val duration: TextView = view.findViewById(R.id.video_duration)
        val title: TextView = view.findViewById(R.id.video_title)
        val size: TextView = view.findViewById(R.id.video_size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = videoList[position]
        holder.title.text = item.title
        holder.duration.text = formatDuration(item.duration)
        holder.size.text = formatSize(item.size)

        Glide.with(holder.itemView.context)
            .load(item.uri)
            .override(320, 200)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .centerCrop()
            .placeholder(R.drawable.ic_max_logo)
            .error(R.drawable.ic_max_logo)
            .into(holder.thumb)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.thumb)
    }

    override fun getItemCount(): Int = videoList.size

    fun updateList(newList: List<MediaItem>) {
        videoList = newList
        notifyDataSetChanged()
    }

    private fun formatDuration(ms: Long): String {
        val totalSecs = ms / 1000
        val seconds = totalSecs % 60
        val minutes = (totalSecs / 60) % 60
        val hours = totalSecs / 3600
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    private fun formatSize(bytes: Long): String {
        val mb = bytes.toDouble() / (1024 * 1024)
        return if (mb >= 1024) {
            String.format(Locale.getDefault(), "%.2f GB", mb / 1024)
        } else {
            String.format(Locale.getDefault(), "%.1f MB", mb)
        }
    }
}
