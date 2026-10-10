package com.app.mediaplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.mediaplayer.R
import com.app.mediaplayer.models.BeatsItem

class BeatsAdapter(
    private var beatsList: List<BeatsItem>,
    private val onItemClick: (BeatsItem) -> Unit
) : RecyclerView.Adapter<BeatsAdapter.BeatsViewHolder>() {

    inner class BeatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.beats_title)
        val bpmView: TextView = itemView.findViewById(R.id.beats_bpm)
        val categoryView: TextView = itemView.findViewById(R.id.beats_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_beats, parent, false)
        return BeatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeatsViewHolder, position: Int) {
        val beats = beatsList[position]
        holder.titleView.text = beats.title
        holder.bpmView.text = "${beats.bpm} BPM"
        holder.categoryView.text = beats.category
        
        holder.itemView.setOnClickListener { onItemClick(beats) }
    }

    override fun getItemCount(): Int = beatsList.size

    fun updateList(newList: List<BeatsItem>) {
        beatsList = newList
        notifyDataSetChanged()
    }
}
