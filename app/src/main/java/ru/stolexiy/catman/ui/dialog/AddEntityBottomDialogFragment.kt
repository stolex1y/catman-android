package ru.stolexiy.catman.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class AddEntityBottomDialogFragment(private val layout: Int) : BottomSheetDialogFragment() {
    protected var _binding: ViewDataBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout, container, false)
        _binding = DataBindingUtil.getBinding(view)
        return view
    }
}