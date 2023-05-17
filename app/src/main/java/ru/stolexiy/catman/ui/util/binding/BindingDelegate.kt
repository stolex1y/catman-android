package ru.stolexiy.catman.ui.util.binding

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class BindingDelegate<T : ViewDataBinding>(
    private val rootView: View?,
    lifecycle: Lifecycle
) : ReadOnlyProperty<Any, T>, DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    private var binding: T? = null

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        requireNotNull(rootView) { "Can't create data binding with empty root view" }
        binding = DataBindingUtil.bind(rootView)!!
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        binding = null
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return binding!!
    }
}
