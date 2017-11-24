package it.unical.mat.lifetune.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import butterknife.BindView
import butterknife.ButterKnife
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.firebase.ui.auth.IdpResponse
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.adapter.AppSectionsPagerAdapter
import it.unical.mat.lifetune.data.ColorSuggestion
import it.unical.mat.lifetune.data.DataHelper


class MainActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {
    private lateinit var mAppSectionsPagerAdapter: AppSectionsPagerAdapter

    @BindView(R.id.tabs)
    lateinit var mTabLayout: TabLayout

    @BindView(R.id.pager)
    lateinit var mViewPager: ViewPager

    @BindView(R.id.music_player)
    lateinit var mMusicPlayer: SimpleExoPlayerView

    @BindView(R.id.floating_search_view)
    lateinit var mFloatingSearchView: FloatingSearchView

    @BindView(R.id.app_bar_layout)
    lateinit var mAppBarLayout: AppBarLayout

    private var mLastQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onCreateTasks()

        setupFloatingSearchView()
    }

    override fun onDestroy() {
        super.onDestroy()

        onDestroyTasks()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        mFloatingSearchView.translationY = verticalOffset.toFloat()
    }

    private fun onCreateTasks() {
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        setupViewPager()

        setupMusicPlayer()

        mAppBarLayout.addOnOffsetChangedListener(this)
    }

    private fun onDestroyTasks() {
        mMusicPlayer.player.release()
    }

    private fun setupViewPager() {
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = AppSectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager.adapter = mAppSectionsPagerAdapter

        mTabLayout.setupWithViewPager(mViewPager)
    }

    private fun setupMusicPlayer() {
        mMusicPlayer.player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(),
                DefaultLoadControl()
        )

        playMusic(dummyMediaSources())
    }

    private fun playMusic(dynamicConcatenatingMediaSource: DynamicConcatenatingMediaSource) {
        mMusicPlayer.player.prepare(dynamicConcatenatingMediaSource)
        mMusicPlayer.player.playWhenReady = true
    }

    // TODO need to add real data source
    private fun dummyMediaSources() : DynamicConcatenatingMediaSource {
        val dynamicConcatenatingMediaSource = DynamicConcatenatingMediaSource()
        val mediaSources = ArrayList<MediaSource>()

        val songUrls = arrayListOf<String>(
                "http://test.flanet.vn/1.mp3", "http://test.flanet.vn/2.mp3"
        )

        songUrls.forEach { mediaSources.add(buildMediaSource(Uri.parse(it))) }

        dynamicConcatenatingMediaSource.addMediaSources(mediaSources)

        return dynamicConcatenatingMediaSource
    }

    private fun buildMediaSource(uri: Uri) : MediaSource {
      return ExtractorMediaSource(uri, DefaultHttpDataSourceFactory("ua"),
              DefaultExtractorsFactory(), null, null)
    }

    private fun setupFloatingSearchView() {
        mFloatingSearchView.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery.isNotBlank() && newQuery.isBlank()) {
                mFloatingSearchView.clearSuggestions()
            } else {

                //this shows the top left circular progress
                //you can call it where ever you want, but
                //it makes sense to do it when loading something in
                //the background.
                mFloatingSearchView.showProgress()

                //simulates a query call to a data source
                //with a new query.
                DataHelper.findSuggestions(this@MainActivity, newQuery, 5,
                        FIND_SUGGESTION_SIMULATED_DELAY) { results ->
                    Log.d(TAG, "setupFloatingSearchView#setOnQueryChangeListener")

                    //this will swap the data and
                    //render the collapse/expand animations as necessary
                    mFloatingSearchView.swapSuggestions(results)

                    //let the users know that the background
                    //process has completed
                    mFloatingSearchView.hideProgress()
                }
            }
        }

        mFloatingSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSearchAction(currentQuery: String?) {

                mLastQuery = currentQuery!!

                DataHelper.findColors(this@MainActivity, currentQuery
                ) { results ->
                    Log.d(TAG, "setupFloatingSearchView#setOnSearchListener")
                }
            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {

                val colorSuggestion = searchSuggestion as ColorSuggestion

                DataHelper.findColors(this@MainActivity, colorSuggestion.body
                ) { results ->
                    Log.d(TAG, "setupFloatingSearchView#setOnSearchListener")
                }

                mLastQuery = searchSuggestion.getBody()
            }

        })

        mFloatingSearchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
            override fun onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                mFloatingSearchView.swapSuggestions(DataHelper.getHistory(this@MainActivity, 3))
            }

            override fun onFocusCleared() {
                //set the title of the bar so that when focus is returned a new query begins
                mFloatingSearchView.setSearchBarTitle(mLastQuery)
            }
        })

    }

    companion object {

        private val TAG = MainActivity::class.java.canonicalName

        private val EXTRA_IDP_RESPONSE = "extra_idp_response"

        private val FIND_SUGGESTION_SIMULATED_DELAY = 250L

        fun createIntent(context: Context, idpResponse: IdpResponse?): Intent {

            val startIntent = Intent()

            if (idpResponse != null) {
                startIntent.putExtra(EXTRA_IDP_RESPONSE, idpResponse)
            }

            return startIntent.setClass(context, MainActivity::class.java)
        }
    }
}
