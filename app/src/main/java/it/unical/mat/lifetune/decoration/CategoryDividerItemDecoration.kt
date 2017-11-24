package it.unical.mat.lifetune.decoration

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View


/**
 * Created by beantoan on 11/21/17.
 */
class CategoryDividerItemDecoration(context: Context, orientation: Int, drawable: Drawable)
    : DividerItemDecoration(context, orientation) {

    init {
        super.setDrawable(drawable)
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        val params = view!!.layoutParams as RecyclerView.LayoutParams

        // we want to retrieve the position in the list
        val position = params.viewAdapterPosition

        if (position < state!!.itemCount - 1) {
            super.getItemOffsets(outRect, view, parent, state)
        } else {
            outRect!!.setEmpty()
        }

    }
}
