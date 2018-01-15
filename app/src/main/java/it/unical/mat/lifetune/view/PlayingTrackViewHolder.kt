package it.unical.mat.lifetune.view

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.databinding.ViewHolderPlayingTrackBinding
import it.unical.mat.lifetune.entity.Track
import it.unical.mat.lifetune.fragment.PlayMusicFragment

/**
 * Created by beantoan on 1/14/18.
 */
class PlayingTrackViewHolder(val playMusicFragment: PlayMusicFragment, val view: View) :
        RecyclerView.ViewHolder(view) {

    init {
        ButterKnife.bind(this, view)
    }

    private var track: Track? = null

    fun bindTrack(_track: Track) {
        track = _track

        val binding = DataBindingUtil.bind(view) as ViewHolderPlayingTrackBinding
        binding.track = _track
        binding.executePendingBindings()
    }

    @OnClick(R.id.track_element)
    fun onTrackElementClicked() {
        Log.d(TAG, "onLikeButtonClicked: track.title=${track?.title}")

        playMusicFragment.playTrackAtPosition(adapterPosition)
    }

    companion object {
        val TAG = PlayingTrackViewHolder::class.java.simpleName
    }
}