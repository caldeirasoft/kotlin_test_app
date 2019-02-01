package com.caldeirasoft.basicapp.presentation.ui.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.presentation.ui.base.annotation.AnnotationManager

//Colombet 06.63.54.26.65

abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.home)
            super.finish()
        return super.onOptionsItemSelected(item)
    }

    open protected fun setContentView() {
        val meta = AnnotationManager.getLayoutOrThrow(this)
        super.setContentView(meta.layoutId)
    }
}