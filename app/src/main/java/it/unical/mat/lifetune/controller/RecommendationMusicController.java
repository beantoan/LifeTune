package it.unical.mat.lifetune.controller;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import it.unical.mat.lifetune.entity.Category;
import it.unical.mat.lifetune.model.CategoryModelGroup;

public class RecommendationMusicController extends BaseMusicController<List<Category>> {

    private static final String TAG = RecommendationMusicController.class.getSimpleName();

    public RecommendationMusicController(AdapterCallbacks _callbacks) {
        super(_callbacks);
    }

    @Override
    protected void buildModels(List<Category> categories) {
        for (Category category : categories) {
            add(new CategoryModelGroup(category, callbacks));
        }
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        Crashlytics.log(Log.ERROR, TAG, "onExceptionSwallowed:" + exception);
        Crashlytics.logException(exception);

        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }
}
