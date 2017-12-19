package it.unical.mat.lifetune.util

import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.TrackSelector
import it.unical.mat.lifetune.entity.Track

/**
 * Created by beantoan on 12/19/17.
 */
class CustomExoPlayer(renderersFactory: RenderersFactory, trackSelector: TrackSelector,
                      loadControl: LoadControl)
    : SimpleExoPlayer(renderersFactory, trackSelector, loadControl) {
    var tracks: List<Track> = ArrayList()
}