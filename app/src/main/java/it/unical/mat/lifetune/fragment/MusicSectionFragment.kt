package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.*
import butterknife.BindView
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyRecyclerView
import com.thedeanda.lorem.LoremIpsum
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.MusicController
import it.unical.mat.lifetune.decoration.CategoryDividerItemDecoration
import it.unical.mat.lifetune.entity.Category
import it.unical.mat.lifetune.entity.Playlist


/**
 * Created by beantoan on 11/17/17.
 */
class MusicSectionFragment : Fragment(), MusicController.AdapterCallbacks {
    @BindView(R.id.categories)
    lateinit var mRecyclerViewPlaylists: EpoxyRecyclerView

    private val musicController: MusicController = MusicController(this)

    private var categories: MutableList<Category> = ArrayList()

    private var actionBarMenu: Menu? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val rootView = inflater.inflate(R.layout.fragment_music_section, container, false)

        onCreateViewTasks(rootView)

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        actionBarMenu = menu
    }

    override fun onPlaylistClicked(category: Category, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchMusicClicked() {
        Log.d(TAG, "onSearchMusicClicked")

        val menuItemSearch = actionBarMenu!!.findItem(R.id.action_search)
        menuItemSearch.expandActionView()
    }

    private fun onCreateViewTasks(rootView: View) {
        setHasOptionsMenu(true)

        ButterKnife.bind(this, rootView)

        setupRecyclerViewPlaylists()

        dummyPlaylistData()

        updateMusicController()
    }

    private fun setupRecyclerViewPlaylists() {
        val dividerItemDecoration = CategoryDividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(context!!, R.drawable.category_divider)
        dividerItemDecoration.setDrawable(dividerDrawable!!)

        mRecyclerViewPlaylists.layoutManager = GridLayoutManager(context, 1)
        mRecyclerViewPlaylists.addItemDecoration(dividerItemDecoration)
        mRecyclerViewPlaylists.setController(musicController)
    }

    private fun updateMusicController() {
        musicController.setData(categories)
    }

    // TODO add temporary data
    private fun dummyPlaylistData() {
        val lorem = LoremIpsum.getInstance()

        val playlists: MutableList<Playlist> = ArrayList()

        (0..10).mapTo(playlists) { Playlist(it, lorem.getTitle(3, 5), "xxxurl", "yyyurl") }

        (0..50).forEach { i -> categories.add(Category(i, lorem.getTitle(2, 4), lorem.getTitle(5, 8), playlists)); }
    }

    companion object {
        private val TAG = MusicSectionFragment::class.java.canonicalName
    }
}