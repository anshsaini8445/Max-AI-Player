package com.app.mediaplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MediaAdapter(
    private var mediaList: List<MediaItemModel>,
    private val onItemClick: (MediaItemModel) -> Unit
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.text_media_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]
        holder.titleView.text = media.title
        holder.itemView.setOnClickListener { onItemClick(media) }
    }

    override fun getItemCount(): Int = mediaList.size

    fun updateList(newList: List<MediaItemModel>) {
        mediaList = newList
        notifyDataSetChanged()
    }
}
