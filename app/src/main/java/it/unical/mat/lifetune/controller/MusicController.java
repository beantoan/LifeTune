package it.unical.mat.lifetune.controller;

import com.airbnb.epoxy.TypedEpoxyController;

import java.util.List;

import it.unical.mat.lifetune.entity.Playlist;
import it.unical.mat.lifetune.model.PlaylistModelGroup;

public class MusicController extends TypedEpoxyController<List<Playlist>> {

    public interface AdapterCallbacks {
        void onPlaylistClicked(Playlist playlist, int position);
    }

    private final AdapterCallbacks callbacks;

    public MusicController(AdapterCallbacks callbacks) {
        this.callbacks = callbacks;
        setDebugLoggingEnabled(true);
    }

    @Override
    protected void buildModels(List<Playlist> playlists) {
        for (int i = 0; i < playlists.size(); i++) {
            Playlist playlist = playlists.get(i);
            add(new PlaylistModelGroup(playlist, callbacks));
        }
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }
}
