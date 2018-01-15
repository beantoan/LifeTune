package it.unical.mat.lifetune.fragment

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.lapism.searchview.SearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.LifeTuneApplication
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.adapter.PlayMusicPagerAdapter
import it.unical.mat.lifetune.adapter.SearchSongResultsAdapter
import it.unical.mat.lifetune.api.ApiServiceFactory
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.Song
import it.unical.mat.lifetune.entity.TrackList
import it.unical.mat.lifetune.view.CustomSearchView
import kotlinx.android.synthetic.main.fragment_play_music.*


/**
 * Created by beantoan on 12/12/17.
 */
class PlayMusicFragment : Fragment(),
        AppBarLayout.OnOffsetChangedListener {

    private lateinit var mPlayMusicPagerAdapter: PlayMusicPagerAdapter

    private val searchSongResultsAdapter = SearchSongResultsAdapter(ArrayList())


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

        setupBottomSheetSearchSongResults()
        
        setupSearchView()

        setupRecycleSearchSongResults()
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

    private fun setupBottomSheetSearchSongResults() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_search_song_results)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        setScrollFragsForAppBarLayout(false)
                    }
                    else -> {
                        setScrollFragsForAppBarLayout(true)
                    }
                }
            }
        })
    }

    private fun setScrollFragsForAppBarLayout(isScroll: Boolean) {
        val layoutParams = floating_search_view_placeholder.layoutParams as AppBarLayout.LayoutParams

        when (isScroll) {
            true -> {
                layoutParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                        AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
                        AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
            }
            false -> {
                layoutParams.scrollFlags = 0
            }
        }

        app_bar_layout.setExpanded(true, true)
    }

    private fun setupSearchView() {
        Log.d(TAG, "setupSearchView")

        search_view.setVoice(false)

        search_view.versionMargins = SearchView.VersionMargins.TOOLBAR_SMALL

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d(TAG, "onQueryTextSubmit: query=$query")

                search_view.close(true)

                ApiServiceFactory.createSongApi().search(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { songs -> onSearchSongsSuccess(songs) },
                                { error -> }
                        )

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        search_view.onClearSearchInputListener = object : CustomSearchView.OnClearSearchInputListener {
            override fun onClear() {
                updateSearchSongResultAdapter(ArrayList())
                displaySearchSongResults(false)
            }
        }

        search_view.setOnOpenCloseListener(object : SearchView.OnOpenCloseListener {
            override fun onOpen(): Boolean {
                val bottomSheetMusicPlayerBehavior = BottomSheetBehavior.from(bottom_sheet_music_player)

                bottomSheetMusicPlayerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                return true
            }

            override fun onClose(): Boolean {
                return true
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

    private fun setupRecycleSearchSongResults() {
        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.song_divider)
        val dividerItemDecoration = RecyclerViewDividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_search_song_results.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_search_song_results.addItemDecoration(dividerItemDecoration)

        recycler_view_search_song_results.adapter = searchSongResultsAdapter
    }

    private fun displaySearchSongResults(isShown: Boolean) {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_search_song_results)

        bottomSheetBehavior.state = when (isShown) {
            true -> BottomSheetBehavior.STATE_EXPANDED
            false -> BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun onSearchSongsSuccess(songs: List<Song>) {
        updateSearchSongResultAdapter(songs)

        displaySearchSongResults(true)
    }

    private fun updateSearchSongResultAdapter(songs: List<Song>) {
        searchSongResultsAdapter.addAll(songs)
    }

    companion object {
        val TAG = PlayMusicFragment::class.java.simpleName
    }
}