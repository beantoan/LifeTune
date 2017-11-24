package it.unical.mat.lifetune.controller;

import android.util.Log;

import com.airbnb.epoxy.TypedEpoxyController;

import java.util.List;

import it.unical.mat.lifetune.entity.Category;
import it.unical.mat.lifetune.model.CategoryModelGroup;

public class MusicController extends TypedEpoxyController<List<Category>> {
    private static final String TAG = MusicController.class.getCanonicalName();

    public interface AdapterCallbacks {
        void onPlaylistClicked(Category category, int position);
    }
    private final AdapterCallbacks callbacks;

    public MusicController(AdapterCallbacks _callbacks) {
        this.callbacks = _callbacks;
        setDebugLoggingEnabled(true);
    }

    @Override
    protected void buildModels(List<Category> categories) {
        for (Category category : categories) {
            add(new CategoryModelGroup(category, callbacks));
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
