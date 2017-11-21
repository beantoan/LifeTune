package it.unical.mat.lifetune.entity

import android.databinding.BaseObservable
import android.databinding.Bindable

/**
 * Created by beantoan on 11/20/17.
 */
class Playlist(
        var id: Int,
        private var _title: String?,
        private var _description: String?,
        private var _songs: List<Song>) : BaseObservable() {

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

    // TODO need to add notifyPropertyChanged later
    var songs: List<Song>
        @Bindable get() = _songs
        set(value) {
            _songs = value
        }

    init {
        title = _title
        description = _description
        songs = _songs
    }
}