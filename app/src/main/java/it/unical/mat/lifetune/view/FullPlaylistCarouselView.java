package it.unical.mat.lifetune.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.ModelView.Size;

@ModelView(autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class FullPlaylistCarouselView extends Carousel {

    public FullPlaylistCarouselView(Context context) {
        super(context);
    }

    @Override
    protected LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }
}
