package it.unical.mat.lifetune.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.lapism.searchview.R
import com.lapism.searchview.SearchView

/**
 * Created by beantoan on 1/14/18.
 */
class CustomSearchView : SearchView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) :
            super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attributeSet, defStyleAttr, defStyleRes)

    var onClearSearchInputListener: OnClearSearchInputListener? = null

    override fun onClick(view: View?) {
        super.onClick(view)

        val clearButton = findViewById<ImageView>(R.id.search_imageView_clear)

        if (view == clearButton) {
            onClearSearchInputListener?.onClear()
        }
    }


    interface OnClearSearchInputListener {
        fun onClear()
    }
}