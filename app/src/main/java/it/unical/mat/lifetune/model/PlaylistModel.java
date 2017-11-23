package it.unical.mat.lifetune.model;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.squareup.picasso.Picasso;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.entity.Playlist;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.model_playlist)
public abstract class PlaylistModel extends DataBindingEpoxyModel {
    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    Playlist playlist;

    PlaylistModel(Playlist _playlist) {
        playlist = _playlist;
    }

    @Override
    protected View buildView(ViewGroup parent) {
        View view = super.buildView(parent);

        ImageView image = view.findViewById(R.id.playlist_thumb);

        int imageSize = 120;

        Picasso.with(view.getContext())
                .load(playlist.getThumbUrl())
                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.drawable.ic_image_black_24dp)
                .resize(imageSize, imageSize)
                .centerCrop()
                .into(image);

        return view;
    }
}
