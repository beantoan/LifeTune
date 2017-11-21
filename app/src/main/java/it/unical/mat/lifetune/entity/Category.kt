package it.unical.mat.lifetune.entity

import android.databinding.BaseObservable
import android.databinding.Bindable

/**
 * Created by beantoan on 11/20/17.
 */
class Category(
        var id: Int,
        private var _title: String?,
        private var _description: String?,
        private var _playlists: List<Playlist>) : BaseObservable() {

    var title: String?
        @Bindable get() = _title
        set(value) {
            if (!_title.equals(value)) {
                _title = value

                notifyPropertyChanged(it.unical.mat.lifetune.BR.title)
            }
        }

    var description: String?
        @Bindable get() = _description
        set(value) {
            if (!_description.equals(value)) {
                _description = value

                notifyPropertyChanged(it.unical.mat.lifetune.BR.description)
            }
        }

    var playlists: List<Playlist>
        @Bindable get() = _playlists
        set(value) {
            _playlists = value
        }

    init {
        title = _title
        description = _description
        playlists = _playlists
    }
}