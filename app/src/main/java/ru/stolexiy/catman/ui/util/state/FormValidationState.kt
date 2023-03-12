package ru.stolexiy.catman.ui.util.state

import ru.stolexiy.catman.ui.util.validation.ValidatedEntity

abstract class FormValidationState<T : ValidatedEntity> {
    val errors: Map<Int, Int?> = emptyMap()
    abstract val entity: T
//    val isValid: Boolean
//        get() = entity.isValid()
}