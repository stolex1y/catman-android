package ru.stolexiy.catman.ui.util.validators.viewbinders

import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import ru.stolexiy.catman.ui.util.validators.Condition
import ru.stolexiy.catman.ui.util.validators.ValidationResult
import java.lang.ref.WeakReference

/**
 * Binds the [condition] validator to the view
 *
 * @param viewRef - Weak reference to <V>, which should react to the validator
 * @param condition - The condition that checks the data <D>
 * **/
abstract class ConditionViewBinder<V : View, D>(
    view: V,
    private val condition: Condition<D?>
) : DefaultLifecycleObserver {
    private val viewRef: WeakReference<V> = WeakReference(view)
    protected val view: V?; get() = viewRef.get()

    /**
     * Triggers validation
     * **/
    fun check() {
        onValidationResult(validate())
    }

    /**
     * Triggers validation and return result as Boolean (valid or invalid)
     * **/
    fun isValid(): Boolean {
        return validate().isValid
    }

    /**
     * Triggers validation with current data in view and return [ValidationResult]
     * **/
    fun validate(): ValidationResult {
        return condition.validate(getValidationData()).apply { onValidationResult(this) }
    }

    /**
     * Indicates that [ConditionViewBinder] is attached to lifecycle
     * **/
    open fun attach() {

    }

    @CallSuper
    open fun detach() {
        viewRef.clear()
    }

    /**
     * Returns data from the view that needs to be checked against the validator
     *
     * @return D - Data to be checked by the validator
     * **/
    abstract fun getValidationData(): D?

    /**
     * Reports the result of field validation
     *
     * @param view - view being checked
     * @param result - result of validation
     * **/
    protected abstract fun onValidationResult(result: ValidationResult?)

    private fun onStart() {
        attach()
    }

    private fun onDestroy() {
        detach()
    }
}