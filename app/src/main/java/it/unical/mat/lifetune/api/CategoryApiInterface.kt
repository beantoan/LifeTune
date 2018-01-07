package it.unical.mat.lifetune.api

import io.reactivex.Flowable
import it.unical.mat.lifetune.entity.Category
import retrofit2.http.GET

/**
 * Created by beantoan on 5/14/16.
 */
interface CategoryApiInterface {
    @GET("categories.json")
    fun index(): Flowable<List<Category>>

    @GET("categories/recommendation.json")
    fun recommendation(): Flowable<List<Category>>
}
