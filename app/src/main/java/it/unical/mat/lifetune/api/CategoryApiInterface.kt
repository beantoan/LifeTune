package it.unical.mat.lifetune.api

import io.reactivex.Single
import it.unical.mat.lifetune.entity.Category
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by beantoan on 5/14/16.
 */
interface CategoryApiInterface {
    @GET("categories.json")
    fun index(): Single<List<Category>>

    @GET("categories/recommendation.json")
    fun recommendation(@Query("user_id") user_id: String,
                       @Query("country_code") countryCode: String?,
                       @Query("temp") temp: Float?,
                       @Query("weather_conditions") weatherConditions: Int?,
                       @Query("activity_type") activityType: Int?): Single<List<Category>>
}
