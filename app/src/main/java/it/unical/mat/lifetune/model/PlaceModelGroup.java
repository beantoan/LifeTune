package it.unical.mat.lifetune.model;

import android.util.Log;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelGroup;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.controller.NearbyPlacesController;
import it.unical.mat.lifetune.entity.Place;

public class PlaceModelGroup extends EpoxyModelGroup {

    private static String TAG = PlaceModelGroup.class.getSimpleName();

    public static Place place;

    public PlaceModelGroup(Place _place,
                           NearbyPlacesController.AdapterCallbacks callbacks) {
        super(R.layout.model_group_full_playlist, buildModels(_place, callbacks));
        PlaceModelGroup.place = _place;
        id(PlaceModelGroup.place.getId());
    }

    private static List<EpoxyModel<?>> buildModels(Place _place, NearbyPlacesController.AdapterCallbacks callbacks) {
//        List<Song> songs = _playlist.getSongs();
        ArrayList<EpoxyModel<?>> models = new ArrayList<>();

        // Header for FullPlaylistCarousel
        PlaceHeaderModel_ placeHeaderModel = new PlaceHeaderModel_(_place);
        placeHeaderModel.id(_place.getId());
        placeHeaderModel.clickListener((model, parentView, clickedView, position) -> {
            Log.d(TAG, "PlaceHeaderModel_.clickListener position=" + position);
            callbacks.onPlaceClicked(model.place);
        });
        models.add(placeHeaderModel);
//
//        // Add a list of Playlist into PlaylistCarousel
//        List<SongModel_> songModels = new ArrayList<>();
//        for (Song song : songs) {
//            songModels.add(new SongModel_(song)
//                    .id(_playlist.getId(), song.getId())
//                    .clickListener((model, parentView, clickedView, position) -> {
//                        Log.d(TAG, "SongModel_.clickListener position=" + position);
//                        callbacks.onSongClicked(model.song);
//                    })
//            );
//        }
//
//        FullPlaylistCarouselViewModel_ fullPlaylistCarouselViewModel = new FullPlaylistCarouselViewModel_();
//        fullPlaylistCarouselViewModel.id("songs-" + _playlist.getId());
//        fullPlaylistCarouselViewModel.models(songModels);
//
//        models.add(fullPlaylistCarouselViewModel);

        return models;
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return totalSpanCount;
    }
}
