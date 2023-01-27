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

abstract class AbstractBottomDialogFragment(
    private val mLayout: Int,
) : BottomSheetDialogFragment() {
    protected var binding: ViewDataBinding? = null

    private val mCallback = object : BottomSheetBehavior.BottomSheetCallback() {
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
        val view = inflater.inflate(mLayout, container, false)
        binding = DataBindingUtil.bind(view)
        (dialog as BottomSheetDialog).behavior.apply {
            saveFlags = BottomSheetBehavior.SAVE_ALL
            addBottomSheetCallback(mCallback)
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

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        Toast.makeText(requireContext(), "Dismiss dialog...", Toast.LENGTH_SHORT).show()
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("destroy dialog")
    }
}