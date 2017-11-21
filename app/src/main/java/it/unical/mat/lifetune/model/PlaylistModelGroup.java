package it.unical.mat.lifetune.model;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelGroup;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.controller.MusicController;
import it.unical.mat.lifetune.entity.Playlist;
import it.unical.mat.lifetune.entity.Song;
import it.unical.mat.lifetune.view.SongsCarouselViewModel_;

public class PlaylistModelGroup extends EpoxyModelGroup {
    public final Playlist data;

    public PlaylistModelGroup(Playlist _playlist, MusicController.AdapterCallbacks callbacks) {
        super(R.layout.model_group_playlist, buildModels(_playlist, callbacks));
        this.data = _playlist;
        id(data.getId());
    }

    private static List<EpoxyModel<?>> buildModels(Playlist playlist,
                                                   MusicController.AdapterCallbacks callbacks) {
        List<Song> songs = playlist.getSongs();
        ArrayList<EpoxyModel<?>> models = new ArrayList<>();

        PlaylistHeaderModel_ playlistHeaderModel = new PlaylistHeaderModel_(playlist);
        playlistHeaderModel.id(playlist.getId());
        models.add(playlistHeaderModel);

        List<SongModel_> songModels = new ArrayList<>();
        for (Song song : songs) {
            songModels.add(new SongModel_(song)
                    .id(song.getId(), playlist.getId())
                    .clickListener((model, parentView, clickedView, position) -> {
                        callbacks.onPlaylistClicked(playlist, position);
                    }));
        }

        SongsCarouselViewModel_ songsCarouselModel = new SongsCarouselViewModel_();
        songsCarouselModel.id("playlists");
        songsCarouselModel.models(songModels);

        models.add(songsCarouselModel);

        return models;
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return totalSpanCount;
    }
}
