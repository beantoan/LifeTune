package it.unical.mat.lifetune.fragment

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
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
        setupViewPager()

        setupMusicPlayer()

        setupFloatingSearchView()

        app_bar_layout.addOnOffsetChangedListener(this)

        floating_search_view.attachNavigationDrawerToMenuButton((activity as MainActivity).getDrawerLayout()!!)
    }

    private fun onDestroyTasks() {
        music_player.player.release()
    }

    private fun setupViewPager() {
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mPlayMusicPagerAdapter = PlayMusicPagerAdapter(this, activity!!.supportFragmentManager)

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        pager.adapter = mPlayMusicPagerAdapter

        tabs.setupWithViewPager(pager)
    }

    private fun setupMusicPlayer() {
        music_player.player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(activity),
                DefaultTrackSelector(),
                DefaultLoadControl()
        )

        hideMusicPlayer()
    }

    private fun playMusic(dynamicConcatenatingMediaSource: DynamicConcatenatingMediaSource) {
        music_player.player.prepare(dynamicConcatenatingMediaSource)
        music_player.player.playWhenReady = true
    }

    private fun displayMusicPlayer(isShown: Boolean) = when {
        isShown -> music_player.visibility = VISIBLE
        else -> music_player.visibility = INVISIBLE
    }

    fun showMusicPlayer() {
        displayMusicPlayer(true)
    }

    fun hideMusicPlayer() {
        displayMusicPlayer(false)
    }

    fun playSongs(songs: List<Song>) {
        if (songs.isEmpty()) {
            this.hideMusicPlayer()
        } else {
            this.showMusicPlayer()

            val dynamicConcatenatingMediaSource = DynamicConcatenatingMediaSource()
            val mediaSources = ArrayList<MediaSource>()

            songs.forEach { mediaSources.add(buildMediaSource(Uri.parse(it.url))) }

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

                //this shows the top left circular progress
                //you can call it where ever you want, but
                //it makes sense to do it when loading something in
                //the background.
                floating_search_view.showProgress()

                //simulates a query call to a data source
                //with a new query.
                DataHelper.findSuggestions(activity, newQuery, 5,
                        PlayMusicFragment.FIND_SUGGESTION_SIMULATED_DELAY) { results ->
                    Log.d(PlayMusicFragment.TAG, "setupFloatingSearchView#setOnQueryChangeListener")

                    //this will swap the data and
                    //render the collapse/expand animations as necessary
                    floating_search_view.swapSuggestions(results)

                    //let the users know that the background
                    //process has completed
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

                //show suggestions when search bar gains focus (typically history suggestions)
                floating_search_view.swapSuggestions(DataHelper.getHistory(activity, 3))
            }

            override fun onFocusCleared() {
                //set the title of the bar so that when focus is returned a new query begins
                floating_search_view.setSearchBarTitle(mLastQuery)
            }
        })
    }

    companion object {
        val TAG = PlayMusicFragment::class.java.canonicalName

        private val FIND_SUGGESTION_SIMULATED_DELAY = 250L
    }

}