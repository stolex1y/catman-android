package ru.stolexiy.catman.ui.util.validation

import androidx.annotation.StringRes
import java.io.Serializable

fun interface Condition<T> : Serializable {
    fun validate(value: T?): ValidationResult

    /**
     * Addition of conditions
     * Boolean OR analog
     *
     * @see ValidationResult.plus
     * **/
    operator fun plus(condition: Condition<T>): Condition<T> {
        return Condition { value ->
            this.validate(value) + condition.validate(value)
        }
    }

    /**
     * Multiplication of validation results
     * Boolean AND analogue
     *
     * @see ValidationResult.times
     * **/
    operator fun times(condition: Condition<T>): Condition<T> {
        return Condition { value ->
            this.validate(value) * condition.validate(value)
        }
    }


    companion object {
        inline fun <V> create(
            @StringRes errorMessageRes: Int,
            crossinline isValueValid: (value: V?) -> Boolean
        ): Condition<V?> = Condition { value ->
            if (isValueValid(value)) {
                ValidationResult.valid()
            } else {
                ValidationResult.invalid(errorMessageRes)
            }
        }

        fun <T> Condition<T>.isValid(value: T): Boolean {
            return this.validate(value).isValid
        }
    }
}
