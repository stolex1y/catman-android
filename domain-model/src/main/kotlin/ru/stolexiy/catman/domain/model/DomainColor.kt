package ru.stolexiy.catman.domain.model

import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import java.io.Serializable

/**
 * @param argb color in argb format
 * @param name string resource id
 */
data class DomainColor(
    @ColorInt val argb: Int,
    @StringRes val name: Int
) : Serializable
