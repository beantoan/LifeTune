package it.unical.mat.lifetune.view;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.ModelView.Size;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.controller.MusicController;

import static com.airbnb.epoxy.ModelProp.Option.DoNotHash;

@ModelView(autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class CategoryCarouselHeaderView extends LinearLayout {

  @BindView(R.id.search_music)
  EditText mEditTextSearchMusic;

  private MusicController.AdapterCallbacks adapterCallbacks;

  public CategoryCarouselHeaderView(Context context) {
    super(context);
    init();
  }

  private void init() {
    setOrientation(VERTICAL);
    inflate(getContext(), R.layout.view_category_carousel_header, this);
    ButterKnife.bind(this);
  }

  @ModelProp(DoNotHash)
  public void setSearchMusicClickListener(MusicController.AdapterCallbacks _adapterCallbacks) {
      this.adapterCallbacks = _adapterCallbacks;
  }

  @OnClick(R.id.search_music)
  public void onSearchMusicClicked() {
      this.adapterCallbacks.onSearchMusicClicked();
  }
}
