package ru.stolexiy.catman.ui.util.validation

import androidx.annotation.StringRes
import ru.stolexiy.common.DateUtils.maxZonedDateTime
import ru.stolexiy.common.DateUtils.minZonedDateTime
import ru.stolexiy.common.DateUtils.today
import java.io.Serializable
import java.time.ZonedDateTime

/**
 * Template classes for creating conditions ([Condition])
 * */
object Conditions {
    class None<T> : Condition<T?>, Serializable {
        override fun validate(value: T?): ValidationResult {
            return ValidationResult.valid()
        }
    }

    /**
     * Checks that a value is not null
     * true - not null
     * false - null
     *
     * @param errorText - Error message when value is null
     * **/
    class NotNull<T>(@StringRes private val errorStringRes: Int) : Condition<T?>, Serializable {
        override fun validate(value: T?): ValidationResult {
            return if (value != null) {
                ValidationResult.create(true, null)
            } else {
                ValidationResult.create(false, errorStringRes)
            }
        }
    }

    /**
     * Required text [CharSequence] field
     * true - The field is not empty, not null and does not consist of only spaces
     * false - The field is empty, null, or contains only spaces
     * **/
    class RequiredField<T : CharSequence>(@StringRes private val errorStringRes: Int) :
        Condition<T?>, Serializable {
        override fun validate(value: T?): ValidationResult {
            return if (value.isNullOrBlank()) {
                ValidationResult.create(false, errorStringRes)
            } else {
                ValidationResult.valid()
            }
        }
    }

    class TextMaxLength<T : CharSequence?>(
        val maxLength: Int,
        @StringRes private val errorStringRes: Int
    ) : Condition<T?>, Serializable {
        override fun validate(value: T?): ValidationResult {
            return if ((value?.length ?: 0) <= maxLength) {
                ValidationResult.valid()
            } else {
                ValidationResult.create(false, errorStringRes)
            }
        }
    }

    class TextMinLength<T : CharSequence>(
        val minLength: Int,
        @StringRes private val errorStringRes: Int
    ) : Condition<T?>, Serializable {

        override fun validate(value: T?): ValidationResult {
            return if ((value?.length ?: 0) >= minLength) {
                ValidationResult.valid()
            } else {
                ValidationResult.create(false, errorStringRes)
            }
        }
    }

    class TextLengthRange<T : CharSequence>(
        val textLenMin: Int,
        val textLenMax: Int,
        @StringRes private val errorStringRes: Int
    ) : Condition<T?>, Serializable {
        override fun validate(value: T?): ValidationResult {
            val textLength = (value?.length ?: 0)

            return if (textLength in textLenMin..textLenMax) {
                ValidationResult.create(true)
            } else {
                ValidationResult.create(false, errorStringRes)
            }
        }
    }

    class TextLength<T : CharSequence>(
        val textLength: Int,
        @StringRes private val errorStringRes: Int
    ) : Condition<T?>, Serializable {

        override fun validate(value: T?): ValidationResult {
            return if ((value?.length ?: 0) == textLength) {
                ValidationResult.create(true)
            } else {
                ValidationResult.create(false, errorStringRes)
            }
        }
    }

    class RegEx<T : CharSequence?>(
        private val regEx: Regex,
        @StringRes private val errorStringRes: Int
    ) : Condition<T?>, Serializable {
        override fun validate(value: T?): ValidationResult {
            val text = value ?: ""
            return if (text.matches(regEx)) {
                ValidationResult.create(true)
            } else {
                ValidationResult.create(false, errorStringRes)
            }
        }
    }

    class DateRange(
        val min: ZonedDateTime = minZonedDateTime(),
        val max: ZonedDateTime = maxZonedDateTime(),
        @StringRes private val errorStringRes: Int
    ) : Condition<ZonedDateTime?>, Serializable {

        override fun validate(value: ZonedDateTime?): ValidationResult {
            return if (value == null)
                ValidationResult.invalid(errorStringRes)
            else
                ValidationResult.create(!value.isBefore(min) && !value.isAfter(max), errorStringRes)
        }

        companion object {
            fun fromToday(errorStringRes: Int) =
                DateRange(today(), errorStringRes = errorStringRes)
        }
    }
}
