package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.FavouriteMusicController
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.Song
import it.unical.mat.lifetune.service.ApiServiceFactory
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils
import kotlinx.android.synthetic.main.fragment_favorite_music.*


/**
 * Created by beantoan on 11/17/17.
 */
class FavoriteMusicFragment : BaseMusicFragment(), FavouriteMusicController.AdapterCallbacks {

    lateinit var controller: FavouriteMusicController

    private var playlists: List<Playlist> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onPlaylistClicked(playlist: Playlist, position: Int) {
        playSongs(playlist.songs)
    }

    override fun onSongClicked(song: Song?, position: Int) {

    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        setupRecyclerViewCategories()

        setupMusicController()

//        callFavouritePlaylistsService()
    }

    private fun setupRecyclerViewCategories() {
        Log.d(TAG, "setupRecyclerViewCategories")

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

        if (playlists.isNotEmpty()) {
            updateMusicController(playlists)
        }
    }

    private fun updateMusicController(data: List<Playlist>) {
        controller.setData(data)
    }

    private fun callFavouritePlaylistsService() {
        if (playlists.isEmpty()) {
            if (AppUtils.isInternetConnected(activity!!.applicationContext)) {
                showLoading()

                getCompositeDisposable().add(
                        ApiServiceFactory.createPlaylistService().favourite()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { playlists -> onFavouritePlaylistsServiceSuccess(playlists) },
                                { error -> onFavouritePlaylistsServiceFailure(error) }
                        )
                )
            } else {
                AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
            }
        }
    }

    private fun onFavouritePlaylistsServiceSuccess(playlists: List<Playlist>) {
        showPlaylists(playlists)
    }

    private fun onFavouritePlaylistsServiceFailure(error: Throwable) {
        Log.e(TAG, "onFavouritePlaylistsServiceFailure", error)

        showPlaylists(ArrayList())

        AppDialog.error(R.string.api_service_error_title, R.string.api_service_error_message, activity!!)
    }

    private fun showPlaylists(_playlists: List<Playlist>) {
        playlists = _playlists
        
        updateMusicController(playlists)

        hideLoading()
    }

    companion object {
        private val TAG = FavoriteMusicFragment::class.java.canonicalName

        fun newInstance(playMusicFragment: PlayMusicFragment): FavoriteMusicFragment {
            val fragment = FavoriteMusicFragment()

            fragment.playMusicFragment = playMusicFragment

            return fragment
        }
    }
}
