package ru.stolexiy.catman.ui.util.validation

import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.stolexiy.catman.ui.util.Converter

object ValidationBindingAdapters {
    @BindingAdapter("bind", "bindConverter")
    @JvmStatic
    fun <T> bindTo(view: TextInputLayout, flow: StateFlow<T?>, converter: (T?) -> String) {
        bindTo(view, flow, Converter { converter(it) })
    }

    @BindingAdapter("bind", "bindConverter")
    @JvmStatic
    fun <T> bindTo(view: TextInputLayout, flow: StateFlow<T?>, converter: Converter<T?, String>) {
        view.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    flow.distinctUntilChangedBy { it }.collect {
                        converter.convert(it).takeIf { it != view.editText?.text.toString() }
                            ?.let { newText ->
                                view.editText?.setText(newText)
                            }
                    }
                }
            }
        }
    }

    @BindingAdapter("bind")
    @JvmStatic
    fun bindTo(view: TextInputLayout, flow: StateFlow<String?>) {
        bindTo(view, flow) { source -> source ?: "" }
    }

    @BindingAdapter("bind", "bindConverter")
    @JvmStatic
    fun <T> bindTo(view: TextView, flow: StateFlow<T?>, converter: Converter<T?, String>) {
        view.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    flow.distinctUntilChangedBy { it }.collect {
                        converter.convert(it).takeIf { it != view.text }?.let { newText ->
                            view.text = newText
                        }
                    }
                }
            }
        }
    }

    @BindingAdapter("bind")
    @JvmStatic
    fun bindTo(view: TextView, flow: StateFlow<String?>) {
        bindTo(view, flow) { source -> source ?: "" }
    }

    @BindingAdapter("bindError")
    @JvmStatic
    fun <T> bindErrorTo(
        view: TextInputLayout,
        validatedProperty: ValidatedEntity.ValidatedProperty<T?>
    ) {
        val updateError = {
            validatedProperty.error?.let {
                view.error = view.context.getString(it)
            } ?: run {
                view.error = ""
            }
        }
        view.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    validatedProperty.errorFlow.filter { it == null }.collect {
                        updateError()
                    }
                }
            }
        }
        view.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateError()
            }
        }
        view.editText?.doOnTextChanged { _, _, _, _ ->
            updateError()
        }

    }

    @BindingAdapter("validateBy")
    @JvmStatic
    fun validatedBy(view: TextInputLayout, property: ValidatedEntity.ValidatedProperty<String?>) {
        val editText = view.editText ?: return
        bindTo(view, property.asFlow)

        editText.doOnTextChanged { text, _, _, _ ->
            property.set(text.toString())
        }
        bindErrorTo(view, property)
    }
}
