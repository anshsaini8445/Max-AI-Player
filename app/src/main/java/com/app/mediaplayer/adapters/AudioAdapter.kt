package com.app.mediaplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.mediaplayer.R
import com.app.mediaplayer.models.MediaItem

class AudioAdapter(
    private var audioList: List<MediaItem>,
    private val onItemClick: (MediaItem) -> Unit,
    private val onOverflowClick: (MediaItem, View) -> Unit
) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.audio_title)
        val artistView: TextView = itemView.findViewById(R.id.audio_artist)
        val durationView: TextView = itemView.findViewById(R.id.audio_duration)
        val iconView: ImageView = itemView.findViewById(R.id.audio_icon)
        val overflowView: ImageView = itemView.findViewById(R.id.audio_overflow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audio = audioList[position]
        holder.titleView.text = audio.title
        holder.artistView.text = audio.artist
        holder.durationView.text = formatDuration(audio.duration)
        
        holder.iconView.setColorFilter(
            holder.itemView.context.getColor(R.color.neon_accent)
        )
        
        holder.itemView.setOnClickListener { onItemClick(audio) }
        holder.overflowView.setOnClickListener { 
            onOverflowClick(audio, holder.overflowView) 
        }
    }

    override fun getItemCount(): Int = audioList.size

    fun updateList(newList: List<MediaItem>) {
        audioList = newList
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
