package it.unical.mat.lifetune.model;

import android.util.Log;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelGroup;

import java.util.ArrayList;
import java.util.List;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.controller.BaseMusicController;
import it.unical.mat.lifetune.entity.Category;
import it.unical.mat.lifetune.entity.Playlist;
import it.unical.mat.lifetune.view.CategoryCarouselViewModel_;

public class CategoryModelGroup extends EpoxyModelGroup {

    private static String TAG = CategoryModelGroup.class.getSimpleName();


    public CategoryModelGroup(Category _category, BaseMusicController.AdapterCallbacks callbacks) {
        super(R.layout.model_group_category, buildModels(_category, callbacks));
        id(_category.getId());
    }

    private static List<EpoxyModel<?>> buildModels(Category _category, BaseMusicController.AdapterCallbacks callbacks) {
        Log.d(TAG, "buildModels: category.id=" + _category.getId());

        List<Playlist> playlists = _category.getPlaylists();
        ArrayList<EpoxyModel<?>> models = new ArrayList<>();

        // Header for CategoryCarousel
        CategoryHeaderModel_ playlistHeaderModel = new CategoryHeaderModel_(_category);
        playlistHeaderModel.id(_category.getId());
        models.add(playlistHeaderModel);

        // Add a list of Playlist into CategoryCarousel
        List<PlaylistModel_> playlistModels = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistModels.add(
                    new PlaylistModel_(playlist)
                            .id(playlist.getId(), _category.getId())
                            .clickListener((model, parentView, clickedView, position) -> {
                                Log.d(TAG, "PlaylistModel_.clickListener playlist=" + model.playlist.shortLog() + ", position=" + position);
                                callbacks.onPlaylistClicked(model.playlist);
                            })
            );
        }

        CategoryCarouselViewModel_ categoryCarouselViewModel = new CategoryCarouselViewModel_();
        categoryCarouselViewModel.id("playlists-" + _category.getId());
        categoryCarouselViewModel.models(playlistModels);

        models.add(categoryCarouselViewModel);

        return models;
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return totalSpanCount;
    }
}
