package it.unical.mat.lifetune.view

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.databinding.ViewHolderSearchSongResultBinding
import it.unical.mat.lifetune.entity.Song
import it.unical.mat.lifetune.fragment.PlayMusicFragment

/**
 * Created by beantoan on 1/14/18.
 */
class SearchResultSongViewHolder(val playMusicFragment: PlayMusicFragment, var view: View) : RecyclerView.ViewHolder(view) {

    init {
        ButterKnife.bind(this, view)
    }

    private var song: Song? = null

    fun bindSong(_song: Song) {
        song = _song

        val binding = DataBindingUtil.bind(view) as ViewHolderSearchSongResultBinding
        binding.song = _song
        binding.executePendingBindings()
    }

    @OnClick(R.id.song_element)
    fun onSongElementClicked() {
        Log.d(TAG, "onLikeButtonClicked: track.title=${song?.shortLog()}")

        playMusicFragment.playFoundSong(song)
    }

    companion object {
        val TAG = SearchResultSongViewHolder::class.java.simpleName
    }
}