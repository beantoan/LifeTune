package it.unical.mat.lifetune.model;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import it.unical.mat.lifetune.R;
import it.unical.mat.lifetune.entity.Category;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.model_category_header)
public abstract class CategoryHeaderModel extends DataBindingEpoxyModel {

    @EpoxyAttribute(DoNotHash)
    Category category;

    public CategoryHeaderModel(Category _category) {
        category = _category;
    }
}
