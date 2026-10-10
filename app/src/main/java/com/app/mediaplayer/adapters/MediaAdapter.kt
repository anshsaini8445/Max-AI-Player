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
import com.bumptech.glide.request.RequestOptions

class MediaAdapter(
    private var mediaList: List<MediaItem>,
    private val onItemClick: (MediaItem) -> Unit,
    private val onOverflowClick: (MediaItem, View) -> Unit
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.media_title)
        val subtitleView: TextView = itemView.findViewById(R.id.media_subtitle)
        val durationView: TextView = itemView.findViewById(R.id.media_duration)
        val thumbnailView: ImageView = itemView.findViewById(R.id.media_thumbnail)
        val overflowView: ImageView = itemView.findViewById(R.id.media_overflow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]
        holder.titleView.text = media.title
        
        if (media.isVideo) {
            holder.subtitleView.text = "${media.artist} • Video"
        } else {
            holder.subtitleView.text = "${media.artist} • Audio"
        }
        
        val duration = if (media.duration > 0) {
            formatDuration(media.duration)
        } else {
            "0:00"
        }
        holder.durationView.text = duration
        
        // Load thumbnail for videos
        if (media.isVideo) {
            Glide.with(holder.itemView.context)
                .load(media.uri)
                .apply(RequestOptions()
                    .placeholder(R.drawable.ic_media_play)
                    .centerCrop()
                    .error(R.drawable.ic_media_play))
                .into(holder.thumbnailView)
        } else {
            // For audio, use a default music icon
            holder.thumbnailView.setImageResource(R.drawable.ic_media_play)
            holder.thumbnailView.setColorFilter(
                holder.itemView.context.getColor(R.color.neon_accent)
            )
        }
        
        holder.itemView.setOnClickListener { onItemClick(media) }
        holder.overflowView.setOnClickListener { 
            onOverflowClick(media, holder.overflowView) 
        }
    }

    override fun getItemCount(): Int = mediaList.size

    fun updateList(newList: List<MediaItem>) {
        mediaList = newList
        notifyDataSetChanged()
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
}
