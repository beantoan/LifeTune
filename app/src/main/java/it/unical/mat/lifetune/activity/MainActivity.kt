package it.unical.mat.lifetune.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import butterknife.BindView
import butterknife.ButterKnife
import com.firebase.ui.auth.IdpResponse
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.adapter.AppSectionsPagerAdapter


class MainActivity : AppCompatActivity(), ActionBar.TabListener {

    private lateinit var mAppSectionsPagerAdapter: AppSectionsPagerAdapter

    private lateinit var mActionBar : ActionBar

    @BindView(R.id.pager)
    lateinit var mViewPager: ViewPager

    @BindView(R.id.music_player)
    lateinit var mMusicPlayer: SimpleExoPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onCreateTasks()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.options_menu, menu)

        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        onDestroyTasks()
    }

    override fun onTabReselected(tab: ActionBar.Tab?, ft: FragmentTransaction?) {

    }

    override fun onTabUnselected(tab: ActionBar.Tab?, ft: FragmentTransaction?) {

    }

    override fun onTabSelected(tab: ActionBar.Tab?, ft: FragmentTransaction?) {
        mViewPager.currentItem = tab!!.position
    }

    private fun onCreateTasks() {
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        setupActionBar()

        setupViewPager()

        setupMusicPlayer()
    }

    private fun onDestroyTasks() {
        mMusicPlayer.player.release()
    }

    private fun setupActionBar() {
        // Set up the action bar.
        mActionBar = supportActionBar!!

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        mActionBar.setHomeButtonEnabled(false)

        // Specify that we will be displaying tabs in the action bar.
        mActionBar.navigationMode = ActionBar.NAVIGATION_MODE_TABS
    }

    private fun setupViewPager() {
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = AppSectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager.adapter = mAppSectionsPagerAdapter
        mViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                mActionBar.setSelectedNavigationItem(position)
            }
        })

        // For each of the sections in the app, add a tab to the action bar.
        (0 until mAppSectionsPagerAdapter.count).forEach { i ->
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            mActionBar.addTab(
                    mActionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this))
        }
    }

    private fun setupMusicPlayer() {

        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        mMusicPlayer.player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

//        mMusicPlayer.player = ExoPlayerFactory.newSimpleInstance(
//                DefaultRenderersFactory(this),
//                DefaultTrackSelector(),
//                DefaultLoadControl()
//        )

        playMusic(dummyMediaSources())
    }

    private fun playMusic(dynamicConcatenatingMediaSource: DynamicConcatenatingMediaSource) {
        mMusicPlayer.player.prepare(dynamicConcatenatingMediaSource)
        mMusicPlayer.player.playWhenReady = true
    }

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

    companion object {

        private val TAG = MainActivity::class.java.canonicalName

        private val EXTRA_IDP_RESPONSE = "extra_idp_response"

        fun createIntent(context: Context, idpResponse: IdpResponse?): Intent {

            val startIntent = Intent()

            if (idpResponse != null) {
                startIntent.putExtra(EXTRA_IDP_RESPONSE, idpResponse)
            }

            return startIntent.setClass(context, MainActivity::class.java)
        }
    }
}
