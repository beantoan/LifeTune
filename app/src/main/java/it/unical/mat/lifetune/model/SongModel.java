package it.unical.mat.lifetune.model;

import android.view.View;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.entity.Song;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.model_song)
public abstract class SongModel extends DataBindingEpoxyModel {
    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    Song song;

    public SongModel(Song _song) {
        song = _song;
    }
}
