package ru.stolexiy.catman.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        val START_FRAGMENT_EXTRA_KEY = "${Companion::class.java.name}.START_FRAGMENT"
        val START_FRAGMENT_ARGS_EXTRA_KEY = "${Companion::class.java.name}.START_FRAGMENT_ARGS"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navigationView = binding.navigationView
        drawerLayout = binding.drawerLayout
        toolbar = binding.toolbar
        setContentView(binding.root)
        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        setSupportActionBar(toolbar)
        setupNavGraph(getStartDestinationIdFromIntent(intent))
        setupNavigation()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getStartDestinationIdFromIntent(intent)?.let { fragmentId ->
            val args = getStartArgsFromIntent(intent)
            navController.navigate(
                fragmentId,
                args,
                NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(fragmentId, false)
                    .build()
            )
        }
    }

    private fun setupNavGraph(startDestinationId: Int? = null) =
        setupNavGraph(startDestinationId ?: R.id.category_list_fragment)

    private fun setupNavGraph(startDestinationId: Int) {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestinationId)
        navController.graph = navGraph
    }

    private fun setupNavigation() {
        // setup start destination
        navigationView.setCheckedItem(navController.graph.startDestinationId)
        navigationView.setupWithNavController(navController)

        fun currentDestinationIsTop() =
            navigationView.menu.findItem(navController.currentDestination!!.id) != null

        toolbar.setNavigationOnClickListener {
            if (!currentDestinationIsTop())
                navController.navigateUp(drawerLayout)
            else
                drawerLayout.open()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.title = destination.label
            if (currentDestinationIsTop()) {
                drawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED)
                toolbar.setNavigationIcon(R.drawable.menu)
            } else {
                drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
                toolbar.setNavigationIcon(R.drawable.back)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!currentDestinationIsTop()) navController.navigateUp(drawerLayout)
                else finish()
            }
        })

    }

    private fun getStartDestinationIdFromIntent(intent: Intent?): Int? {
        return intent?.getIntExtra(START_FRAGMENT_EXTRA_KEY, -1).takeIf { it != -1 }
    }

    private fun getStartArgsFromIntent(intent: Intent?): Bundle? {
        return intent?.getBundleExtra(START_FRAGMENT_ARGS_EXTRA_KEY)
    }
}
