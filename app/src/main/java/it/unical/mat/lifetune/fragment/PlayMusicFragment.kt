package it.unical.mat.lifetune.fragment

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.lapism.searchview.SearchView
import it.unical.mat.lifetune.BuildConfig
import it.unical.mat.lifetune.LifeTuneApplication
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.adapter.PlayMusicPagerAdapter
import it.unical.mat.lifetune.adapter.PlayingTracksAdapter
import it.unical.mat.lifetune.adapter.SearchSongResultsAdapter
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.CommonApiResponse
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.entity.Song
import it.unical.mat.lifetune.entity.Track
import it.unical.mat.lifetune.presenter.PlaylistPresenter
import it.unical.mat.lifetune.presenter.SongPresenter
import it.unical.mat.lifetune.view.CustomSearchView
import it.unical.mat.lifetune.view.PlaybackControlView
import kotlinx.android.synthetic.main.bottom_sheet_music_player.*
import kotlinx.android.synthetic.main.bottom_sheet_search_song_results.*
import kotlinx.android.synthetic.main.fragment_play_music.*


/**
 * Created by beantoan on 12/12/17.
 */
class PlayMusicFragment : BaseFragment(), AppBarLayout.OnOffsetChangedListener {

    private lateinit var mPlayMusicPagerAdapter: PlayMusicPagerAdapter

    private var searchSongResultsAdapter: SearchSongResultsAdapter? = null

    private var playingTrackAdapter: PlayingTracksAdapter? = null

    private var needToPlaySong: Song? = null

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
        Log.d(TAG, "onResumeTasks")

        search_view.close(false)

        updateMediaPlayControl()
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

        setupAppBarLayout()

        setupViewPager()
        
        setupMusicPlayer()

        setupBottomSheetMusicPlayer()

        setupBottomSheetSearchSongResults()
        
        setupSearchView()

        setupRecyclerViewSearchSongResults()

        setupRecyclerViewPlayingTracks()

