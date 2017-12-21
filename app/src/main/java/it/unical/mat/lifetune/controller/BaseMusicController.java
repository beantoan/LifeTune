package it.unical.mat.lifetune.controller;


import com.airbnb.epoxy.TypedEpoxyController;

import it.unical.mat.lifetune.entity.Playlist;
import it.unical.mat.lifetune.entity.Song;

public abstract class BaseMusicController<T> extends TypedEpoxyController<T> {

    private static final String TAG = BaseMusicController.class.getCanonicalName();

    final AdapterCallbacks callbacks;

    public interface AdapterCallbacks {
        void onSongClicked(Song song);

        void onPlaylistClicked(Playlist playlist);
    }


    public BaseMusicController(AdapterCallbacks _callbacks) {
        callbacks = _callbacks;

        setDebugLoggingEnabled(true);
    }
}
