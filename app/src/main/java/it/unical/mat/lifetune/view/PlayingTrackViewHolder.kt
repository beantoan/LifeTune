package it.unical.mat.lifetune.view

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import it.unical.mat.lifetune.databinding.ViewHolderPlayingTrackBinding
import it.unical.mat.lifetune.entity.Track

/**
 * Created by beantoan on 1/14/18.
 */
class PlayingTrackViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    private var track: Track? = null

    fun bindTrack(_track: Track) {
        track = _track

        val binding = DataBindingUtil.bind(view) as ViewHolderPlayingTrackBinding
        binding.track = _track
        binding.executePendingBindings()
    }
}