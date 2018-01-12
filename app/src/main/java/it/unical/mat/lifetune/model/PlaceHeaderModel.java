package it.unical.mat.lifetune.model;

import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.entity.Place;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.model_place_header)
abstract class PlaceHeaderModel extends DataBindingEpoxyModel {
    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    Place place;

    PlaceHeaderModel(Place _place) {
        this.place = _place;
    }

    @Override
    protected View buildView(ViewGroup parent) {
        View view = super.buildView(parent);
//
//        ImageView image = view.findViewById(R.id.full_playlist_img);
//
//        Picasso.with(view.getContext())
//                .load(place.getImg())
//                .placeholder(R.drawable.no_image)
//                .error(R.drawable.no_image)
//                .into(image);

        return view;
    }
}
