package it.unical.mat.lifetune.fragment

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.adapter.PlayMusicPagerAdapter
import it.unical.mat.lifetune.data.ColorSuggestion
import it.unical.mat.lifetune.data.DataHelper
import it.unical.mat.lifetune.entity.Song
import kotlinx.android.synthetic.main.fragment_play_music.*

/**
 * Created by beantoan on 12/12/17.
 */
class PlayMusicFragment : Fragment(),
        AppBarLayout.OnOffsetChangedListener {

    private lateinit var mPlayMusicPagerAdapter: PlayMusicPagerAdapter

    private var mLastQuery = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_play_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onViewCreatedTasks()
    }

    override fun onDestroy() {
        super.onDestroy()

        onDestroyTasks()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        floating_search_view.translationY = verticalOffset.toFloat()
    }

    private fun onViewCreatedTasks() {
        Log.d(TAG, "onViewCreatedTasks")
        
        setupViewPager()

        setupFloatingSearchView()

        app_bar_layout.addOnOffsetChangedListener(this)

        floating_search_view.attachNavigationDrawerToMenuButton((activity as MainActivity).getDrawerLayout()!!)

        setupMusicPlayer()
    }

    private fun onDestroyTasks() {
        Log.d(TAG, "onDestroyTasks")

        music_player.player.release()
    }

    private fun setupViewPager() {
        mPlayMusicPagerAdapter = PlayMusicPagerAdapter(this, activity!!.supportFragmentManager)

        pager.adapter = mPlayMusicPagerAdapter

        tabs.setupWithViewPager(pager)
    }

    private fun setupMusicPlayer() {
        music_player.player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(activity),
                DefaultTrackSelector(),
                DefaultLoadControl()
        )
    }

    private fun playMusic(dynamicConcatenatingMediaSource: DynamicConcatenatingMediaSource) {
        music_player.player.prepare(dynamicConcatenatingMediaSource)
        music_player.player.playWhenReady = true
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

    fun playSongs(songs: List<Song>) {
        music_player.player.stop()

        if (songs.isEmpty()) {
            this.hideMusicPlayer()
        } else {
            this.showMusicPlayer()

            val dynamicConcatenatingMediaSource = DynamicConcatenatingMediaSource()
            val mediaSources = ArrayList<MediaSource>()

            songs.forEach { mediaSources.add(buildMediaSource(Uri.parse(it.mp3_url))) }

            dynamicConcatenatingMediaSource.addMediaSources(mediaSources)

            playMusic(dynamicConcatenatingMediaSource)
        }
    }

    private fun buildMediaSource(uri: Uri): ExtractorMediaSource {
        return ExtractorMediaSource(uri, DefaultHttpDataSourceFactory("ua"),
                DefaultExtractorsFactory(), null, null)
    }

    private fun setupFloatingSearchView() {
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

    companion object {
        val TAG = PlayMusicFragment::class.java.canonicalName

        private val FIND_SUGGESTION_SIMULATED_DELAY = 250L
    }

}