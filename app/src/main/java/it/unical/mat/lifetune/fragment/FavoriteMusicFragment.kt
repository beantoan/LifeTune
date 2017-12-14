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
import it.unical.mat.lifetune.service.PlaylistServiceInterface
import it.unical.mat.lifetune.util.AppUtils
import kotlinx.android.synthetic.main.fragment_favorite_music.*


/**
 * Created by beantoan on 11/17/17.
 */
class FavoriteMusicFragment : BaseMusicFragment(), FavouriteMusicController.AdapterCallbacks {

    lateinit var favouriteMusicController: FavouriteMusicController
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onPlaylistClicked(playlist: Playlist, position: Int) {

    }

    override fun onSongClicked(song: Song?, position: Int) {

    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        setupRecyclerViewCategories()

        setupMusicController()

        callFavouritePlaylistsService()
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
        favouriteMusicController = FavouriteMusicController(this)

        recycler_view_playlists.clear()
        recycler_view_playlists.setController(favouriteMusicController)
    }

    private fun updateMusicController(data: List<Playlist>) {
        favouriteMusicController.setData(data)
    }

    private fun callFavouritePlaylistsService() {
        if (AppUtils.isInternetConnected(activity!!.applicationContext)) {
            val playlistService = ApiServiceFactory.create(PlaylistServiceInterface::class.java)

            getCompositeDisposable().add(playlistService.favourite()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { playlists -> showPlaylists(playlists) }
            )
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        updateMusicController(playlists)
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
