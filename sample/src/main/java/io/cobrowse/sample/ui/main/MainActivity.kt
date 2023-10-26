package io.cobrowse.sample.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.core.view.postDelayed
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.cobrowse.CobrowseIO
import io.cobrowse.Session
import io.cobrowse.sample.R
import io.cobrowse.sample.databinding.ActivityMainBinding
import io.cobrowse.sample.ui.CobrowseViewModelFactory
import io.cobrowse.sample.ui.canPopNavigation
import io.cobrowse.sample.ui.collectCobrowseRedactedViews
import io.cobrowse.sample.ui.login.LoginActivity
import io.cobrowse.sample.ui.popNavigation

/**
 * Activity that hosts all fragments when user is logged in.
 */
class MainActivity : AppCompatActivity(), CobrowseIO.Redacted {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var navHostFragmentMain: NavHostFragment
    private lateinit var navHostFragmentNested: NavHostFragment

    private var menu: Menu? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayoutCompat>

    private var backPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (navHostFragmentMain.popNavigation()) {
                return
            }
            if (navHostFragmentNested.popNavigation()) {
                return
            }
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    return
                }
                BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return
                }
                else -> updateBackPressedCallback()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.transactionsBottomSheet)
        navHostFragmentMain = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragmentNested = supportFragmentManager.findFragmentById(R.id.bottom_sheet_nav_host_fragment) as NavHostFragment
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, CobrowseViewModelFactory())
            .get(MainViewModel::class.java)

        if (!viewModel.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setUpMenu()

        viewModel.cobrowseDelegate.current.observe(this@MainActivity, Observer {
            updateUiWithSession(it)
        })

        setUpNavigation(savedInstanceState)
        setUpNestedNavigation(savedInstanceState)
        setUpBottomSheet(savedInstanceState)

        setUpBackPressedCallback()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("bottomSheetBehaviorState", bottomSheetBehavior.state)
    }

    private fun setUpMenu() {
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
    }

    private fun setUpNavigation(savedInstanceState: Bundle?) {
        val navController: NavController = navHostFragmentMain.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { controller,  destination, arguments ->
            run {
                updateBottomSheetState(savedInstanceState)
            }
        }
    }

    private fun setUpNestedNavigation(savedInstanceState: Bundle?) {
        val navController: NavController = navHostFragmentNested.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = binding.toolbarBottomSheet

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { controller,  destination, arguments ->
            run {
                updateBottomSheetState(savedInstanceState)
            }
        }
    }

    private fun setUpBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        navHostFragmentMain.navController.addOnDestinationChangedListener { _, _, _ ->
            run(MainActivity::updateBackPressedCallback)
        }
        navHostFragmentNested.navController.addOnDestinationChangedListener { _,  _, _ ->
            run(MainActivity::updateBackPressedCallback)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                updateBackPressedCallback()
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) { }
        })
    }

    private fun setUpBottomSheet(savedInstanceState: Bundle?) {
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                bottomSheetBehavior.isHideable = newState == BottomSheetBehavior.STATE_HIDDEN
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) { }
        })
        if (savedInstanceState != null) {
            updateBottomSheetState(savedInstanceState)
        } else {
            // Activity might be recreated on configuration changes,
            // and we want to animate the list only on its very first appearance.
            val duration = resources.getInteger(android.R.integer.config_longAnimTime).toLong() * 2
            binding.transactionsBottomSheet.postDelayed(duration) {
                bottomSheetBehavior.halfExpandedRatio = 0.4f
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
    }

    private fun updateBottomSheetState(savedInstanceState: Bundle?) {
        val mainDestinationId: Int? = navHostFragmentMain.navController.currentDestination?.id
        val mainStartDestinationId: Int = navHostFragmentMain.navController.graph.startDestinationId
        val nestedDestinationId: Int? = navHostFragmentNested.navController.currentDestination?.id
        val nestedStartDestinationId: Int = navHostFragmentNested.navController.graph.startDestinationId

        // The nested toolbar has extra margins when the transactions list is shown
        with(binding.toolbarBottomSheet.layoutParams as FrameLayout.LayoutParams) {
            if (nestedDestinationId == nestedStartDestinationId) {
                setMargins(resources.getDimension(R.dimen.list_horizontal_margin).toInt(), 0,
                           resources.getDimension(R.dimen.list_horizontal_margin).toInt(), 0)
            } else {
                setMargins(0, 0, 0, 0)
            }
        }

        if (mainDestinationId != mainStartDestinationId) {
            // No bottom sheet when the main navigation is active
            bottomSheetBehavior.isHideable = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            // AndroidX automatically collapses the bottom sheet on configuration changes.
            // If that's the case, expand the bottom sheet to the last remembered state.
            if (savedInstanceState != null) {
                bottomSheetBehavior.state = savedInstanceState.getInt("bottomSheetBehaviorState",
                                                                      BottomSheetBehavior.STATE_HALF_EXPANDED)
            }
        } else if (nestedDestinationId != nestedStartDestinationId) {
            // Expand the bottom sheet if the nested navigation is active
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else if (bottomSheetBehavior.isHideable) {
            // If no navigation is active, just make sure the bottom sheet is shown
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    private fun updateBackPressedCallback() {
        backPressedCallback.isEnabled =
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
                    || bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED
                    || navHostFragmentNested.canPopNavigation()
                    || navHostFragmentMain.canPopNavigation()
    }

    private fun updateUiWithSession(session: Session?) {
        menu?.findItem(R.id.end_cobrowse_session)?.let {
            it.isVisible = session?.isActive == true
        }
    }

    override fun redactedViews(): MutableList<View> {
        val redacted = navHostFragmentMain.childFragmentManager.collectCobrowseRedactedViews()
        // Also redact views from the bottom sheet navigation
        redacted.addAll(navHostFragmentNested.childFragmentManager.collectCobrowseRedactedViews())
        return redacted
    }
}