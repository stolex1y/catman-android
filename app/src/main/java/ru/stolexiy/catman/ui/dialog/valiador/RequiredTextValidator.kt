package ru.stolexiy.catman.ui.dialog.valiador

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import ru.stolexiy.catman.R

class RequiredTextValidator<out T : EditText>(
    val textInput: TextInputLayout,
    val textField: T,
) : Validator<String>(textField) {

    init {
        setListener()
    }

    override fun setListener() {
        super.setListener()
        textField.addTextChangedListener {
            valueUpdated()
        }
    }

    override fun isValid(value: String?): Boolean = value?.trim() != ""

    override fun onError() {
        textInput.error = textInput.context.getString(R.string.required_field_error)
    }

    override fun onSuccess() {
        textInput.error = ""
    }

    override fun getValue(): String? {
        return textField.text?.toString()
    }
}