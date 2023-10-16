package io.cobrowse.sample.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.cobrowse.sample.R
import io.cobrowse.sample.databinding.FragmentAccountBinding
import io.cobrowse.sample.ui.CobrowseViewModelFactory
import io.cobrowse.sample.ui.login.LoginActivity


class AccountFragment : Fragment()  {
    companion object {
        fun newInstance() = AccountFragment()
    }

    private lateinit var viewModel: AccountViewModel
    private lateinit var binding: FragmentAccountBinding
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, CobrowseViewModelFactory())
            .get(AccountViewModel::class.java)

        viewModel.logoutResult.observe(this@AccountFragment, Observer {
            val logoutResult = it ?: return@Observer

            if (logoutResult.error != null) {
                // logout failed
                Toast.makeText(this.context, logoutResult.error, Toast.LENGTH_SHORT).show()
            }
            if (logoutResult.success != null) {
                // logout succeeded
                startActivity(Intent(this.context, LoginActivity::class.java))

                //Complete and destroy the host activity once successful
                this.activity?.finish()
            }
        })

        viewModel.cobrowseDelegate.current.observe(this@AccountFragment, Observer {
            updateUiWithSession(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        binding.accountName.text = viewModel.user?.displayName
        binding.accountEmail.text = viewModel.user?.email

        val logout = binding.logOut
        logout.setOnClickListener {
            viewModel.logOut()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_account, menu)
                this@AccountFragment.menu = menu
                updateUiWithSession(viewModel.cobrowseDelegate.current.value)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.end_cobrowse_session) {
                    viewModel.endCobrowseSession()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun updateUiWithSession(session: io.cobrowse.Session?) {
        menu?.findItem(R.id.end_cobrowse_session).let {
            it?.isVisible = session?.isActive == true
        }
    }
}