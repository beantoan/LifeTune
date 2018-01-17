package it.unical.mat.lifetune.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.entity.Song
import it.unical.mat.lifetune.fragment.PlayMusicFragment
import it.unical.mat.lifetune.view.SearchResultSongViewHolder


/**
 * Created by beantoan on 1/14/18.
 */
class SearchSongResultsAdapter(val playingMusicFragment: PlayMusicFragment, songs: ArrayList<Song>) :
        ManipulatedAdapter<Song, SearchResultSongViewHolder>(songs) {

    override fun onBindViewHolder(holderSong: SearchResultSongViewHolder?, position: Int) {
        val song = this.getItem(position)

        holderSong!!.bindSong(song)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchResultSongViewHolder {
        val view = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.view_holder_search_song_result, parent, false)
        return SearchResultSongViewHolder(playingMusicFragment, view)
    }


}