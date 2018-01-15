package it.unical.mat.lifetune.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.entity.Track
import it.unical.mat.lifetune.fragment.PlayMusicFragment
import it.unical.mat.lifetune.view.PlayingTrackViewHolder


/**
 * Created by beantoan on 1/14/18.
 */
class PlayingTracksAdapter(val playingMusicFragment: PlayMusicFragment, tracks: ArrayList<Track>) :
        ManipulatedAdapter<Track, PlayingTrackViewHolder>(tracks) {

    override fun onBindViewHolder(holderTrack: PlayingTrackViewHolder?, position: Int) {
        val track = this.getItem(position)

        holderTrack!!.bindTrack(track)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlayingTrackViewHolder {
        val view = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.view_holder_playing_track, parent, false)
        return PlayingTrackViewHolder(playingMusicFragment, view)
    }


}