package ru.stolexiy.catman.ui.util.validators

import ru.stolexiy.catman.ui.util.validators.viewbinders.ConditionViewBinder

class ValidatedForm(
    initialValidators: Collection<ConditionViewBinder<*, *>>? = null,
    private var operator: Validator.Operator = Validator.Operator.Conjunction()
) {
    protected val validators: MutableSet<ConditionViewBinder<*, *>> = LinkedHashSet()

    init {
        initialValidators?.run(validators::addAll)
    }

    fun addValidators(newValidators: Collection<ConditionViewBinder<*, *>>) {
        if (newValidators.isEmpty()) {
            return
        }
        validators.addAll(newValidators)
    }

    fun addValidator(validator: ConditionViewBinder<*, *>) {
        validators.add(validator)
    }

    fun removeValidator(validator: ConditionViewBinder<*, *>) {
        validators.remove(validator)
    }

    fun validate(): ValidationResult {
        val results = validators
            .map { it.validate() }
            .toSet()
        return operator.validate(results)
    }


}