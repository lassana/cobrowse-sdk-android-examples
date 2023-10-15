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
import io.cobrowse.sample.R
import io.cobrowse.sample.databinding.FragmentMainBinding
import io.cobrowse.sample.ui.CobrowseViewModelFactory
import io.cobrowse.sample.ui.login.LoginActivity

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, CobrowseViewModelFactory())
            .get(MainViewModel::class.java)

        viewModel.logoutResult.observe(this@MainFragment, Observer {
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)

        val logout = binding.logOut
        logout.setOnClickListener {
            viewModel.logOut()
        }

        return binding.root
        //return inflater.inflate(R.layout.fragment_main, container, false)
    }

}