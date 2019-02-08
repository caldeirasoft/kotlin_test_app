package com.caldeirasoft.basicapp.presentation.utils.widget

import android.app.Dialog
import android.os.Bundle
import com.caldeirasoft.basicapp.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by arn on 03/01/2018.
 */
// https://gist.github.com/skimarxall/863585dcd7abde8f4153

open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

}