package it.unical.mat.lifetune.model;

import android.util.Log;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelGroup;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.controller.BaseMusicController;
import it.unical.mat.lifetune.entity.Playlist;
import it.unical.mat.lifetune.entity.Song;
import it.unical.mat.lifetune.view.FullPlaylistCarouselViewModel_;

public class FullPlaylistModelGroup extends EpoxyModelGroup {

    private static String TAG = FullPlaylistModelGroup.class.getSimpleName();

    public static Playlist playlist;

    public FullPlaylistModelGroup(Playlist _playlist,
                                  BaseMusicController.AdapterCallbacks callbacks) {
        super(R.layout.model_group_full_playlist, buildModels(_playlist, callbacks));
        playlist = _playlist;
        id(playlist.getId());
    }

    private static List<EpoxyModel<?>> buildModels(Playlist _playlist, BaseMusicController.AdapterCallbacks callbacks) {
        List<Song> songs = _playlist.getSongs();
        ArrayList<EpoxyModel<?>> models = new ArrayList<>();

        // Header for FullPlaylistCarousel
        FullPlaylistHeaderModel_ fullPlaylistHeaderModel = new FullPlaylistHeaderModel_(_playlist);
        fullPlaylistHeaderModel.id(_playlist.getId());
        fullPlaylistHeaderModel.clickListener((model, parentView, clickedView, position) -> {
            Log.d(TAG, "FullPlaylistHeaderModel_.clickListener position=" + position);
            callbacks.onPlaylistClicked(model.playlist);
        });
        models.add(fullPlaylistHeaderModel);

        // Add a list of Playlist into PlaylistCarousel
        List<SongModel_> songModels = new ArrayList<>();
        for (Song song : songs) {
            songModels.add(new SongModel_(song)
                    .id(_playlist.getId(), song.getId())
                    .clickListener((model, parentView, clickedView, position) -> {
                        Log.d(TAG, "SongModel_.clickListener position=" + position);
                        callbacks.onSongClicked(model.song);
                    })
            );
        }

        FullPlaylistCarouselViewModel_ fullPlaylistCarouselViewModel = new FullPlaylistCarouselViewModel_();
        fullPlaylistCarouselViewModel.id("songs-" + _playlist.getId());
        fullPlaylistCarouselViewModel.models(songModels);

        models.add(fullPlaylistCarouselViewModel);

        return models;
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return totalSpanCount;
    }
}
