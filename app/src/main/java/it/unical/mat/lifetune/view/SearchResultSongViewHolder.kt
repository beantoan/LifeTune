package it.unical.mat.lifetune.view

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import it.unical.mat.lifetune.databinding.ViewHolderSearchSongResultBinding
import it.unical.mat.lifetune.entity.Song

/**
 * Created by beantoan on 1/14/18.
 */
class SearchResultSongViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    private var song: Song? = null

    fun bindSong(_song: Song) {
        song = _song

        val binding = DataBindingUtil.bind(view) as ViewHolderSearchSongResultBinding
        binding.song = _song
        binding.executePendingBindings()
    }
}