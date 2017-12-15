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
import it.unical.mat.lifetune.controller.RecommendationMusicController
import it.unical.mat.lifetune.decoration.RecyclerViewDividerItemDecoration
import it.unical.mat.lifetune.entity.Category
import it.unical.mat.lifetune.entity.Playlist
import it.unical.mat.lifetune.service.ApiServiceFactory
import it.unical.mat.lifetune.service.CategoryServiceInterface
import it.unical.mat.lifetune.util.AppDialog
import it.unical.mat.lifetune.util.AppUtils
import kotlinx.android.synthetic.main.fragment_recommended_music.*


/**
 * Created by beantoan on 11/17/17.
 */
class RecommendedMusicFragment : BaseMusicFragment(), RecommendationMusicController.AdapterCallbacks {

    private lateinit var controller: RecommendationMusicController

    private var categories: List<Category> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommended_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreateViewTasks(view)
    }

    override fun onPlaylistClicked(playlist: Playlist, position: Int) {
        callPlaylistSongsService()
    }

    private fun onCreateViewTasks(view: View) {
        Log.d(TAG, "onCreateViewTasks")

        setupRecyclerViewCategories()

        setupMusicController()

        callRecommendationCategoriesService()
    }

    private fun setupRecyclerViewCategories() {
        Log.d(TAG, "setupRecyclerViewCategories")

        val dividerDrawable = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.category_divider)
        val dividerItemDecoration = RecyclerViewDividerItemDecoration(activity!!.applicationContext, DividerItemDecoration.VERTICAL, dividerDrawable!!)

        recycler_view_categories.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        recycler_view_categories.addItemDecoration(dividerItemDecoration)
    }

    private fun setupMusicController() {
        Log.d(TAG, "setupMusicController")
        controller = RecommendationMusicController(this)

        recycler_view_categories.clear()
        recycler_view_categories.setController(controller)
    }

    private fun updateMusicController(data: List<Category>) {
        controller.setData(data)
    }

    private fun callRecommendationCategoriesService() {
        if (categories.isEmpty()) {
            if (AppUtils.isInternetConnected(activity!!.applicationContext)) {
                val categoryService = ApiServiceFactory.create(CategoryServiceInterface::class.java)

                getCompositeDisposable().add(
                        categoryService.recommendation()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { categories -> showCategories(categories) },
                                        { error ->
                                            Log.e(TAG, "callRecommendationCategoriesService", error)

                                            showCategories(ArrayList())

                                            AppDialog.error(R.string.api_service_error_title, R.string.api_service_error_message, activity!!)
                                        }
                                )
                )
            } else {
                AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
            }
        }
    }

    private fun showCategories(_categories: List<Category>) {
        categories = _categories

        updateMusicController(categories)

        if (categories.isEmpty()) {
            this.playMusicFragment.hideMusicPlayer()
        } else {
            this.playMusicFragment.showMusicPlayer()
        }

        AppDialog.hideProgress(activity!!)
    }

    private fun callPlaylistSongsService() {
        if (AppUtils.isInternetConnected(activity!!.applicationContext)) {
            getCompositeDisposable().add(
                    ApiServiceFactory.createCategoryService().recommendation()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { categories -> showCategories(categories) },
                                    { error ->
                                        Log.e(TAG, "callRecommendationCategoriesService", error)

                                        showCategories(ArrayList())

                                        AppDialog.error(R.string.api_service_error_title, R.string.api_service_error_message, activity!!)
                                    }
                            )
            )
        } else {
            AppDialog.error(R.string.no_internet_error_title, R.string.no_internet_error_message, activity!!)
        }
    }

    companion object {
        private val TAG = RecommendedMusicFragment::class.java.canonicalName

        fun newInstance(playMusicFragment: PlayMusicFragment): RecommendedMusicFragment {
            val fragment = RecommendedMusicFragment()

            fragment.playMusicFragment = playMusicFragment

            return fragment
        }
    }
}
