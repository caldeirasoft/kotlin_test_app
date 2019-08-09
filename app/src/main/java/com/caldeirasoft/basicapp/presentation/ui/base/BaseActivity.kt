package com.caldeirasoft.basicapp.presentation.ui.base

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.presentation.ui.base.annotation.AnnotationManager


abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setContentView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.home)
            super.finish()
        return super.onOptionsItemSelected(item)
    }

    protected open fun setContentView() {
        val meta = AnnotationManager.getLayoutOrThrow(this)
        super.setContentView(meta.layoutId)
    }
}