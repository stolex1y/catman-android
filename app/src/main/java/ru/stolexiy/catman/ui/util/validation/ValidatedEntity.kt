package ru.stolexiy.catman.ui.util.validation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.stolexiy.catman.ui.util.validation.Condition.Companion.isValid
import java.io.Serializable

abstract class ValidatedEntity : Serializable {
    private val propsValidity: MutableList<Boolean> = mutableListOf()

    protected fun <T> validatedProperty(
        initialValue: T,
        condition: Condition<T?> = Conditions.None()
    ): ValidatedProperty<T> =
        ValidatedProperty(initialValue, condition)

    @Transient
    private val _isValid: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @Transient
    val isValidAsFlow: StateFlow<Boolean> = _isValid.asStateFlow()
    val isValid: Boolean
        get() = _isValid.value

    private fun updateValidity() {
        _isValid.value = propsValidity.all { it }
    }

    inner class ValidatedProperty<T>(
        initialValue: T,
        val condition: Condition<T?> = Conditions.None<T?>()
    ) : Serializable {
        private val index = propsValidity.size

        @Transient
        private val _state: MutableStateFlow<T> = MutableStateFlow(initialValue)

        @Transient
        val asFlow: StateFlow<T> = _state.asStateFlow()
        val value: T
            get() = _state.value

        val isValid: Boolean
            get() = condition.isValid(value)

        @Transient
        private val _error: MutableStateFlow<Int?> =
            MutableStateFlow(condition.validate(initialValue).errorMessageRes)

        @Transient
        val errorFlow: StateFlow<Int?> = _error.asStateFlow()
        val error: Int?
            get() = _error.value

        init {
            propsValidity.add(isValid)
            updateValidity()
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
        }

        fun get() = value

        override fun toString(): String {
            return value.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (other == null)
                return false

            if (other is ValidatedProperty<*>) {
                return if ((other.value == null) xor (this.value == null))
                    false
                else if ((other.value == null) and (this.value == null))
                    true
                else if (other.value!!::class == this.value!!::class)
                    this.value!! == other.value!!
                else
                    false
            } else {
                if (this.value == null)
                    return false
                else if (other::class == this.value!!::class)
                    return this.value == other
            }
            return false
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }
    }
}
