package ru.stolexiy.catman.ui.dialog.category.model

import android.content.Context
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.adapter.AbstractArrayAdapter

class ColorWithNameAdapter(
    items: List<Color>,
    context: Context
) : AbstractArrayAdapter<Color>(
    R.layout.list_item_color_wth_name,
    items,
    context
)
