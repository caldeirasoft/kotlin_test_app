package com.caldeirasoft.basicapp.presentation.utils.extensions

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : ViewDataBinding> Fragment.dataBinding(): ReadOnlyProperty<Fragment, T> {
    return object : ReadOnlyProperty<Fragment, T> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            (requireView().getTag(property.name.hashCode()) as? T)?.let { return it }
            return bind<T>(requireView()).also {
                it.lifecycleOwner = thisRef.viewLifecycleOwner
                it.root.setTag(property.name.hashCode(), it)
            }
        }

        private fun <T : ViewDataBinding> bind(view: View): T = DataBindingUtil.bind(view)!!
    }
}

fun <T : ViewDataBinding> FragmentActivity.dataBinding(): Lazy<T> = object : Lazy<T> {
    private var binding: T? = null
    override fun isInitialized(): Boolean = binding != null
    override val value: T
        get() = binding ?: bind<T>(getContentView()).also {
            it.lifecycleOwner = this@dataBinding
            binding = it
        }

    private fun FragmentActivity.getContentView(): View {
        return checkNotNull(findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) {
            "Call setContentView or Use Activity's secondary constructor passing layout res id."
        }
    }

    private fun <T : ViewDataBinding> bind(view: View): T = DataBindingUtil.bind(view)!!
}