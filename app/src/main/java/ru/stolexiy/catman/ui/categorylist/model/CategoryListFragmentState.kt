package ru.stolexiy.catman.ui.categorylist.model

sealed class CategoryListFragmentState {
    object Init : CategoryListFragmentState()
    object IsLoading : CategoryListFragmentState()
    data class LoadedData(val data: List<CategoryListItem>) : CategoryListFragmentState()
}
