package com.caldeirasoft.basicapp.ui.common

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * FragmentStatePagerAdapter that caches each pages.
 *
 * FragmentStatePagerAdapter is also originally caches pages,
 * but its keys are not public nor documented, so depending
 * on how it create cache key is dangerous.
 *
 * This adapter caches pages by itself and provide getter method to the cache.
 */
abstract class CacheFragmentStatePagerAdapter(private val mFm: FragmentManager): FragmentStatePagerAdapter(mFm) {
    private val mPages: SparseArray<Fragment> = SparseArray()

    override fun saveState(): Parcelable? {
        val p = super.saveState()
        val bundle = Bundle()
        bundle.putParcelable(STATE_SUPER_STATE, p)

        bundle.putInt(STATE_PAGES, mPages.size())
        if (0 < mPages.size()) {
            for (i in 0 until mPages.size()) {
                val position = mPages.keyAt(i)
                bundle.putInt(createCacheIndex(i), position)
                val f = mPages.get(position)
                mFm.putFragment(bundle, createCacheKey(position), f)
            }
        }
        return bundle
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        val bundle = state as Bundle?
        val pages = bundle!!.getInt(STATE_PAGES)
        if (0 < pages) {
            for (i in 0 until pages) {
                val position = bundle.getInt(createCacheIndex(i))
                val f = mFm.getFragment(bundle, createCacheKey(position))
                mPages.put(position, f)
            }
        }

        val p = bundle.getParcelable<Parcelable>(STATE_SUPER_STATE)
        super.restoreState(p, loader)
    }

    /**
     * Get a new Fragment instance.
     *
     * Each fragments are automatically cached in this method,
     * so you don't have to do it by yourself.
     * If you want to implement instantiation of Fragments,
     * you should override [.createItem] instead.
     *
     *
     * {@inheritDoc}
     *
     * @param position Position of the item in the adapter.
     * @return Fragment instance.
     */
    override fun getItem(position: Int): Fragment {
        val f = createItem(position)
        // We should cache fragments manually to access to them later
        mPages.put(position, f)
        return f
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (0 <= mPages.indexOfKey(position)) {
            mPages.remove(position)
        }
        super.destroyItem(container, position, `object`)
    }

    /**
     * Get the item at the specified position in the adapter.
     *
     * @param position Position of the item in the adapter.
     * @return Fragment instance.
     */
    fun getItemAt(position: Int): Fragment {
        return mPages.get(position)
    }

    /**
     * Create a new Fragment instance.
     * This is called inside [.getItem].
     *
     * @param position Position of the item in the adapter.
     * @return Fragment instance.
     */
    protected abstract fun createItem(position: Int): Fragment

    /**
     * Create an index string for caching Fragment pages.
     *
     * @param index Index of the item in the adapter.
     * @return Key string for caching Fragment pages.
     */
    protected fun createCacheIndex(index: Int): String {
        return STATE_PAGE_INDEX_PREFIX + index
    }

    /**
     * Create a key string for caching Fragment pages.
     *
     * @param position Position of the item in the adapter.
     * @return Key string for caching Fragment pages.
     */
    protected fun createCacheKey(position: Int): String {
        return STATE_PAGE_KEY_PREFIX + position
    }

    companion object {

        private const val STATE_SUPER_STATE = "superState"
        private const val STATE_PAGES = "pages"
        private const val STATE_PAGE_INDEX_PREFIX = "pageIndex:"
        private const val STATE_PAGE_KEY_PREFIX = "page:"
    }
}