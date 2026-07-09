package com.cigartv.tv.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cigartv.tv.R
import com.cigartv.tv.model.Episode

class EpisodeAdapter(
    private var episodes: List<Episode>,
    private val onPlay: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.VH>() {

    fun update(newEpisodes: List<Episode>) {
        episodes = newEpisodes
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val thumb: ImageView = v.findViewById(R.id.ep_thumb)
        val meta: TextView = v.findViewById(R.id.ep_meta)
        val title: TextView = v.findViewById(R.id.ep_title)
        val desc: TextView = v.findViewById(R.id.ep_desc)
        val focusBar: View = v.findViewById(R.id.ep_focus_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ep = episodes[position]
        holder.meta.text = ep.metaLine()
        holder.title.text = ep.title
        holder.desc.text = ep.description
        holder.thumb.load(ep.thumbnailUrl) {
            crossfade(true)
            placeholder(R.drawable.card_placeholder)
        }

        holder.itemView.setOnClickListener { onPlay(ep) }
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            holder.focusBar.visibility = if (hasFocus) View.VISIBLE else View.INVISIBLE
            holder.itemView.animate()
                .scaleX(if (hasFocus) 1.02f else 1f)
                .scaleY(if (hasFocus) 1.02f else 1f)
                .setDuration(150).start()
        }
    }

    override fun getItemCount() = episodes.size
}
