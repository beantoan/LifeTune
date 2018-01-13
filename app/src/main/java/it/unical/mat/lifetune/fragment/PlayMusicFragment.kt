package it.unical.mat.lifetune.fragment

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import it.unical.mat.lifetune.LifeTuneApplication
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.adapter.PlayMusicPagerAdapter
import it.unical.mat.lifetune.data.ColorSuggestion
import it.unical.mat.lifetune.data.DataHelper
import it.unical.mat.lifetune.entity.TrackList
import kotlinx.android.synthetic.main.fragment_play_music.*


/**
 * Created by beantoan on 12/12/17.
 */
class PlayMusicFragment : Fragment(),
        AppBarLayout.OnOffsetChangedListener {

    private lateinit var mPlayMusicPagerAdapter: PlayMusicPagerAdapter

    private var mLastQuery = ""

    private var floatingSearchViewTranslationY = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_play_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        onViewCreatedTasks()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        super.onDestroy()

        onDestroyTasks()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        floatingSearchViewTranslationY = verticalOffset.toFloat()
        floating_search_view.translationY = verticalOffset.toFloat()
    }

    private fun onViewCreatedTasks() {
        Log.d(TAG, "onViewCreatedTasks")

        setupViewPager()

        setupFloatingSearchView()

        app_bar_layout.addOnOffsetChangedListener(this)

        setupMusicPlayer()

        setupBottomSheet()
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
        music_player.layoutParams.height = when {
            isShown -> resources.getDimension(R.dimen.music_player_height).toInt()
            else -> 0
        }
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

    fun currentViewPagerItem(): Int = pager.currentItem

    fun isCurrentRecommendationMusicFragment(): Boolean = currentViewPagerItem() == PlayMusicPagerAdapter.RECOMMENDATION_MUSIC_FRAGMENT

    fun isCurrentFavouriteMusicFragment(): Boolean = currentViewPagerItem() == PlayMusicPagerAdapter.FAVOURITE_MUSIC_FRAGMENT

    private fun buildMediaSource(uri: Uri): ExtractorMediaSource {
        return ExtractorMediaSource(uri, DefaultHttpDataSourceFactory("ua"),
                DefaultExtractorsFactory(), null, null)
    }

    private fun setupFloatingSearchView() {
        Log.d(TAG, "setupFloatingSearchView")

        val drawerLayout = (activity as MainActivity).getDrawerLayout()!!

        floating_search_view.attachNavigationDrawerToMenuButton(drawerLayout)

        floating_search_view.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery.isNotBlank() && newQuery.isBlank()) {
                floating_search_view.clearSuggestions()
            } else {
                floating_search_view.showProgress()

                DataHelper.findSuggestions(activity, newQuery, 5,
                        PlayMusicFragment.FIND_SUGGESTION_SIMULATED_DELAY) { results ->
                    Log.d(PlayMusicFragment.TAG, "setupFloatingSearchView#setOnQueryChangeListener")

                    floating_search_view.swapSuggestions(results)

                    floating_search_view.hideProgress()
                }
            }
        }

        floating_search_view.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSearchAction(currentQuery: String?) {

                mLastQuery = currentQuery!!

                DataHelper.findColors(activity, currentQuery
                ) { results ->
                    Log.d(PlayMusicFragment.TAG, "setupFloatingSearchView#setOnSearchListener")
                }
            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {

                val colorSuggestion = searchSuggestion as ColorSuggestion

                DataHelper.findColors(activity, colorSuggestion.body
                ) { results ->
                    Log.d(PlayMusicFragment.TAG, "setupFloatingSearchView#setOnSearchListener")
                }

                mLastQuery = searchSuggestion.getBody()
            }

        })

        floating_search_view.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() {

                floating_search_view.swapSuggestions(DataHelper.getHistory(activity, 3))
            }

            override fun onFocusCleared() {
                floating_search_view.setSearchBarTitle(mLastQuery)
            }
        })
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

    companion object {
        val TAG = PlayMusicFragment::class.java.simpleName

        private val FIND_SUGGESTION_SIMULATED_DELAY = 250L
    }
}