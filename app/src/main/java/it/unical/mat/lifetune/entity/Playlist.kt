package it.unical.mat.lifetune.entity

import android.databinding.BaseObservable
import android.databinding.Bindable

/**
 * Created by beantoan on 11/20/17.
 */
class Playlist(
        var id: Int,
        private var _title: String?,
        private var _url: String?,
        private var _thumbUrl: String?) : BaseObservable() {

    var title: String?
        @Bindable get() = _title
        set(value) {
            if (!_title.equals(value)) {
                _title = value
            }
        }

    var url: String?
        @Bindable get() = _url
        set(value) {
            if (!_url.equals(value)) {
                _url = value
            }
        }

    var thumbUrl: String?
        @Bindable get() = _thumbUrl
        set(value) {
            if (!_thumbUrl.equals(value)) {
                _thumbUrl = value
            }
        }

    init {
        title = _title
        url = _url
        thumbUrl = _thumbUrl
    }
}