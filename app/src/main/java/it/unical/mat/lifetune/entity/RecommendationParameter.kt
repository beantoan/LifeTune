package it.unical.mat.lifetune.entity

/**
 * Created by beantoan on 1/6/18.
 */
data class RecommendationParameter(
        var countryCode: String? = null,
        var temp: Float? = null,
        var weatherConditions: Int? = null,
        var activityType: Int? = null
)