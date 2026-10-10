package com.app.mediaplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.mediaplayer.R
import com.app.mediaplayer.models.PlayQueueItem

class PlayQueueAdapter(
    private var queueList: List<PlayQueueItem>,
    private val onItemClick: (PlayQueueItem) -> Unit,
    private val onRemoveClick: (PlayQueueItem) -> Unit
) : RecyclerView.Adapter<PlayQueueAdapter.QueueViewHolder>() {

    inner class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.queue_title)
        val subtitleView: TextView = itemView.findViewById(R.id.queue_subtitle)
        val positionView: TextView = itemView.findViewById(R.id.queue_position)
        val playingIndicator: View = itemView.findViewById(R.id.queue_playing_indicator)
        val removeView: ImageView = itemView.findViewById(R.id.queue_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_queue, parent, false)
        return QueueViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val queueItem = queueList[position]
        holder.titleView.text = queueItem.mediaItem.title
        holder.subtitleView.text = queueItem.mediaItem.artist
        holder.positionView.text = (position + 1).toString()
        
        holder.playingIndicator.visibility = if (queueItem.isPlaying) View.VISIBLE else View.GONE
        
        holder.itemView.setOnClickListener { onItemClick(queueItem) }
        holder.removeView.setOnClickListener { onRemoveClick(queueItem) }
    }

    override fun getItemCount(): Int = queueList.size

    fun updateList(newList: List<PlayQueueItem>) {
        queueList = newList
        notifyDataSetChanged()
    }

    fun updatePlayingStatus(position: Int) {
        queueList = queueList.mapIndexed { index, item ->
            item.copy(isPlaying = index == position)
        }
        notifyDataSetChanged()
    }
}
