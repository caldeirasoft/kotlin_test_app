package com.caldeirasoft.basicapp.presentation.views

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.TypedValue

/**
 * A Drawable object that draws text.
 * A TextDrawable accepts most of the same parameters that can be applied to
 * [android.widget.TextView] for displaying and formatting text.
 *
 * Optionally, a [Path] may be supplied on which to draw the text.
 *
 * A TextDrawable has an intrinsic size equal to that required to draw all
 * the text it has been supplied, when possible.  In cases where a [Path]
 * has been supplied, the caller must explicitly call
 * [setBounds()][.setBounds] to provide the Drawable
 * size based on the Path constraints.
 */
class TextDrawable(context: Context) : Drawable() {

    /* Resources for scaling values to the given device */
    private val mResources: Resources = context.resources
    /* Paint to hold most drawing primitives for the text */
    private val mTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    /* Layout is used to measure and draw the text */
    private var mTextLayout: StaticLayout? = null
    /* Alignment of the text inside its bounds */
    /**
     * Return the current text alignment setting
     */
    /**
     * Set the text alignment.  The alignment itself is based on the text layout direction.
     * For LTR text NORMAL is left aligned and OPPOSITE is right aligned.
     * For RTL text, those alignments are reversed.
     * @param align Text alignment value.  Should be set to one of:
     *
     * [Layout.Alignment.ALIGN_NORMAL],
     * [Layout.Alignment.ALIGN_NORMAL],
     * [Layout.Alignment.ALIGN_OPPOSITE].
     */
    var textAlign: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL
        set(align) {
            if (textAlign != align) {
                field = align
                measureContent()
            }
        }
    /* Optional path on which to draw the text */
    private var mTextPath: Path? = null
    /* Stateful text color list */
    private var mTextColors: ColorStateList? = null
    /* Container for the bounds to be reported to widgets */
    private val mTextBounds: Rect = Rect()
    /* Text string to draw */
    /**
     * Return the text currently being displayed
     */
    /**
     * Set the text that will be displayed
     * @param text Text to display
     */
    var text: CharSequence? = ""
        set(text) {
            var text = text
            if (text == null) text = ""

            field = text

            measureContent()
        }

    /**
     * Return the current text size, in pixels
     */
    /**
     * Set the text size.  The value will be interpreted in "sp" units
     * @param size Text size value, in sp
     */
    var textSize: Float
        get() = mTextPaint.textSize
        set(size) = setTextSize(TypedValue.COMPLEX_UNIT_SP, size)

    /**
     * Return the horizontal stretch factor of the text
     */
    /**
     * Set the horizontal stretch factor of the text
     * @param size Text scale factor
     */
    var textScaleX: Float
        get() = mTextPaint.textScaleX
        set(size) {
            if (size != mTextPaint.textScaleX) {
                mTextPaint.textScaleX = size
                measureContent()
            }
        }

    /**
     * Return the current typeface and style that the Paint
     * using for display.
     */
    /**
     * Sets the typeface and style in which the text should be displayed.
     * Note that not all Typeface families actually have bold and italic
     * variants, so you may need to use
     * [.setTypeface] to get the appearance
     * that you actually want.
     */
    var typeface: Typeface?
        get() = mTextPaint.typeface
        set(tf) {
            if (mTextPaint.typeface !== tf) {
                mTextPaint.typeface = tf

                measureContent()
            }
        }


    init {
        //Used to load and scale resource items
        //Definition of this drawables size
        //Paint to use for the text
        mTextPaint.density = mResources.displayMetrics.density
        mTextPaint.isDither = true

        var textSize = 15
        var textColor: ColorStateList? = null
        var styleIndex = -1
        var typefaceIndex = -1

        //Set default parameters from the current theme
        val a = context.theme.obtainStyledAttributes(themeAttributes)
        val appearanceId = a.getResourceId(0, -1)
        a.recycle()

        var ap: TypedArray? = null
        if (appearanceId != -1) {
            ap = context.obtainStyledAttributes(appearanceId, appearanceAttributes)
        }
        if (ap != null) {
            for (i in 0 until ap.indexCount) {
                val attr = ap.getIndex(i)
                when (attr) {
                    0 //Text Size
                    -> textSize = a.getDimensionPixelSize(attr, textSize)
                    1 //Typeface
                    -> typefaceIndex = a.getInt(attr, typefaceIndex)
                    2 //Text Style
                    -> styleIndex = a.getInt(attr, styleIndex)
                    3 //Text Color
                    -> textColor = a.getColorStateList(attr)
                    else -> {
                    }
                }
            }

            ap.recycle()
        }

        setTextColor(if (textColor != null) textColor else ColorStateList.valueOf(-0x1000000))
        setRawTextSize(textSize.toFloat())

        var tf: Typeface? = null
        when (typefaceIndex) {
            SANS -> tf = Typeface.SANS_SERIF

            SERIF -> tf = Typeface.SERIF

            MONOSPACE -> tf = Typeface.MONOSPACE
        }

        setTypeface(tf, styleIndex)
    }

    /**
     * Set the text size, using the supplied complex units
     * @param unit Units for the text size, such as dp or sp
     * @param size Text size value
     */
    fun setTextSize(unit: Int, size: Float) {
        val dimension = TypedValue.applyDimension(unit, size,
                mResources.displayMetrics)
        setRawTextSize(dimension)
    }

