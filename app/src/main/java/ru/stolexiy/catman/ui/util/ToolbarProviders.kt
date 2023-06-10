package ru.stolexiy.catman.ui.util

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import ru.stolexiy.catman.R
import timber.log.Timber

object ToolbarProviders {

    fun toolbarWithSettingsProvider(itemClickListener: () -> Unit) =
        object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_toolbar_with_settings, menu)
                Timber.d("menu with settings created")
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.settings -> {
                        Timber.d("settings item clicked")
                        itemClickListener()
                        true
                    }

                    else -> false
                }
            }
        }
}
