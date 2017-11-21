package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.adapter.PlaylistsRecyclerViewAdapter
import it.unical.mat.lifetune.model.Playlist

/**
 * Created by beantoan on 11/17/17.
 */
class MusicSectionFragment : Fragment() {

    @BindView(R.id.playlists)
    lateinit var mRecyclerViewPlaylists: RecyclerView

    lateinit var mAdapter: RecyclerView.Adapter<*>
    lateinit var mLayoutManager: RecyclerView.LayoutManager

    private var playlists : MutableList<Playlist> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val rootView = inflater.inflate(R.layout.fragment_music_section, container, false)

        onCreateViewTasks(rootView)

        return rootView
    }

    private fun onCreateViewTasks(rootView: View) {
        ButterKnife.bind(this, rootView)

        mRecyclerViewPlaylists.setHasFixedSize(true)

        mLayoutManager = LinearLayoutManager(rootView.context)
        mRecyclerViewPlaylists.layoutManager = mLayoutManager

        // TODO add temporary data
        val playlist1 = Playlist("Playlist 01", "test 1", ArrayList())
        val playlist2 = Playlist("Playlist 02", "test 2", ArrayList())
        playlists.add(playlist1)
        playlists.add(playlist2)

        Log.d(TAG, playlist1.title)

        mAdapter = PlaylistsRecyclerViewAdapter(playlists)
        mRecyclerViewPlaylists.adapter = mAdapter
    }

    companion object {
        private val TAG = MusicSectionFragment::class.java.canonicalName
    }
}