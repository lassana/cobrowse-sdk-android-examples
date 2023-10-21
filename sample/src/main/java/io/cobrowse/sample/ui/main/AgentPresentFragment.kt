package io.cobrowse.sample.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import io.cobrowse.Session
import io.cobrowse.sample.R
import io.cobrowse.sample.databinding.FragmentAgentPresentBinding
import io.cobrowse.sample.ui.CobrowseViewModelFactory
import io.cobrowse.sample.ui.login.afterTextChanged

/**
 * Fragment that implements Cobrowse.io Agent Present mode: a special mode when an agent
 * can use this app to share their screen within a "reverse" screensharing session.
 */
class AgentPresentFragment : Fragment()  {

    private lateinit var viewModel: AgentPresentViewModel
    private lateinit var binding: FragmentAgentPresentBinding
    private var inputs: Array<EditText?> = arrayOfNulls(6)

    private val inputsLayout: LinearLayout
        get() = binding.agentPresentCodeLayout

    private val mainLabel: TextView
        get() = binding.logoPresentationDescription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, CobrowseViewModelFactory())
            .get(AgentPresentViewModel::class.java)

        viewModel.cobrowseDelegate.current.observe(this@AgentPresentFragment, Observer {
            updateUiWithSession(it)
        })

        viewModel.agentSessionResult.observe(this@AgentPresentFragment, Observer {
            updateUiWithAgentSession(it.first, it.second)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgentPresentBinding.inflate(layoutInflater)

        val layout = binding.root.findViewById<LinearLayout>(R.id.agent_present_code_layout)
        for (i in 0 ..< layout.childCount) {
            inputs[i] = (layout.getChildAt(i) as TextInputLayout).editText
            inputs[i]?.afterTextChanged {
                if (it.isEmpty()) {
                    if (i > 0) {
                        (layout.getChildAt(i - 1) as TextInputLayout).editText?.requestFocus()
                    }
                } else if (i == 5) {
                    initiateSession()
                } else {
                    (layout.getChildAt(i + 1) as TextInputLayout).editText?.requestFocus()
                }
            }
        }

        return binding.root
    }

    private fun updateUiWithSession(session: Session?) {
        val isActive = session?.isActive == true
        inputsLayout.visibility = if (isActive) View.GONE else View.VISIBLE

        if (session == null || isActive) {
            resetInputs()
        }

        if (session == null || !isActive) {
            inputs[0]?.requestFocus()
        }

        mainLabel.text = if (isActive) getString(R.string.agent_present_welcome_you_are_presenting)
                         else getString(R.string.agent_present_welcome)
    }

    private fun updateUiWithAgentSession(error: Error?, session: Session?) {
        if (error == null) {
            // Presenting mode has been activated successfully
            // TODO does the line below disable the full device mode?
            session?.setCapabilities(arrayOfNulls(0), null)
            return
        }

        val shake = AnimationUtils.loadAnimation(this.context, R.anim.shake)
        shake.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) { }
            override fun onAnimationRepeat(animation: Animation?) { }
            override fun onAnimationEnd(animation: Animation?) {
                binding.root.post {
                    resetInputs()
                }
            }
        })
        inputsLayout.startAnimation(shake)
    }

    private fun initiateSession() {
        val code = arrayOfNulls<String>(6)
        for (i in inputs.indices) {
            val typed = inputs[i]?.text
            if (typed.isNullOrEmpty()) {
                return
            }
            code[i] = typed[0].toString()
        }

        inputs.forEach { it?.isEnabled = false }
        viewModel.initiateSession(code.joinToString(""))
    }

    private fun resetInputs() {
        inputs.forEach {
            it?.text = null
            it?.isEnabled = true
        }
        inputs[0]?.requestFocus()
    }
}