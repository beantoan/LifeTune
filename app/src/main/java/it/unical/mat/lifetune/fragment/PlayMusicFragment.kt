package it.unical.mat.lifetune.fragment

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.lapism.searchview.SearchHistoryTable
import com.lapism.searchview.SearchItem
import com.lapism.searchview.SearchView
import it.unical.mat.lifetune.LifeTuneApplication
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.adapter.PlayMusicPagerAdapter
import it.unical.mat.lifetune.entity.TrackList
import kotlinx.android.synthetic.main.fragment_play_music.*


/**
 * Created by beantoan on 12/12/17.
 */
class PlayMusicFragment : Fragment(),
        AppBarLayout.OnOffsetChangedListener {

    private lateinit var mPlayMusicPagerAdapter: PlayMusicPagerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_play_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        onViewCreatedTasks()
    }

    override fun onResume() {
        super.onResume()

        onResumeTasks()
    }

    private fun onResumeTasks() {
        search_view.close(false)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        onDestroyTasks()

        super.onDestroy()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        search_view.translationY = verticalOffset.toFloat()
    }

    private fun onViewCreatedTasks() {
        Log.d(TAG, "onViewCreatedTasks")

        app_bar_layout.addOnOffsetChangedListener(this)

        setupViewPager()
        
        setupMusicPlayer()

        setupBottomSheet()

        setupSearchView()
    }

    private fun onDestroyTasks() {
        Log.d(TAG, "onDestroyTasks")
    }

    private fun setupViewPager() {
        Log.d(TAG, "setupViewPager")

        mPlayMusicPagerAdapter = PlayMusicPagerAdapter(this, activity!!.supportFragmentManager)

        pager.adapter = mPlayMusicPagerAdapter

        tabs.setupWithViewPager(pager)
    }

    private fun setupMusicPlayer() {
        Log.d(TAG, "setupMusicPlayer")

        music_player.player = LifeTuneApplication.musicPlayer
        music_player.requestFocus()
        music_player.controllerAutoShow = true
        music_player.showController()

        if (LifeTuneApplication.musicPlayer.tracks.isEmpty()) {
            hideMusicPlayer()
        }
    }

    private fun displayMusicPlayer(isShown: Boolean) {
        Log.d(TAG, "displayMusicPlayer: isShown=$isShown")

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_music_player)

        val musicPlayerHeight = when {
            isShown -> resources.getDimension(R.dimen.music_player_height).toInt()
            else -> 0
        }

        bottomSheetBehavior.peekHeight = musicPlayerHeight
        pager.setPadding(0, 0, 0, musicPlayerHeight)
    }

    fun showMusicPlayer() {
        displayMusicPlayer(true)
    }

    fun hideMusicPlayer() {
        displayMusicPlayer(false)
    }

    fun playSongs(trackList: TrackList?) {
        Log.d(TAG, "playSongs")

        music_player.player.stop()

        val dynamicConcatenatingMediaSource = DynamicConcatenatingMediaSource()

        if (trackList == null || trackList.tracks.isEmpty()) {
            LifeTuneApplication.musicPlayer.tracks = ArrayList()
        } else {
            LifeTuneApplication.musicPlayer.tracks = trackList.tracks

            val mediaSources = ArrayList<MediaSource>()

            trackList.tracks.forEach { mediaSources.add(buildMediaSource(Uri.parse(it.url))) }

            dynamicConcatenatingMediaSource.addMediaSources(mediaSources)
        }

        music_player.player.prepare(dynamicConcatenatingMediaSource)
        music_player.player.playWhenReady = true

        showMusicPlayer()
    }

    private fun currentViewPagerItem(): Int = pager.currentItem

    fun isCurrentRecommendationMusicFragment(): Boolean = currentViewPagerItem() == PlayMusicPagerAdapter.RECOMMENDATION_MUSIC_FRAGMENT

    fun isCurrentFavouriteMusicFragment(): Boolean = currentViewPagerItem() == PlayMusicPagerAdapter.FAVOURITE_MUSIC_FRAGMENT

    private fun buildMediaSource(uri: Uri): ExtractorMediaSource {
        return ExtractorMediaSource(uri, DefaultHttpDataSourceFactory("ua"),
                DefaultExtractorsFactory(), null, null)
    }

    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_music_player)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        app_bar_layout.setExpanded(true, true)
                    }
                }
            }
        })
    }

    private fun setupSearchView() {
        Log.d(TAG, "setupSearchView")

        val mHistoryDatabase = SearchHistoryTable(context)

        search_view.setVoice(false)

        search_view.versionMargins = SearchView.VersionMargins.TOOLBAR_SMALL

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d(TAG, "onQueryTextSubmit: query=$query")

                mHistoryDatabase.addItem(SearchItem(query))
                search_view.close(true)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        search_view.setOnNavigationIconClickListener {
            Log.d(TAG, "search_view.setOnNavigationIconClickListener")

            val drawerLayout = (activity as MainActivity).getDrawerLayout()!!

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    companion object {
        val TAG = PlayMusicFragment::class.java.simpleName
    }
}