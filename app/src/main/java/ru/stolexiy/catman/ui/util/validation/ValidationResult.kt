package ru.stolexiy.catman.ui.util.validation

import androidx.annotation.StringRes

interface ValidationResult {
    val isValid: Boolean
    val isNotValid: Boolean
        get() = !isValid

    val errorMessageRes: Int?

    val errorMessageArgs: Array<out Any>?

    /**
     * Addition of conditions
     * Boolean OR analog
     *
     * Example:
     * ValidationResult(true) + ValidationResult(false, "error") = ValidationResult(true)
     * **/
    operator fun plus(increment: ValidationResult): ValidationResult {
        val isValid = this.isValid || increment.isValid

        return if (isValid)
            valid()
        else
            invalid(errorMessageRes!!, errorMessageArgs)
    }

    /**
     * Multiplication of validation results
     * Boolean AND analogue
     *
     * Example:
     * ValidationResult(true) * ValidationResult(false, "error") = ValidationResult(false, "error")
     * **/
    operator fun times(increment: ValidationResult): ValidationResult {
        val isValid = this.isValid && increment.isValid

        return if (isValid)
            valid()
        else {
            val errorMessage: Pair<Int, Array<out Any>?> = when {
                this.isNotValid -> this.errorMessageRes!! to this.errorMessageArgs
                else -> increment.errorMessageRes!! to increment.errorMessageArgs
            }
            invalid(errorMessage.first, errorMessage.second)
        }
    }

    companion object {
        /**
         * Creation of ValidationResult
         *
         * @param isValid - Result of validation
         * @param errorMessage - Error message
         *
         * @return [ValidationResult] where [ValidationResult.isValid] == [isValid] and [ValidationResult.errorMessageRes] == [errorMessage]
         * **/
        fun create(
            isValid: Boolean,
            errorMessage: Int,
            vararg errorMessageArgs: Any
        ): ValidationResult {
            return if (isValid) {
                valid()
            } else {
                invalid(errorMessage, errorMessageArgs)
            }
        }

        /**
         * Creates valid [ValidationResult]
         *
         * @return [ValidationResult] where [ValidationResult.isValid] == true and [ValidationResult.errorMessageRes] == null
         * **/
        fun valid(): ValidationResult {
            return object : ValidationResult {
                override val isValid: Boolean
                    get() = true
                override val errorMessageRes: Int?
                    get() = null

                override val errorMessageArgs: Array<Any>?
                    get() = null
            }
        }

        /**
         * Creates invalid [ValidationResult]
         *
         * @return [ValidationResult] where [ValidationResult.isValid] == false and [ValidationResult.errorMessageRes] == [errorMessageRes]
         * **/
        fun invalid(
            @StringRes errorMessageRes: Int,
            errorMessageArgs: Array<out Any>? = null
        ): ValidationResult {
            return object : ValidationResult {
                override val isValid: Boolean
                    get() = false
                override val errorMessageRes: Int
                    get() = errorMessageRes

                override val errorMessageArgs: Array<out Any>?
                    get() = errorMessageArgs
            }
        }
    }
}