    /*
     * Set the text size, in raw pixels
     */
    private fun setRawTextSize(size: Float) {
        if (size != mTextPaint.textSize) {
            mTextPaint.textSize = size

            measureContent()
        }
    }

    /**
     * Sets the typeface and style in which the text should be displayed,
     * and turns on the fake bold and italic bits in the Paint if the
     * Typeface that you provided does not have all the bits in the
     * style that you specified.
     *
     */
    fun setTypeface(tf: Typeface?, style: Int) {
        var tf = tf
        if (style > 0) {
            tf = if (tf == null) {
                Typeface.defaultFromStyle(style)
            } else {
                Typeface.create(tf, style)
            }

            typeface = tf
            // now compute what (if any) algorithmic styling is needed
            val typefaceStyle = if (tf != null) tf.style else 0
            val need = style and typefaceStyle.inv()
            mTextPaint.isFakeBoldText = need and Typeface.BOLD != 0
            mTextPaint.textSkewX = if (need and Typeface.ITALIC != 0) -0.25f else 0f
        } else {
            mTextPaint.isFakeBoldText = false
            mTextPaint.textSkewX = 0f
            typeface = tf
        }
    }

    /**
     * Set a single text color for all states
     * @param color Color value such as [Color.WHITE] or [Color.argb]
     */
    fun setTextColor(color: Int) {
        setTextColor(ColorStateList.valueOf(color))
    }

    /**
     * Set the text color as a state list
     * @param colorStateList ColorStateList of text colors, such as inflated from an R.color resource
     */
    fun setTextColor(colorStateList: ColorStateList) {
        mTextColors = colorStateList
        updateTextColors(state)
    }

    /**
     * Optional Path object on which to draw the text.  If this is set,
     * TextDrawable cannot properly measure the bounds this drawable will need.
     * You must call [setBounds()][.setBounds] before
     * applying this TextDrawable to any View.
     *
     * Calling this method with `null` will remove any Path currently attached.
     */
    fun setTextPath(path: Path) {
        if (mTextPath !== path) {
            mTextPath = path
            measureContent()
        }
    }

    /**
     * Internal method to take measurements of the current contents and apply
     * the correct bounds when possible.
     */
    private fun measureContent() {
        //If drawing to a path, we cannot measure intrinsic bounds
        //We must resly on setBounds being called externally
        if (mTextPath != null) {
            //Clear any previous measurement
            mTextLayout = null
            mTextBounds.setEmpty()
        } else {
            //Measure text bounds
            val desired = Math.ceil(Layout.getDesiredWidth(text, mTextPaint).toDouble())
            mTextLayout = StaticLayout(text, mTextPaint, desired.toInt(),
                    textAlign, 1.0f, 0.0f, false)
            mTextBounds.set(0, 0, mTextLayout!!.width, mTextLayout!!.height)
        }

        //We may need to be redrawn
        invalidateSelf()
    }

    /**
     * Internal method to apply the correct text color based on the drawable's state
     */
    private fun updateTextColors(stateSet: IntArray): Boolean {
        val newColor = mTextColors!!.getColorForState(stateSet, Color.WHITE)
        if (mTextPaint.color != newColor) {
            mTextPaint.color = newColor
            return true
        }

        return false
    }

    override fun onBoundsChange(bounds: Rect) {
        //Update the internal bounds in response to any external requests
        mTextBounds.set(bounds)
    }

    override fun isStateful(): Boolean {
        /*
         * The drawable's ability to represent state is based on
         * the text color list set
         */
        return mTextColors!!.isStateful
    }

    override fun onStateChange(state: IntArray): Boolean {
        //Upon state changes, grab the correct text color
        return updateTextColors(state)
    }

    override fun getIntrinsicHeight(): Int {
        //Return the vertical bounds measured, or -1 if none
        return if (mTextBounds.isEmpty) {
            -1
        } else {
            mTextBounds.bottom - mTextBounds.top
        }
    }

    override fun getIntrinsicWidth(): Int {
        //Return the horizontal bounds measured, or -1 if none
        return if (mTextBounds.isEmpty) {
            -1
        } else {
            mTextBounds.right - mTextBounds.left
        }
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val count = canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        if (mTextPath == null) {
            //Allow the layout to draw the text
            mTextLayout!!.draw(canvas)
        } else {
            //Draw directly on the canvas using the supplied path
            canvas.drawTextOnPath(text!!.toString(), mTextPath!!, 0f, 0f, mTextPaint)
        }
        canvas.restoreToCount(count)
    }

    override fun setAlpha(alpha: Int) {
        if (mTextPaint.alpha != alpha) {
            mTextPaint.alpha = alpha
        }
    }

    override fun getOpacity(): Int {
        return mTextPaint.alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        if (mTextPaint.colorFilter !== cf) {
            mTextPaint.colorFilter = cf
        }
    }

    companion object {

        /* Platform XML constants for typeface */
        private val SANS = 1
        private val SERIF = 2
        private val MONOSPACE = 3

        /* Attribute lists to pull default values from the current theme */
        private val themeAttributes = intArrayOf(android.R.attr.textAppearance)
        private val appearanceAttributes = intArrayOf(android.R.attr.textSize, android.R.attr.typeface, android.R.attr.textStyle, android.R.attr.textColor)
    }

}