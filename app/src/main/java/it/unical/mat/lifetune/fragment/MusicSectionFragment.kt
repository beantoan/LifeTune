package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyRecyclerView
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.MusicController
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.Song

/**
 * Created by beantoan on 11/17/17.
 */
class MusicSectionFragment : Fragment(), MusicController.AdapterCallbacks {

    @BindView(R.id.all_playlists)
    lateinit var mRecyclerViewPlaylists: EpoxyRecyclerView

    private val musicController: MusicController = MusicController(this)

    private var playlists: MutableList<Playlist> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val rootView = inflater.inflate(R.layout.fragment_music_section, container, false)

        onCreateViewTasks(rootView)

        return rootView
    }

    override fun onPlaylistClicked(playlist: Playlist?, position: Int) {

    }

    private fun onCreateViewTasks(rootView: View) {
        ButterKnife.bind(this, rootView)

        mRecyclerViewPlaylists.layoutManager = GridLayoutManager(rootView.context, 1)
        mRecyclerViewPlaylists.setController(musicController)

        dummyPlaylistData()

        updateMusicController()
    }

    private fun updateMusicController() {
        musicController.setData(playlists)
    }

    // TODO add temporary data
    private fun dummyPlaylistData() {
        val songs: MutableList<Song> = ArrayList()

        for (i in 0..100) {
            songs.add(Song(i, "Song $i", "xxxurl", "yyyurl"))
        }

        for (i in 0..1000) {
            playlists.add(Playlist(i, "Playlist $i", "test 1", songs));
        }
    }

    companion object {
        private val TAG = MusicSectionFragment::class.java.canonicalName
    }
}