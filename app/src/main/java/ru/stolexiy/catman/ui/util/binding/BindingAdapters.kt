package ru.stolexiy.catman.ui.util.binding

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.toSpanned
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.stolexiy.catman.ui.util.Converter
import ru.stolexiy.catman.ui.util.validation.ValidatedEntity.ValidatedProperty

object BindingAdapters {
    @BindingAdapter("isGone")
    @JvmStatic
    fun viewIsGone(view: View, isGone: Boolean) {
        view.visibility = if (isGone)
            View.GONE
        else
            View.VISIBLE
    }

    @BindingAdapter("tintColor")
    @JvmStatic
    fun color(imageView: ImageView, color: Int?) {
        val tintColor = color ?: Color.TRANSPARENT
        imageView.drawable.setTint(tintColor)
    }

    @BindingAdapter("startIconTintColor")
    @JvmStatic
    fun tintColor(textInputLayout: TextInputLayout, color: Int?) {
        val tintColor = color ?: Color.TRANSPARENT
        textInputLayout.startIconDrawable?.setTint(tintColor)
    }

    @BindingAdapter("startIconIsGone")
    @JvmStatic
    fun startIconIsGone(inputLayout: TextInputLayout, isGone: Boolean) {
        inputLayout.isStartIconVisible = !isGone
    }

    @BindingAdapter("endIconIsGone")
    @JvmStatic
    fun endIconIsGone(inputLayout: TextInputLayout, isGone: Boolean) {
        inputLayout.isEndIconVisible = !isGone
    }

    @BindingAdapter("popupBackground")
    @JvmStatic
    fun popupBackground(autoCompleteTextView: MaterialAutoCompleteTextView, drawableRes: Int) {
        autoCompleteTextView.setDropDownBackgroundResource(drawableRes)
    }

    @BindingAdapter("hintAsterisk")
    @JvmStatic
    fun addAsteriskToHint(inputLayout: TextInputLayout, color: Int) {
        val asterisk = '*'
        val hint = inputLayout.hint.toString() + " " + asterisk
        inputLayout.hint = hint.colorize(inputLayout.context.getColor(color), asterisk.toString())
    }

    @BindingAdapter(value = ["textColoring", "textColoringPart"], requireAll = false)
    @JvmStatic
    fun colorizeText(textView: TextView, color: Int?, part: String?) {
        if (color == null)
            return
        val text = textView.text ?: return
        textView.text = text.colorize(textView.context.getColor(color), part ?: text)
    }


    fun CharSequence.colorize(color: Int, part: CharSequence = this): Spanned {
        val spannable = SpannableString(this)
        val start = this.indexOf(part.toString())
        if (start == -1)
            return spannable
        spannable.setSpan(
            ForegroundColorSpan(color),
            start,
            start + part.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable.toSpanned()

    }

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
    fun <T> bindErrorTo(view: TextInputLayout, validatedProperty: ValidatedProperty<T?>) {
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
    fun validatedBy(view: TextInputLayout, property: ValidatedProperty<String?>) {
        val editText = view.editText ?: return
        bindTo(view, property.asFlow)

        editText.doOnTextChanged { text, _, _, _ ->
            property.set(text.toString())
        }
        bindErrorTo(view, property)
    }
}