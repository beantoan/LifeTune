package it.unical.mat.lifetune.entity

import android.content.Intent

/**
 * Created by beantoan on 1/6/18.
 */
data class ActivityResultEvent(val requestCode: Int, val resultCode: Int, val data: Intent?)