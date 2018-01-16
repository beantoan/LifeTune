package it.unical.mat.lifetune.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by beantoan on 1/15/18.
 */
data class CommonApiResponse(@SerializedName("result") val result: String) {
    fun isOk() = result == OK

    fun isError() = result == ERROR

    companion object {
        val OK = "OK"
        val ERROR = "ERROR"
    }
}