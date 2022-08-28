package ru.stolexiy.catman.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.savedstate.SavedStateRegistry
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navigationView = binding.navigationView
        drawerLayout = binding.drawerLayout
        toolbar = binding.toolbar
        setContentView(binding.root)
        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        setupNavGraph()
        setupNavigation()
    }

    private fun setupNavGraph(startDestinationId: Int = R.id.category_list_fragment) {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestinationId)
        navController.graph = navGraph
    }

    private fun setupNavigation() {
        // setup start destination
        navigationView.setCheckedItem(navController.graph.startDestinationId)

        fun currentDestinationIsTop() =
            navigationView.menu.findItem(navController.currentDestination!!.id) != null

        navigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == navController.currentDestination!!.id)
                return@setNavigationItemSelectedListener true

            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .setPopUpTo(navController.currentDestination!!.id, inclusive = true, saveState = true)
            val options = builder.build()
            navController.navigate(menuItem.itemId, null, options)
            binding.drawerLayout.close()
            menuItem.isChecked = true
            return@setNavigationItemSelectedListener true
        }

        binding.toolbar.setNavigationOnClickListener {
            if (!currentDestinationIsTop())
                navController.navigateUp(drawerLayout)
            else
                drawerLayout.open()
        }

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            binding.toolbar.title = destination.label ?: ""
            if (currentDestinationIsTop()) {
                binding.drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED)
                binding.toolbar.setNavigationIcon(R.drawable.menu)
            } else {
                binding.drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
                binding.toolbar.setNavigationIcon(R.drawable.back)
            }
        }

    }

}