        setupPlayingPlaylistActions()
    }

    private fun onDestroyTasks() {
        Log.d(TAG, "onDestroyTasks")
    }

    private fun setupAppBarLayout() {
        Log.d(TAG, "setupAppBarLayout")

        app_bar_layout.addOnOffsetChangedListener(this)
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

        music_player.setOnCollapseExpandListener(object : PlaybackControlView.CollapseExpandListener {
            override fun onExpanded() {
                displayTrackList(true)
                expandCollapseAppBar(false)
            }

            override fun onCollapsed() {
                displayTrackList(false)
            }

        })

        if (LifeTuneApplication.musicPlayer.playlist == null) {
            hideMusicPlayer()
        } else {
            showMusicPlayer()
        }
    }

    private fun setupBottomSheetMusicPlayer() {
        Log.d(TAG, "setupBottomSheetMusicPlayer")

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_music_player)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        expandCollapseAppBar(true)
                    }
                }
            }
        })
    }

    private fun setupBottomSheetSearchSongResults() {
        Log.d(TAG, "setupBottomSheetSearchSongResults")

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

    private fun setupRecyclerViewSearchSongResults() {
        Log.d(TAG, "setupRecyclerViewSearchSongResults")

        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.song_divider)
        val dividerItemDecoration = RecyclerViewDividerItemDecoration(activity!!.applicationContext,
                DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_search_song_results.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_search_song_results.addItemDecoration(dividerItemDecoration)

        searchSongResultsAdapter = SearchSongResultsAdapter(this@PlayMusicFragment, ArrayList())
        recycler_view_search_song_results.adapter = searchSongResultsAdapter
    }

    private fun setupRecyclerViewPlayingTracks() {
        Log.d(TAG, "setupRecyclerViewPlayingTracks")

        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.song_divider)
        val dividerItemDecoration = RecyclerViewDividerItemDecoration(activity!!.applicationContext,
                DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_playing_tracks.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_playing_tracks.addItemDecoration(dividerItemDecoration)

        playingTrackAdapter = PlayingTracksAdapter(this@PlayMusicFragment, ArrayList())

        recycler_view_playing_tracks.adapter = playingTrackAdapter
    }

    private fun setupPlayingPlaylistActions() {
        Log.d(TAG, "setupPlayingPlaylistActions")

        like_playing_playlist.setOnClickListener {
            likePlaylist(LifeTuneApplication.musicPlayer.playlist)
        }

        unlike_playing_playlist.setOnClickListener {
            unlikePlaylist(LifeTuneApplication.musicPlayer.playlist)
        }

        share_playing_playlist.setOnClickListener {
            sharePlaylist(LifeTuneApplication.musicPlayer.playlist)
        }
    }

    private fun likePlaylist(playlist: Playlist?) {
        Log.d(TAG, "likePlaylist: ${playlist?.shortLog()}")

        if (playlist != null) {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            PlaylistPresenter(ImplLikePlaylistCallbacks(this)).callLikePlaylistApi(playlist, userId)
        }
    }

    private fun unlikePlaylist(playlist: Playlist?) {
        Log.d(TAG, "unlikePlaylist: ${playlist?.shortLog()}")

        if (playlist != null) {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            PlaylistPresenter(ImplUnlikePlaylistCallbacks(this)).callUnlikePlaylistApi(playlist, userId)
        }
    }

    private fun sharePlaylist(playlist: Playlist?) {
        Log.d(TAG, "sharePlaylist: ${playlist?.shortLog()}")

        if (playlist != null) {
            val content = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(playlist.url))
                    .build()

            val shareDialog = ShareDialog(this)
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC)

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

    fun playSongs(playlist: Playlist?) {
        Log.d(TAG, "playSongs: ${playlist?.shortLog()}, needToPlaySong=${needToPlaySong?.shortLog()}")

        music_player.player.stop()

        if (playlist == null || playlist.tracks.isEmpty()) {
            LifeTuneApplication.musicPlayer.playlist = null

            LifeTuneApplication.musicPlayer.stop()

            hideMusicPlayer()
        } else {
            LifeTuneApplication.musicPlayer.playlist = playlist

            val mediaSources = ArrayList<MediaSource>()

            var needToPlayTrackPosition = -1

            playlist.tracks.forEachIndexed { index, track ->

                Log.d(TAG, "track=${track.shortLog()}")
                mediaSources.add(buildMediaSource(Uri.parse(track.url)))

                if (needToPlaySong != null && needToPlayTrackPosition == -1
                        && track.songKey == needToPlaySong?.code) {
                    needToPlayTrackPosition = index
                }
            }

            val concatenatingMediaSource = ConcatenatingMediaSource(*mediaSources.toTypedArray())

            showMusicPlayer()

            music_player.player.prepare(concatenatingMediaSource)
            music_player.player.playWhenReady = true

            if (needToPlayTrackPosition > -1) {
                playTrackAtPosition(needToPlayTrackPosition)
            }
        }

        needToPlaySong = null

        updateMediaPlayControl()
    }

    private fun updateMediaPlayControl() {
        updatePlayingTrackAdapter(LifeTuneApplication.musicPlayer.playlist?.tracks)

        updateLikeUnlikeButton(LifeTuneApplication.musicPlayer.playlist)

        playlist_playlist_title.text = LifeTuneApplication.musicPlayer.playlist?.title
        playlist_playlist_title.isSelected = true
        playlist_playlist_title.marqueeRepeatLimit = Animation.INFINITE
    }

    private fun activeViewPagerItem(): Int = pager.currentItem

    fun isRecommendationMusicFragmentActive(): Boolean =
            activeViewPagerItem() == PlayMusicPagerAdapter.RECOMMENDATION_MUSIC_FRAGMENT

    fun isFavouriteMusicFragmentActive():
            Boolean = activeViewPagerItem() == PlayMusicPagerAdapter.FAVOURITE_MUSIC_FRAGMENT

    private fun buildMediaSource(uri: Uri): ExtractorMediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, BuildConfig.APPLICATION_ID), DefaultBandwidthMeter())

        return ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
    }

    private fun expandCollapseAppBar(isExpand: Boolean) {
        Log.d(TAG, "expandCollapseAppBar: isExpand=$isExpand")

        when (isExpand) {
            false -> {
                app_bar_layout.setExpanded(false, true)
                search_view.visibility = View.GONE
            }
            true -> {
                app_bar_layout.setExpanded(true, true)
                search_view.visibility = View.VISIBLE
            }
        }
    }

    private fun setScrollFragsForAppBarLayout(isScroll: Boolean) {
        Log.d(TAG, "setScrollFragsForAppBarLayout: isScroll=$isScroll")

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

                SongPresenter(ImplSearchCallbacks(this@PlayMusicFragment)).callSearchApi(query)

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

    private fun onSearchSongsError(error: Throwable) {
        Log.e(TAG, "onSearchSongsError", error)
    }

    private fun displaySearchSongResults(isShown: Boolean) {
        Log.d(TAG, "displaySearchSongResults: isShown=$isShown")

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_search_song_results)

        bottomSheetBehavior.state = when (isShown) {
            true -> BottomSheetBehavior.STATE_EXPANDED
            false -> BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun onSearchSongsSuccess(songs: List<Song>) {
        Log.d(TAG, "onSearchSongsSuccess: songs.size=${songs.size}")

        updateSearchSongResultAdapter(songs)

        displaySearchSongResults(true)
    }

    private fun updateSearchSongResultAdapter(songs: List<Song>) {
        Log.d(TAG, "updateSearchSongResultAdapter: songs.size=${songs.size}")

        searchSongResultsAdapter?.addAll(songs)
    }

    private fun updatePlayingTrackAdapter(tracks: List<Track>?) {
        Log.d(TAG, "updatePlayingTrackAdapter: tracks.size=${tracks?.size}")

        if (tracks == null) {
            playingTrackAdapter?.clear()
        } else {
            playingTrackAdapter?.addAll(tracks)
        }
    }

    private fun displayTrackList(isShown: Boolean) {
        Log.d(TAG, "displayTrackList: isShown=$isShown")

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_music_player)

        bottomSheetBehavior.state = when (isShown) {
            true -> BottomSheetBehavior.STATE_EXPANDED
            false -> BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun updateLikeUnlikeButton(playlist: Playlist?) {
        Log.d(TAG, "updateLikeUnlikeButton: playlist.isLiked=${playlist?.isLiked}")
        
        if (playlist == null) {
            playing_playlist_actions.visibility = View.GONE
        } else {
            playing_playlist_actions.visibility = View.VISIBLE

            like_playing_playlist.visibility = if (playlist.isLiked) View.GONE else View.VISIBLE
            unlike_playing_playlist.visibility = if (playlist.isLiked) View.VISIBLE else View.GONE
        }
    }

    private fun onLikePlaylistError(playlist: Playlist) {
        val msg = activity!!.getString(R.string.liked_playlist_unsuccessfully_message, playlist.title)

        Snackbar.make(main_content, msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun onLikePlaylistSuccess(commonApiResponse: CommonApiResponse, playlist: Playlist) {

        if (commonApiResponse.isOk()) {
            playlist.isLiked = true
            playlist.notifyChange()

            updateLikeUnlikeButton(playlist)

            val msg = activity!!.getString(R.string.liked_playlist_successfully_message, playlist.title)

            Snackbar.make(main_content, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onUnlikePlaylistSuccess(commonApiResponse: CommonApiResponse, playlist: Playlist) {

        if (commonApiResponse.isOk()) {
            playlist.isLiked = false
            playlist.notifyChange()

            updateLikeUnlikeButton(playlist)

            val msg = activity!!.getString(R.string.unliked_playlist_successfully_message, playlist.title)

            Snackbar.make(main_content, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onUnlikePlaylistError(playlist: Playlist) {
        val msg = activity!!.getString(R.string.unliked_playlist_unsuccessfully_message, playlist.title)

        Snackbar.make(main_content, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun displayProgressBar(isShown: Boolean) {
        displayProgressBar(progress_bar, isShown)
    }

    fun playTrackAtPosition(position: Int) {
        Log.d(TAG, "playTrackAtPosition: position=$position")

        LifeTuneApplication.musicPlayer.seekTo(position, C.TIME_UNSET)
    }

    fun playFoundSong(song: Song?) {
        search_view.close(true)

        displaySearchSongResults(false)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        SongPresenter(ImplPlaylistCallbacks(this)).callPlaylistApi(song!!, userId)
    }

    private fun onPlaylistSuccess(song: Song, playlist: Playlist) {
        Log.d(TAG, "onPlaylistSuccess: ${song.shortLog()}, ${playlist.shortLog()}")

        needToPlaySong = song

        val musicFragment = mPlayMusicPagerAdapter.getMusicFragment(PlayMusicPagerAdapter.RECOMMENDATION_MUSIC_FRAGMENT)

        musicFragment.callSongsApi(playlist)
    }

    private fun onPlaylistError(song: Song) {

    }

    companion object {
        val TAG = PlayMusicFragment::class.java.simpleName
    }

    private class ImplLikePlaylistCallbacks(val playMusicFragment: PlayMusicFragment) : PlaylistPresenter.LikePlaylistCallbacks {
        companion object {
            val TAG = ImplLikePlaylistCallbacks::class.java.simpleName
        }

        override fun onLikePlaylistSuccess(commonApiResponse: CommonApiResponse, playlist: Playlist) {
            Log.d(TAG, "onLikePlaylistSuccess: commonApiResponse=$commonApiResponse, playlist=${playlist.shortLog()}")

            playMusicFragment.onLikePlaylistSuccess(commonApiResponse, playlist)
        }

        override fun onLikePlaylistError(error: Throwable, playlist: Playlist) {
            Log.e(TAG, "onLikePlaylistError", error)

            playMusicFragment.onLikePlaylistError(playlist)
        }
    }

    private class ImplUnlikePlaylistCallbacks(val playMusicFragment: PlayMusicFragment) : PlaylistPresenter.UnlikePlaylistCallbacks {
        companion object {
            val TAG = ImplUnlikePlaylistCallbacks::class.java.simpleName
        }

        override fun onUnlikePlaylistSuccess(commonApiResponse: CommonApiResponse, playlist: Playlist) {
            Log.d(TAG, "onLikePlaylistSuccess: commonApiResponse=$commonApiResponse, playlist=${playlist.shortLog()}")

            playMusicFragment.onUnlikePlaylistSuccess(commonApiResponse, playlist)
        }

        override fun onUnlikePlaylistError(error: Throwable, playlist: Playlist) {
            Log.e(TAG, "onUnlikePlaylistError", error)

            playMusicFragment.onUnlikePlaylistError(playlist)
        }
    }

    private class ImplSearchCallbacks(val playMusicFragment: PlayMusicFragment) : SongPresenter.SearchCallbacks {
        override fun onSearchSuccess(songs: List<Song>) {
            playMusicFragment.onSearchSongsSuccess(songs)
        }

        override fun onSearchError(error: Throwable) {
            playMusicFragment.onSearchSongsError(error)
        }
    }

    private class ImplPlaylistCallbacks(val playMusicFragment: PlayMusicFragment) : SongPresenter.PlaylistCallbacks {
        override fun onPlaylistSuccess(song: Song, playlist: Playlist) {
            playMusicFragment.onPlaylistSuccess(song, playlist)
        }

        override fun onPlaylistError(error: Throwable, song: Song) {
            playMusicFragment.onPlaylistError(song)
        }

    }
}