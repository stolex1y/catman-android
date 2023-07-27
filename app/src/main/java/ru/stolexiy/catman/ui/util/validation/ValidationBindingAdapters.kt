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
import ru.stolexiy.catman.ui.util.ToTextConverter

object ValidationBindingAdapters {
    @BindingAdapter("bind", "toTextConverter", requireAll = true)
    @JvmStatic
    fun <T> bindTo(
        view: TextInputLayout,
        property: ValidatedEntity.ValidatedProperty<T?>,
        toTextConverter: (T?) -> String
    ) {
        bindTo(view, property.asFlow, ToTextConverter { toTextConverter(it) })
    }

    @BindingAdapter("bind", "toTextConverter", requireAll = true)
    @JvmStatic
    fun <T> bindTo(
        view: TextInputLayout,
        property: ValidatedEntity.ValidatedProperty<T?>,
        toTextConverter: ToTextConverter<T?>
    ) {
        bindTo(view, property.asFlow, toTextConverter)
    }

    @BindingAdapter("bind", "toTextConverter", requireAll = true)
    @JvmStatic
    fun <T> bindTo(view: TextInputLayout, flow: StateFlow<T?>, toTextConverter: (T?) -> String) {
        bindTo(view, flow, ToTextConverter { toTextConverter(it) })
    }

    @BindingAdapter("bind", "toTextConverter", requireAll = true)
    @JvmStatic
    fun <T> bindTo(
        view: TextInputLayout,
        flow: StateFlow<T?>,
        toTextConverter: ToTextConverter<T?>
    ) {
        view.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    flow.distinctUntilChangedBy { it }.collect {
                        toTextConverter.convert(it).takeIf { it != view.editText?.text.toString() }
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

    @BindingAdapter("bind", "toTextConverter", requireAll = true)
    @JvmStatic
    fun <T> bindTo(view: TextView, flow: StateFlow<T?>, toTextConverter: Converter<T?, String>) {
        view.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    flow.distinctUntilChangedBy { it }.collect {
                        toTextConverter.convert(it).takeIf { it != view.text }?.let { newText ->
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
            validatedProperty.validationResult.takeIf { it.isNotValid }?.let {
                view.error = view.context.getString(it.errorMessageRes!!, it.errorMessageArgs)
            } ?: run {
                view.error = ""
            }
        }
        view.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    validatedProperty.validationResultFlow.filter { it.isValid }.collect {
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

    @BindingAdapter("validateBy", "textConverter", "toTextConverter", requireAll = true)
    @JvmStatic
    fun <T> validatedBy(
        view: TextInputLayout,
        property: ValidatedEntity.ValidatedProperty<T?>,
        textConverter: Converter<String?, T?>,
        toTextConverter: ToTextConverter<T?>
    ) {
        val editText = view.editText ?: return
        bindTo<T>(view, property.asFlow, toTextConverter)

        editText.doOnTextChanged { text, _, _, _ ->
            property.set(textConverter.convert(text.toString()))
        }
        bindErrorTo(view, property)
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
