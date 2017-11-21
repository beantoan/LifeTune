package it.unical.mat.lifetune.view;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.ModelView.Size;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.unical.mat.lifetune.R;

@ModelView(autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class CategoryCarouselHeaderView extends LinearLayout {

  @BindView(R.id.search_music)
  EditText mEditTextSearchMusic;

  public CategoryCarouselHeaderView(Context context) {
    super(context);
    init();
  }

  private void init() {
    setOrientation(VERTICAL);
    inflate(getContext(), R.layout.view_category_carousel_header, this);
    ButterKnife.bind(this);
  }
}
