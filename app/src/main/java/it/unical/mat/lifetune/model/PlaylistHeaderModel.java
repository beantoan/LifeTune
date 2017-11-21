package it.unical.mat.lifetune.model;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.entity.Playlist;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.model_playlist_header)
public abstract class PlaylistHeaderModel extends DataBindingEpoxyModel {

    @EpoxyAttribute(DoNotHash)
    Playlist playlist;

    public PlaylistHeaderModel(Playlist _playlist) {
        playlist = _playlist;
    }
}
