package ru.stolexiy.catman.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

abstract class AddEntityBottomDialogFragment(
    private val layout: Int,
    private val onDismiss: () -> Unit = {},
    private val onCancel: () -> Unit = {}
) : BottomSheetDialogFragment() {
    protected var _binding: ViewDataBinding? = null

    private val callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            this@AddEntityBottomDialogFragment.onStateChanged(bottomSheet, newState)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            this@AddEntityBottomDialogFragment.onSlide(bottomSheet, slideOffset)
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
        _binding = DataBindingUtil.bind(view)
        val bottomSheetBehavior = (dialog as BottomSheetDialog).behavior
        bottomSheetBehavior.saveFlags = BottomSheetBehavior.SAVE_ALL
        bottomSheetBehavior.addBottomSheetCallback(callback)
        bottomSheetBehavior.maxHeight = Int.MAX_VALUE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        bottomSheetBehavior.halfExpandedRatio = 0.5f
        bottomSheetBehavior.expandedOffset = 0
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    protected fun onStateChanged(bottomSheet: View, newState: Int) {
    }

    protected fun onSlide(bottomSheet: View, slideOffset: Float) {
    }

    override fun onCancel(dialog: DialogInterface) {
        onCancel()
        super.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        Toast.makeText(requireContext(), "Dismiss dialog...", Toast.LENGTH_SHORT).show()
        onDismiss()
        super.onDismiss(dialog)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("destroy dialog")
        _binding = null
    }
}