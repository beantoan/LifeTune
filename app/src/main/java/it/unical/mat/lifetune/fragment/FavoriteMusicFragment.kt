package it.unical.mat.lifetune.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thedeanda.lorem.LoremIpsum
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.controller.MusicController
import it.unical.mat.lifetune.decoration.CategoryDividerItemDecoration
import it.unical.mat.lifetune.entity.Category
import it.unical.mat.lifetune.entity.Playlist
import kotlinx.android.synthetic.main.fragment_favorite_music.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by beantoan on 11/17/17.
 */
class FavoriteMusicFragment : Fragment(), MusicController.AdapterCallbacks {

    lateinit var musicController: MusicController

    private var categories: List<Category> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onPlaylistClicked(category: Category?, position: Int) {

    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        setupRecyclerViewCategories()

        setupMusicController()

        updateMusicController(dummyPlaylistData())
    }

    private fun setupRecyclerViewCategories() {
        Log.d(TAG, "setupRecyclerViewCategories")

        val dividerDrawable = ContextCompat.getDrawable(context!!, R.drawable.category_divider)
        val dividerItemDecoration = CategoryDividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_categories.layoutManager = LinearLayoutManager(context)
        recycler_view_categories.addItemDecoration(dividerItemDecoration)
    }

    private fun setupMusicController() {
        Log.d(TAG, "setupMusicController")
        musicController = MusicController(this)

        recycler_view_categories.clear()
        recycler_view_categories.setController(musicController)
    }

    private fun updateMusicController(data: List<Category>) {
        musicController.setData(data)
    }

    // TODO add temporary data
    private fun dummyPlaylistData(): List<Category> {
        val data = ArrayList<Category>()

        val lorem = LoremIpsum.getInstance()

        val images = arrayOf(
                "https://hdwallsource.com/img/2013/19/anime-girls-2426.jpg",
                "http://animefanatika.co.za/afwp/wp-content/uploads/2016/01/2015-cover.jpg",
                "https://vignette.wikia.nocookie.net/date-a-live/images/e/e0/MA048001_1.png/revision/latest?cb=20130704113347",
                "https://www.w3schools.com/w3css/img_fjords.jpg",
                "http://www.ptahai.com/wp-content/uploads/2016/06/Best-Reverse-Image-Search-Engines-Apps-And-Its-Uses-2016.jpg",
                "https://www.smashingmagazine.com/wp-content/uploads/2015/06/10-dithering-opt.jpg"
        )

        val countImages = images.size

        (0..3).forEach { i ->
            val playlists = (0..5).map {
                Playlist(it, lorem.getTitle(3, 5), "xxxurl", images[Random().nextInt(countImages)])
            }

            data.add(Category(i, "$i - ${lorem.getTitle(2, 4)}", lorem.getTitle(5, 8), playlists))
        }

        return data
    }

    companion object {
        private val TAG = FavoriteMusicFragment::class.java.canonicalName
    }
}
