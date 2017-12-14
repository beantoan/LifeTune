package it.unical.mat.lifetune.controller;

import android.util.Log;

import com.airbnb.epoxy.TypedEpoxyController;

import java.util.List;

import it.unical.mat.lifetune.entity.Playlist;
import it.unical.mat.lifetune.model.FullPlaylistModel_;

public class FavouriteMusicController extends TypedEpoxyController<List<Playlist>> {
    private static final String TAG = FavouriteMusicController.class.getCanonicalName();

    public interface AdapterCallbacks {
        void onPlaylistClicked(Playlist playlist, int position);
    }

    private final AdapterCallbacks callbacks;

    public FavouriteMusicController(AdapterCallbacks _callbacks) {
        this.callbacks = _callbacks;
        setDebugLoggingEnabled(true);
    }

    @Override
    protected void buildModels(List<Playlist> playlists) {
        for (Playlist playlist : playlists) {
            add(new FullPlaylistModel_(playlist)
                    .id(playlist.getId())
                    .clickListener((model, parentView, clickedView, position) -> {
                        callbacks.onPlaylistClicked(playlist, position);
                    }));
        }
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        Log.e(TAG, "onExceptionSwallowed", exception);

        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }
}
