package io.cobrowse.sample.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import io.cobrowse.CobrowseIO
import io.cobrowse.sample.R
import io.cobrowse.sample.databinding.FragmentAccountBinding
import io.cobrowse.sample.ui.CobrowseViewModelFactory
import io.cobrowse.sample.ui.login.LoginActivity

/**
 * Fragment that displays the information about user who is currently logged in, and provides
 * navigation to [AgentPresentFragment].
 */
class AccountFragment : Fragment(), CobrowseIO.Redacted  {

    private lateinit var viewModel: AccountViewModel
    private lateinit var binding: FragmentAccountBinding

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

        viewModel.sessionCodeResult.observe(this@AccountFragment, Observer {
            updateUiWithCobrowseCode(it)
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

        val getSessionCode = binding.getSessionCode
        getSessionCode.setOnClickListener {
            viewModel.requestSessionCode()
        }

        val agentPresent = binding.agentPresentMode
        agentPresent.setOnClickListener {
            val navController = findNavController(binding.root)
            navController.navigate(R.id.action_accountFragment_to_agentPresentFragment)
        }

        return binding.root
    }

    private fun updateUiWithCobrowseCode(code: String) {
        if (code.isNotEmpty()) {
            binding.sessionCode.text = code
            binding.sessionCode.visibility = View.VISIBLE
        } else {
            binding.sessionCode.text = null
            binding.sessionCode.visibility = View.INVISIBLE
        }
    }

    private fun updateUiWithSession(session: io.cobrowse.Session?) {
        if (session?.isActive == true) {
            // Hide the session code label once the session has started
            binding.sessionCode.visibility = View.INVISIBLE
            binding.getSessionCode.visibility = View.INVISIBLE
            binding.agentPresentMode.visibility = View.INVISIBLE
        } else {
            binding.getSessionCode.visibility = View.VISIBLE
            binding.agentPresentMode.visibility = View.VISIBLE
        }
        if (session == null) {
            binding.sessionCode.text = null
        }
    }

    override fun redactedViews(): MutableList<View> {
        return listOf<View>(
            binding.accountEmail,
            binding.accountName)
            .toMutableList()
    }
}