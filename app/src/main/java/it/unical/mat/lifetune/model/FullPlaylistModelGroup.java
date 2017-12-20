package it.unical.mat.lifetune.model;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelGroup;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.controller.FavouriteMusicController;
import it.unical.mat.lifetune.entity.Playlist;
import it.unical.mat.lifetune.entity.Song;
import it.unical.mat.lifetune.view.FullPlaylistCarouselViewModel_;

public class FullPlaylistModelGroup extends EpoxyModelGroup {
    public final Playlist data;

    public FullPlaylistModelGroup(Playlist _playlist, FavouriteMusicController.AdapterCallbacks callbacks) {
        super(R.layout.model_group_full_playlist, buildModels(_playlist, callbacks));
        this.data = _playlist;
        id(data.getId());
    }

    private static List<EpoxyModel<?>> buildModels(Playlist playlist,
                                                   FavouriteMusicController.AdapterCallbacks callbacks) {
        List<Song> songs = playlist.getSongs();
        ArrayList<EpoxyModel<?>> models = new ArrayList<>();

        // Header for CategoryCarousel
        FullPlaylistHeaderModel_ fullPlaylistHeaderModel = new FullPlaylistHeaderModel_(playlist);
        fullPlaylistHeaderModel.id(playlist.getId());
        fullPlaylistHeaderModel.clickListener((model, parentView, clickedView, position) ->
                callbacks.onPlaylistClicked(playlist, position));
        models.add(fullPlaylistHeaderModel);

        // Add a list of Playlist into PlaylistCarousel
        List<SongModel_> songModels = new ArrayList<>();
        for (Song song : songs) {
            songModels.add(new SongModel_(song)
                    .id(playlist.getId(), song.getId())
                    .clickListener((model, parentView, clickedView, position) ->
                            callbacks.onSongClicked(song, position)
                    ));
        }

        FullPlaylistCarouselViewModel_ fullPlaylistCarouselViewModel = new FullPlaylistCarouselViewModel_();
        fullPlaylistCarouselViewModel.id("songs-" + String.valueOf(playlist.getId()));
        fullPlaylistCarouselViewModel.models(songModels);

        models.add(fullPlaylistCarouselViewModel);

        return models;
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return totalSpanCount;
    }
}
