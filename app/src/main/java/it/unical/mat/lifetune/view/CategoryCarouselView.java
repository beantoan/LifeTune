package it.unical.mat.lifetune.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.ModelView.Size;

@ModelView(autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class CategoryCarouselView extends Carousel {
    private static final int SPAN_COUNT = 1;

    public CategoryCarouselView(Context context) {
        super(context);
    }

    @Override
    protected LayoutManager createLayoutManager() {
        return new GridLayoutManager(getContext(), SPAN_COUNT, LinearLayoutManager.HORIZONTAL, false);
    }
}
