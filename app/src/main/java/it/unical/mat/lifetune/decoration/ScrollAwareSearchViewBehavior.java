package it.unical.mat.lifetune.decoration;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.arlib.floatingsearchview.FloatingSearchView;

/**
 * Created by beantoan on 11/22/17.
 */

public class ScrollAwareSearchViewBehavior extends CoordinatorLayout.Behavior<FloatingSearchView>  {
    public ScrollAwareSearchViewBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingSearchView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return true;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingSearchView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        Log.d("TAG", String.valueOf(dyConsumed));

        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.setVisibility(View.INVISIBLE);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.setVisibility(View.VISIBLE);
        }

        float d = target.getContext().getResources().getDisplayMetrics().scaledDensity;
        int top = 0, ty = -30;

        if (child.getVisibility() == View.VISIBLE && dyConsumed == 0) {
            top = (int) (d * 65);
            ty = 30;
        }

        params.setMargins(0, top, 0, 16);

        target.setLayoutParams(params);
        target.requestLayout();
    }
}
