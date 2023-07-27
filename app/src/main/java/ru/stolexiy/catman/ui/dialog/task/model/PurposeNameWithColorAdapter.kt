package ru.stolexiy.catman.ui.dialog.task.model

import android.content.Context
import ru.stolexiy.catman.R
import ru.stolexiy.catman.ui.util.adapter.AbstractArrayAdapter

class PurposeNameWithColorAdapter(
    items: List<Purpose>,
    context: Context,
) : AbstractArrayAdapter<Purpose>(
    R.layout.list_item_purpose_name,
    items,
    context
)
