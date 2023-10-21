package io.cobrowse.sample.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import io.cobrowse.CobrowseIO
import io.cobrowse.sample.R
import io.cobrowse.sample.ui.CobrowseViewModelFactory

/**
 * Activity that hosts all fragments when user is logged in.
 */
class MainActivity : AppCompatActivity(), CobrowseIO.Redacted {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var viewModel: MainHostViewModel
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main_host, menu)
                this@MainActivity.menu = menu
                updateUiWithSession(viewModel.cobrowseDelegate.current.value)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.end_cobrowse_session) {
                    viewModel.endCobrowseSession()
                    return true
                }
                return false
            }
        })

        viewModel = ViewModelProvider(this, CobrowseViewModelFactory())
            .get(MainHostViewModel::class.java)
        viewModel.cobrowseDelegate.current.observe(this@MainActivity, Observer {
            updateUiWithSession(it)
        })

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
    }

    private fun updateUiWithSession(session: io.cobrowse.Session?) {
        menu?.findItem(R.id.end_cobrowse_session)?.let {
            it.isVisible = session?.isActive == true
        }
    }

    override fun redactedViews(): MutableList<View> {
        return navHostFragment.childFragmentManager.fragments
            .filter { it is CobrowseIO.Redacted }
            .flatMap { (it as CobrowseIO.Redacted).redactedViews() }
            .filterNotNull()
            .toMutableList()
    }
}