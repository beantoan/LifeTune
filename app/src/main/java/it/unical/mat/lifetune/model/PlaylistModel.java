package it.unical.mat.lifetune.model;

import android.view.View;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.entity.Playlist;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.model_playlist)
public abstract class PlaylistModel extends DataBindingEpoxyModel {
    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    Playlist playlist;

    public PlaylistModel(Playlist _playlist) {
        playlist = _playlist;
    }
}
