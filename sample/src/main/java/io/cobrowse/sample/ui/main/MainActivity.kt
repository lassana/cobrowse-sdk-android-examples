package io.cobrowse.sample.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import io.cobrowse.CobrowseIO
import io.cobrowse.sample.R


class MainActivity : AppCompatActivity(), CobrowseIO.Redacted {

    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
    }

    override fun redactedViews(): MutableList<View> {
        return navHostFragment.childFragmentManager.fragments
            .filter { it is CobrowseIO.Redacted }
            .flatMap { (it as CobrowseIO.Redacted).redactedViews() }
            .filterNotNull()
            .toMutableList()
    }
}