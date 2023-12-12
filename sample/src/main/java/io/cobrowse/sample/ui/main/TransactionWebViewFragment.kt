package io.cobrowse.sample.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import io.cobrowse.CobrowseIO
import io.cobrowse.sample.R
import io.cobrowse.sample.data.CobrowseSessionDelegate
import io.cobrowse.sample.data.getAndroidLogTag
import io.cobrowse.sample.databinding.FragmentTransactionWebviewBinding
import io.cobrowse.sample.ui.CobrowseViewModelFactory

/**
 * WebView-based fragment that can display information about certain transaction.
 */
class TransactionWebViewFragment : Fragment(), CobrowseIO.Unredacted {

    @Suppress("PrivatePropertyName")
    private val Any.TAG: String
        get() = javaClass.getAndroidLogTag()

    private lateinit var viewModel: TransactionViewModel
    private lateinit var binding: FragmentTransactionWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, CobrowseViewModelFactory())
            .get(TransactionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionWebviewBinding.inflate(layoutInflater)
        setUpWebView(binding.webView)
        arguments?.getString("url")?.let {
            if (it.isNotEmpty()) {
                binding.webView.loadUrl(it)
            }
        }
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(webView: WebView) {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.let {
                    when (request.url?.scheme) {
                        "tel", "sms", "mailto" -> {
                            invokeViewIntent(request.url)
                            return true
                        }
                        else -> {}
                    }
                    if (request.isForMainFrame) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !request.isRedirect) {
                            invokeNewWebView(request.url)
                            return true
                        }
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
    }

    private fun invokeViewIntent(uri: Uri) {
        try {
            startActivity(Intent.createChooser(Intent().apply {
                action = Intent.ACTION_VIEW
                data = uri
            }, null))
        } catch (e: Exception) {
            Log.e(TAG, "Cannot open a URI of the scheme ${uri.scheme}");
        }
    }

    private fun invokeNewWebView(uri: Uri) {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_transactionWebViewFragment_to_transactionWebViewFragment,
                Bundle().also {
                    it.putString("url", uri.toString())
                })
    }

    override fun unredactedViews(): MutableList<View> {
        return if (CobrowseSessionDelegate.isRedactionByDefaultEnabled(requireContext()))
            mutableListOf(binding.root)
            else mutableListOf()
    }
}