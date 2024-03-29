package ru.stolexiy.catman.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

abstract class AbstractBottomDialogFragment(
    @LayoutRes private val layout: Int,
    private val onDestroyDialog: () -> Unit = {},
) : BottomSheetDialogFragment() {

    private val callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            this@AbstractBottomDialogFragment.onStateChanged(bottomSheet, newState)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            this@AbstractBottomDialogFragment.onSlide(bottomSheet, slideOffset)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("create dialog")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout, container, false)
        (dialog as BottomSheetDialog).behavior.apply {
            saveFlags = BottomSheetBehavior.SAVE_ALL
            addBottomSheetCallback(callback)
            maxHeight = Int.MAX_VALUE
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
            halfExpandedRatio = 0.5f
            expandedOffset = 0
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    protected fun onStateChanged(bottomSheet: View, newState: Int) {
    }

    protected fun onSlide(bottomSheet: View, slideOffset: Float) {
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyDialog()
        Timber.d("destroy dialog")
    }
}
