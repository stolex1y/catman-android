package ru.stolexiy.catman.ui.dialog.valiador

import com.google.android.material.textfield.TextInputEditText
import ru.stolexiy.catman.R

class RequiredSelectingValidator(
    val textInput: TextInputEditText,
    val onSuccessInput: (input: String) -> Unit
) : Validator<String>(textInput) {
    override fun isValid(value: String?): Boolean = value?.trim() != ""

    override fun onError() {
        textInput.error = textInput.context.getString(R.string.required_field_error)
    }

    override fun onSuccess() {
        onSuccessInput(getValue()!!)
    }

    override fun getValue(): String? {
        return textInput.text?.toString()
    }
}