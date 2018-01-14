package it.unical.mat.lifetune.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log

/**
 * Created by beantoan on 8/11/16.
 */
abstract class ManipulatedAdapter<E, VH : RecyclerView.ViewHolder>(private var mItems: ArrayList<E>) :
        RecyclerView.Adapter<VH>() {

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun getItem(position: Int): E {
        return mItems[position]
    }

    fun insert(item: E, position: Int) {
        mItems.add(position, item)
        notifyItemInserted(position)
    }

    fun appendTop(item: E) {
        insert(item, 0)
    }

    fun appendTopAll(items: Collection<E>) {
        mItems.addAll(0, items)
        notifyItemRangeInserted(0, items.size)
    }

    fun appendBottom(item: E) {
        insert(item, mItems.size)
    }

    fun appendBottomAll(items: Collection<E>) {
        val startIndex = mItems.size
        mItems.addAll(startIndex, items)
        notifyItemRangeInserted(startIndex, items.size)
    }

    fun remove(position: Int) {
        try {
            mItems.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: Exception) {
            Log.e(TAG, "remove", e)
        }

    }

    fun remove(item: E) {
        val position = mItems.indexOf(item)
        remove(position)
    }

    fun clear() {
        val size = mItems.size
        mItems.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addAll(items: Collection<E>) {
        clear()
        appendTopAll(items)
    }

    companion object {

        private val TAG = ManipulatedAdapter::class.java.simpleName
    }
}
