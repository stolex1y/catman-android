package ru.stolexiy.catman.ui.util.validation

import androidx.annotation.StringRes

interface ValidationResult {
    val isValid: Boolean
    val isNotValid: Boolean
        get() = !isValid

    val errorMessageRes: Int?

    /**
     * Addition of conditions
     * Boolean OR analog
     *
     * Example:
     * ValidationResult(true) + ValidationResult(false, "error") = ValidationResult(true)
     * **/
    operator fun plus(increment: ValidationResult): ValidationResult {
        val isValid = this.isValid || increment.isValid

        val errorMessage = when {
            this.isNotValid -> this.errorMessageRes
            increment.isNotValid -> increment.errorMessageRes
            else -> null
        }

        return create(isValid, errorMessage)
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

        val errorMessage = when {
            !this.isValid -> this.errorMessageRes
            !increment.isValid -> increment.errorMessageRes
            else -> null
        }

        return create(isValid, errorMessage)
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
        fun create(isValid: Boolean, errorMessage: Int? = null): ValidationResult {
            return if (isValid) {
                valid()
            } else {
                invalid(errorMessage)
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
            }
        }

        /**
         * Creates invalid [ValidationResult]
         *
         * @return [ValidationResult] where [ValidationResult.isValid] == false and [ValidationResult.errorMessageRes] == [errorMessageRes]
         * **/
        fun invalid(@StringRes errorMessageRes: Int?): ValidationResult {
            return object : ValidationResult {
                override val isValid: Boolean
                    get() = false
                override val errorMessageRes: Int?
                    get() = errorMessageRes
            }
        }
    }
}