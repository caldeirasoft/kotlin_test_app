package com.caldeirasoft.basicapp.ui.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.R

//Colombet 06.63.54.26.65

abstract class BaseActivity : AppCompatActivity() {

    /**
     * Adds a [Fragment] to this activity's layout.
     */
    fun addFragment(fragment: Fragment, tag: String,
                    addToBackSTack: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container_fragment, fragment, tag)
        if (addToBackSTack) fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

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
        super.setContentView(getLayout())
    }

    abstract protected fun getLayout(): Int
}