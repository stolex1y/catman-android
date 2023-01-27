package ru.stolexiy.catman.ui.util.validation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.stolexiy.catman.ui.util.validation.Condition.Companion.isValid
import java.io.Serializable

abstract class ValidatedEntity : Serializable {
    private val propsValidity: MutableList<Boolean> = mutableListOf()

    protected fun <T> validatedProperty(initialValue: T, condition: Condition<T?>): ValidatedProperty<T> =
        ValidatedProperty(initialValue, condition)

    private val _isValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isValidAsFlow: StateFlow<Boolean> = _isValid.asStateFlow()
    val isValid: Boolean = _isValid.value

    init {
    }

    private fun updateValidity() {
        _isValid.value = propsValidity.all { it }
    }

    inner class ValidatedProperty<T> constructor(
        initialValue: T,
        val condition: Condition<T?> = Conditions.None<T?>()
    ) {
        private val index = propsValidity.size

        private val _state: MutableStateFlow<T> = MutableStateFlow(initialValue)
        val asFlow: StateFlow<T> = _state.asStateFlow()
        val value: T
            get() = _state.value

        val isValid: Boolean
            get() = condition.isValid(value)

        private val _error: MutableStateFlow<Int?> = MutableStateFlow(condition.validate(initialValue).errorMessageRes)
        val errorFlow: StateFlow<Int?> = _error.asStateFlow()
        val error: Int?
            get() = _error.value

        init {
            propsValidity.add(isValid)
        }

        fun set(value: T) {
            if (value != _state.value) {
                _state.value = value
                if (this@ValidatedEntity.propsValidity.size <= index)
                    throw IllegalStateException()
                else {
                    this@ValidatedEntity.apply {
                        propsValidity[index] = this@ValidatedProperty.isValid
                        updateValidity()
                    }
                }

                condition.validate(value).takeIf { it.isNotValid }?.let {
                    _error.value = it.errorMessageRes
                } ?: run {
                    _error.value = null
                }
            }

            /*condition.validate(value).takeIf { it.isNotValid }?.let {
                _error.value = it.errorMessageRes
                throw InvalidValueException(it.errorMessageRes)
            } ?: run {
                _error.value = null
            }*/
        }

        fun get() = value

        override fun toString(): String {
            return value.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (other == null)
                return false
            when {
                other is ValidatedProperty<*> -> {
                    if ((other.value == null) xor (this.value == null))
                        return false
                    else if ((other.value == null) and (this.value == null))
                        return true
                    else if (other.value!!::class == this.value!!::class) {
                        return this.value == other.value
                    }
                }
                this.value == null -> return false
                other::class == this.value!!::class -> return this.value == other
            }
            return false
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }
    }
}