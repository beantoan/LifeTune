package it.unical.mat.lifetune.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.databinding.ViewHolderPlaylistBinding
import it.unical.mat.lifetune.model.Playlist


/**
 * Created by beantoan on 11/20/17.
 */

class PlaylistsRecyclerViewAdapter(private val playlists: List<Playlist>) :
        RecyclerView.Adapter<PlaylistsRecyclerViewAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val playlist = this.playlists[position]
        holder!!.bindObject(playlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.view_holder_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = this.playlists.size


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var viewHolderPlaylistBinding = DataBindingUtil.bind<ViewHolderPlaylistBinding>(this.itemView)

        fun bindObject(playlist: Playlist) {
            viewHolderPlaylistBinding.playlist = playlist
            viewHolderPlaylistBinding.executePendingBindings()
        }
    }
}