package it.unical.mat.lifetune.controller;

import android.util.Log;

import com.airbnb.epoxy.TypedEpoxyController;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import it.unical.mat.lifetune.entity.Place;
import it.unical.mat.lifetune.model.PlaceModelGroup;

public class NearbyPlacesController extends TypedEpoxyController<List<Place>> {

    private static final String TAG = NearbyPlacesController.class.getSimpleName();

    final AdapterCallbacks callbacks;

    public interface AdapterCallbacks {
        void onPlaceClicked(Place place);
    }

    public NearbyPlacesController(AdapterCallbacks _callbacks) {
        callbacks = _callbacks;
    }

    @Override
    protected void buildModels(List<Place> places) {
        for (Place place : places) {
            add(new PlaceModelGroup(place, callbacks));
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
