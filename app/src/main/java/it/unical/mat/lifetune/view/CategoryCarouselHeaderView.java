package it.unical.mat.lifetune.view;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.ModelView.Size;
import com.arlib.floatingsearchview.FloatingSearchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.controller.MusicController;

import static com.airbnb.epoxy.ModelProp.Option.DoNotHash;

@ModelView(autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class CategoryCarouselHeaderView extends LinearLayout {

  private static final String TAG = CategoryCarouselHeaderView.class.getCanonicalName();

  @BindView(R.id.floating_search_view_search_music)
  FloatingSearchView mFloatingSearchView;

  private MusicController.AdapterCallbacks adapterCallbacks;

  public CategoryCarouselHeaderView(Context context) {
    super(context);
    init();
  }

  private void init() {
    setOrientation(VERTICAL);
    inflate(getContext(), R.layout.view_category_carousel_header, this);
    ButterKnife.bind(this);

    mFloatingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
      @Override
      public void onFocus() {
        adapterCallbacks.onSearchMusicFocused();
        mFloatingSearchView.clearFocus();

        Log.d(TAG, "onFocus()");
      }

      @Override
      public void onFocusCleared() {

        Log.d(TAG, "onFocusCleared()");
      }
    });
  }

  @ModelProp(DoNotHash)
  public void setSearchMusicClickListener(MusicController.AdapterCallbacks _adapterCallbacks) {
      this.adapterCallbacks = _adapterCallbacks;
  }
}
