package ru.stolexiy.catman.ui.dialog.common.listadapter

import android.content.Context
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.dialog.common.model.Category
import ru.stolexiy.catman.ui.util.adapter.AbstractArrayAdapter

class CategoryNameWithColorAdapter(
    items: List<Category>,
    context: Context,
) : AbstractArrayAdapter<Category>(
    R.layout.list_item_category_name_with_color,
    items,
    context
)
