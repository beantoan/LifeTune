package it.unical.mat.lifetune.service

import io.reactivex.Single
import it.unical.mat.lifetune.entity.Category
import retrofit2.http.GET

/**
 * Created by beantoan on 5/14/16.
 */
interface CategoryServiceInterface {
    @GET("categories.json")
    fun index(): Single<List<Category>>

    @GET("categories/recommendation.json")
    fun recommendation(): Single<List<Category>>
}
