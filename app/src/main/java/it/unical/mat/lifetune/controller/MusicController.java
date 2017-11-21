package it.unical.mat.lifetune.controller;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.TypedEpoxyController;

import java.util.List;

import it.unical.mat.lifetune.entity.Category;
import it.unical.mat.lifetune.model.CategoryModelGroup;
import it.unical.mat.lifetune.view.CategoryCarouselHeaderViewModel_;

public class MusicController extends TypedEpoxyController<List<Category>> {
    public interface AdapterCallbacks {
        void onSearchMusicClicked();
        void onPlaylistClicked(Category category, int position);
    }

    @AutoModel
    CategoryCarouselHeaderViewModel_ categoryCarouselHeaderViewModel;

    private final AdapterCallbacks callbacks;

    public MusicController(AdapterCallbacks _callbacks) {
        this.callbacks = _callbacks;
        setDebugLoggingEnabled(true);
    }

    @Override
    protected void buildModels(List<Category> categories) {
//        categoryCarouselHeaderViewModel.addTo(this);
        categoryCarouselHeaderViewModel.searchMusicClickListener(callbacks);

        for (Category category : categories) {
            add(new CategoryModelGroup(category, callbacks));
        }
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }
}
