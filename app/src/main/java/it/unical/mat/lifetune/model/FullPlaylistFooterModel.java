package it.unical.mat.lifetune.model;

import android.view.View;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.entity.Playlist;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.model_full_playlist_footer)
abstract class FullPlaylistFooterModel extends DataBindingEpoxyModel {
    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    Playlist playlist;

    FullPlaylistFooterModel(Playlist _playlist) {
        playlist = _playlist;
    }

}
