package ru.stolexiy.catman.core

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.core.view.MenuProvider
import ru.stolexiy.catman.R

class TopToolbarWithSettingsProvider(private val itemClickListener: () -> Unit) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_toolbar_with_settings, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.settings -> {
                itemClickListener()
                true
            }
            else -> false
        }
    }
}