package com.example.cobrowsefulldevicedemo

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.cobrowse.CobrowseAccessibilityService
import io.cobrowse.CobrowseIO
import io.cobrowse.Session

class MainActivity : AppCompatActivity(), CobrowseIO.SessionLoadDelegate {

    private lateinit var sessionState: TextView
    private lateinit var remoteControlState: TextView
    private lateinit var accessibilityState: TextView
    private lateinit var sessionCode: TextView
    private lateinit var sessionButton: Button
    private lateinit var accessibilityButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.root)) { root, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            toolbar.setPadding(0, insets.top, 0, 0)
            root.setPadding(insets.left, 0, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        sessionState = findViewById(R.id.session_state)
        remoteControlState = findViewById(R.id.remote_control_state)
        accessibilityState = findViewById(R.id.accessibility_state)
        sessionCode = findViewById(R.id.session_code)
        sessionButton = findViewById(R.id.session_button)
        accessibilityButton = findViewById(R.id.accessibility_button)

        CobrowseIO.instance().setDelegate(this)

        sessionButton.setOnClickListener {
            val session = CobrowseIO.instance().currentSession()
            if (session == null || session.isEnded) {
                CobrowseIO.instance().createSession { err, _ ->
                    if (err != null)
                        Toast.makeText(this, err.message, Toast.LENGTH_LONG).show()
                    updateUi()
                }
            } else {
                session.end { err, _ ->
                    if (err != null)
                        Toast.makeText(this, err.message, Toast.LENGTH_LONG).show()
                    updateUi()
                }
            }
        }

        accessibilityButton.setOnClickListener {
            if (CobrowseAccessibilityService.isEnabled()
                && !CobrowseAccessibilityService.isRunning()) {
                CobrowseAccessibilityService.showSetup(this)
            } else {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh after returning from the system accessibility settings
        updateUi()
    }

    override fun sessionDidLoad(session: Session) = updateUi()

    override fun sessionDidUpdate(session: Session) = updateUi()

    override fun sessionDidEnd(session: Session) = updateUi()

    private fun updateUi() {
        val session = CobrowseIO.instance().currentSession()

        sessionState.text = getString(when {
            session == null -> R.string.session_none
            session.isPending -> R.string.session_pending
            session.isAuthorizing -> R.string.session_authorizing
            session.isActive -> when (session.fullDevice()) {
                Session.FullDeviceState.On -> R.string.session_active_full_device
                Session.FullDeviceState.Requested -> R.string.session_active_full_device_requested
                Session.FullDeviceState.Rejected -> R.string.session_active_full_device_rejected
                else -> R.string.session_active_in_app
            }
            session.isEnded -> R.string.session_ended
            else -> R.string.state_unknown
        })

        remoteControlState.text = getString(when {
            session == null || !session.isActive -> R.string.remote_control_no_session
            else -> when (session.remoteControl()) {
                Session.RemoteControlState.Off -> R.string.remote_control_off
                Session.RemoteControlState.Requested -> R.string.remote_control_requested
                Session.RemoteControlState.Rejected -> R.string.remote_control_rejected
                Session.RemoteControlState.On ->
                    if (session.fullDevice() != Session.FullDeviceState.On)
                        R.string.remote_control_active_in_app
                    else if (CobrowseAccessibilityService.isRunning())
                        R.string.remote_control_active_full_device
                    else
                        R.string.remote_control_full_device_unavailable
            }
        })

        accessibilityState.text = getString(when {
            !CobrowseAccessibilityService.isEnabled() -> R.string.accessibility_disabled_in_app
            !CobrowseAccessibilityService.isRunning() -> R.string.accessibility_disabled_in_system
            else -> R.string.accessibility_enabled
        })

        sessionCode.text = session
            ?.takeIf { !it.isEnded }
            ?.code()
            ?: getString(R.string.session_code_none)

        sessionButton.setText(
            if (session == null || session.isEnded) R.string.button_create_session
            else R.string.button_end_session)
    }
}
