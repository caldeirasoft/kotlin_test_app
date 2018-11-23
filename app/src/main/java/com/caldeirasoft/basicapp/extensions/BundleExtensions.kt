package com.caldeirasoft.basicapp.extensions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.os.PersistableBundle
import android.util.Log
import android.util.Size
import android.util.SizeF
import androidx.fragment.app.Fragment

/**
 * Usage :
 * class OtherActivity : Activity() {
 *      val extraString by lazyExtra<String>(EXTRA_STRING)
 *      val extraFloat by lazyExtra<Float>(EXTRA_FLOAT)
 *      val extraBoolean by lazyExtra<Boolean>(EXTRA_BOOLEAN)
 *      val extraOptional by lazyExtra<String?>(EXTRA_OPTIONAL) // Optional by nullable type
 *      (...)
 *  }
 */

/**
 * Usage :
 * startActivity<MyActivity>(
 *      EXTRA_BOOLEAN to true,
 *      EXTRA_STRING to "Oh, hello world!",
 *      EXTRA_FLOAT to 1.12f)
 */

/**
 * Using fragements :
 * class CustomFragment : Fragment() {
 *      val argString by lazyArg<String>(ARG_STRING)
 *      val argOptional by lazyArg<String?>(ARG_OPTIONAL)
 *      (...)
 */

/**
 * // Attach arguments to Fragment
 * CustomFragment().withArgs(
 *      ARG_STRING to "string argument value",
 *      ARG_LONG to 1000L
 */

/** Lazy initialize with intent extra [key] */
inline fun <reified T : Any?> Activity.lazyExtra(key: String)
        = lazy { intent.extras?.get(key) as T }

/** Lazy initialize with intent extra [key] */
inline fun <reified T : Any?> Fragment.lazyArg(key: String)
        = lazy { arguments?.get(key) as T }

/** Lazy initialize with intent extra [key] */
inline fun <reified T : Any?> android.app.Fragment.lazyArg(key: String)
        = lazy { arguments?.get(key) as T }

/** Start activity from fragment with given extras */
inline fun <reified T : Any> Fragment.startActivity(vararg extras: Pair<String, Any?> = emptyArray()) {
    activity?.startActivity<T>(*extras)
}

/** Start activity from fragment with given extras */
inline fun <reified T : Any> android.app.Fragment.startActivity(vararg extras: Pair<String, Any?> = emptyArray()) {
    activity.startActivity<T>(*extras)
}

/** Start activity with given extras */
inline fun <reified T : Any> Activity.startActivity(vararg extras: Pair<String, Any?> = emptyArray()) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(Bundle().putExtras(extras))
    startActivity(intent)
}

/** Put extras inside intent */
fun Intent.withExtras(vararg extras: Pair<String, Any?>): Intent {
    val bundle = Bundle()
    bundle.putExtras(extras)
    putExtras(bundle)
    return this
}

/** Put args inside fragment. Reuses existing fragments arguments if set. */
fun <T : android.app.Fragment> T.withArgs(vararg args: Pair<String, Any?> = emptyArray()): T {
    val bundle = arguments ?: Bundle()
    arguments = bundle
    bundle.putExtras(args)
    return this
}

/** Put args inside fragment. Reuses existing fragments arguments if set. */
fun <T: Fragment> T.withArgs(vararg args: Pair<String, Any?> = emptyArray()): T {
    val bundle = arguments ?: Bundle()
    arguments = bundle
    bundle.putExtras(args)
    return this
}

/** Create bundle from [extras] */
fun Bundle.putExtras(extras: Array<out Pair<String, Any?>> = emptyArray()) = apply {
    for ((key, value) in extras) {
        when (value) {
            null -> Unit // poor mans null safety
            is IBinder -> putBinder(key, value)
            is Boolean -> putBoolean(key, value)
            is Bundle -> putBundle(key, value)
            is Byte -> putByte(key, value)
            is Char -> putChar(key, value)
            is CharSequence -> putCharSequence(key, value)
            is Double -> putDouble(key, value)
            is Float -> putFloat(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Parcelable -> putParcelable(key, value)
            is Short -> putShort(key, value)
            is Size -> putSize(key, value)
            is SizeF -> putSizeF(key, value)
            is String -> putString(key, value)
            is java.io.Serializable -> {
                Log.w("BundleExtensions.kt", "Warning: using Serializable for bundling value of class ${value.javaClass}")
                putSerializable(key, value)
            }
            is PersistableBundle -> putAll(value)
            else -> throw IllegalArgumentException("Cannot put to bundle, unsupported type: ${value.javaClass}")
        }
    }
}

/** Create support fragment with given args */
@Deprecated("Please use Fragment#withArgs() func instead")
inline fun <reified T : Fragment> createSupportFragment(vararg args: Pair<String, Any?> = emptyArray()): T {
    val instance = try {
        T::class.java.newInstance()
    } catch (e: InstantiationException) {
        throw InstantiationException("Fragment ${T::class.simpleName} does not ")
    }
    instance.arguments = Bundle().putExtras(args)
    return instance
}

/** Create fragment with given args */
@Deprecated("Please use Fragment#withArgs() func instead")
inline fun <reified T : android.app.Fragment> createFragment(vararg args: Pair<String, Any?> = emptyArray()): T {
    val instance = try {
        T::class.java.newInstance()
    } catch (e: InstantiationException) {
        throw InstantiationException("Fragment ${T::class.simpleName} does not ")
    }
    instance.arguments = Bundle().putExtras(args)
    return instance
}