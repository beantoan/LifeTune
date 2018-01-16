package it.unical.mat.lifetune.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.unical.mat.lifetune.api.ApiServiceFactory
import it.unical.mat.lifetune.entity.Category

/**
 * Created by beantoan on 1/16/18.
 */
class CategoryPresenter {
    var recommendationCallbacks: RecommendationCallbacks? = null

    constructor(callbacks: RecommendationCallbacks) {
        recommendationCallbacks = callbacks
    }

    fun callRecommendationApi() {
        ApiServiceFactory.createCategoryApi().recommendation()
                .subscribeOn(Schedulers.io()) // "work" on io thread
                .observeOn(AndroidSchedulers.mainThread()) // "listen" on UIThread
                .subscribe(
                        { categories -> recommendationCallbacks?.onRecommendationApiSuccess(categories) },
                        { error -> recommendationCallbacks?.onRecommendationApiError(error) }
                )
    }

    interface RecommendationCallbacks {
        fun onRecommendationApiSuccess(categories: List<Category>)
        fun onRecommendationApiError(error: Throwable)
    }
}