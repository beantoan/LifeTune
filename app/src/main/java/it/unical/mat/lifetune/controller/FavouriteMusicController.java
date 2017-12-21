package it.unical.mat.lifetune.controller;

import android.util.Log;

import java.util.List;

import it.unical.mat.lifetune.entity.Playlist;
import it.unical.mat.lifetune.model.FullPlaylistModelGroup;

public class FavouriteMusicController extends BaseMusicController<List<Playlist>> {
    private static final String TAG = FavouriteMusicController.class.getCanonicalName();

    public FavouriteMusicController(AdapterCallbacks _callbacks) {
        super(_callbacks);
    }

    @Override
    protected void buildModels(List<Playlist> playlists) {
        for (Playlist playlist : playlists) {
            add(new FullPlaylistModelGroup(playlist, callbacks));
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
