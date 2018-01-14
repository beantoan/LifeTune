package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.FavouriteMusicController
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.Playlist
import kotlinx.android.synthetic.main.fragment_favorite_music.*


/**
 * Created by beantoan on 11/17/17.
 */
class FavoriteMusicFragment : BaseMusicFragment() {

    private var controller: FavouriteMusicController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")

        return inflater.inflate(R.layout.fragment_favorite_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")

        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onResume() {
        Log.d(TAG, "onResume")

        super.onResume()

        onResumeTasks()
    }

    override fun onPause() {
        updateControllerData(ArrayList())

        super.onPause()
    }

    override fun beforeCallFavouriteApi() {
        super.beforeCallFavouriteApi()

        updateControllerData(ArrayList())
    }

    override fun onFavouriteApiSuccess(playlists: List<Playlist>) {
        super.onFavouriteApiSuccess(playlists)

        updateControllerData(playlists)
    }

    override fun onFavouriteApiFailure(error: Throwable) {
        super.onFavouriteApiFailure(error)

        updateControllerData(ArrayList())
    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        setupRecyclerViewPlaylists()

        setupMusicController()
    }

    private fun onResumeTasks() {
        callFavouriteApi()
    }

    private fun setupRecyclerViewPlaylists() {
        Log.d(TAG, "setupRecyclerViewPlaylists")

        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.category_divider)
        val dividerItemDecoration = RecyclerViewDividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_playlists.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_playlists.addItemDecoration(dividerItemDecoration)
    }

    private fun setupMusicController() {
        Log.d(TAG, "setupMusicController")
        controller = FavouriteMusicController(this)

        recycler_view_playlists.clear()
        recycler_view_playlists.setController(controller)
    }

    private fun updateControllerData(playlists: List<Playlist>) {
        controller?.cancelPendingModelBuild()
        controller?.setData(playlists)
    }

    companion object {
        private val TAG = FavoriteMusicFragment::class.java.simpleName

        fun newInstance(playMusicFragment: PlayMusicFragment): FavoriteMusicFragment {
            val fragment = FavoriteMusicFragment()

            fragment.playMusicFragment = playMusicFragment

            return fragment
        }
    }
}
