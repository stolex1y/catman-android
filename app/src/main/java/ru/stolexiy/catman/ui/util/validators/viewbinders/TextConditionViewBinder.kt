package ru.stolexiy.catman.ui.util.validators.viewbinders

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import ru.stolexiy.catman.ui.util.validators.Condition
import ru.stolexiy.catman.ui.util.validators.ValidationResult
import java.lang.ref.WeakReference

/**
 * [ConditionViewBinder] for text validation in the TextView (and its descendants)
 * After each change of the text in the text field, checks the text against the validator
 * Depending on the result of validation - sets an error on the field
 *
 * @param textViewRef - Weak reference to the TextView to be validated
 * @param validator - Validator that validates the text
 * **/
class TextConditionViewBinder<V : TextView>(
    textView: V,
    textInputLayout: TextInputLayout? = null,
    private val validator: Condition<CharSequence?>
) : ConditionViewBinder<V, CharSequence?>(textView, validator), TextWatcher, OnFocusChangeListener {

    private val textViewLayoutRef: WeakReference<TextInputLayout> = WeakReference(textInputLayout)
    private val textInputLayout: TextInputLayout?
        get() = textViewLayoutRef.get()

    fun activate() {
        view?.addTextChangedListener(this)
        view?.onFocusChangeListener = this
    }

    fun deactivate() {
        view?.removeTextChangedListener(this)
        view?.onFocusChangeListener = null
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) = check()

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus)
            check()
    }

    override fun getValidationData(): CharSequence? {
        return view?.text
    }

    override fun onValidationResult(result: ValidationResult?) {
        if (result?.isValid == true) {
            removeError()
        } else {
            showError(result?.errorMessage ?: "")
        }
    }

    private fun showError(error: String) {
        textInputLayout?.error = error
    }

    private fun removeError() {
        textInputLayout?.error = ""
    }
}

fun Pair<TextView, TextInputLayout>.validateBy(
    condition: Condition<CharSequence?>
): TextConditionViewBinder<TextView> {
    return TextConditionViewBinder(
        this.first,
        this.second,
        condition
    ).apply { activate() }
}
/*

fun TextView.validateBy(
    textInputLayout: TextInputLayout,
    condition: Condition<CharSequence?>
): TextConditionViewBinder<TextView> {
    val viewValidator = TextConditionViewBinder(
        this,
        textInputLayout,
        condition
    )
    viewValidator.activate()
    return viewValidator
}*/
