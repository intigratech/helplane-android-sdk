package io.helplane.sdk

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

/**
 * Activity that displays the HelpLane chat widget in a WebView
 */
class HelpLaneChatActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    private var brandToken: String = ""
    private var baseUrl: String = "https://api.helplane.io"
    private var user: HelpLaneUser? = null

    companion object {
        const val EXTRA_BRAND_TOKEN = "io.helplane.sdk.BRAND_TOKEN"
        const val EXTRA_BASE_URL = "io.helplane.sdk.BASE_URL"
        const val EXTRA_USER = "io.helplane.sdk.USER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helplane_chat)

        // Setup toolbar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Support"
        }

        // Get extras
        brandToken = intent.getStringExtra(EXTRA_BRAND_TOKEN) ?: ""
        baseUrl = intent.getStringExtra(EXTRA_BASE_URL) ?: "https://api.helplane.io"
        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_USER, HelpLaneUser::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_USER)
        }

        // Initialize views
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        setupWebView()
        loadWidget()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = false
            allowContentAccess = false
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    progressBar.visibility = View.GONE
                    showError()
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url ?: return false

                // Open external links in browser
                if (url.host != Uri.parse(baseUrl).host) {
                    startActivity(Intent(Intent.ACTION_VIEW, url))
                    return true
                }

                return false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < 100) {
                    progressBar.progress = newProgress
                }
            }
        }
    }

    private fun loadWidget() {
        progressBar.visibility = View.VISIBLE
        webView.visibility = View.INVISIBLE

        val html = generateWidgetHTML()
        webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null)
    }

    private fun generateWidgetHTML(): String {
        val settings = mutableMapOf<String, Any>(
            "brandToken" to brandToken,
            "baseUrl" to baseUrl,
            "embedded" to true,
            "autoOpen" to true,
            "hideLauncher" to true
        )

        user?.let { u ->
            u.userId?.let { settings["userID"] = it }
            u.email?.let { settings["email"] = it }
            u.name?.let { settings["name"] = it }
            u.phone?.let { settings["phone"] = it }
            u.tier?.let { settings["tier"] = it }
            u.meta?.let { settings["meta"] = it }
        }

        val settingsJson = Gson().toJson(settings)

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    html, body {
                        width: 100%;
                        height: 100%;
                        overflow: hidden;
                        background: transparent;
                    }
                    /* Override widget positioning for embedded mode */
                    #helplane-widget-container {
                        position: fixed !important;
                        top: 0 !important;
                        left: 0 !important;
                        right: 0 !important;
                        bottom: 0 !important;
                        width: 100% !important;
                        height: 100% !important;
                    }
                    /* Hide the launcher button in embedded mode */
                    [data-helplane-launcher] {
                        display: none !important;
                    }
                </style>
            </head>
            <body>
                <script>
                    window.HelpLaneSettings = $settingsJson;
                </script>
                <script src="$baseUrl/api/widget/client.js" defer></script>
            </body>
            </html>
        """.trimIndent()
    }

    private fun showError() {
        AlertDialog.Builder(this)
            .setTitle("Connection Error")
            .setMessage("Unable to load chat. Please check your internet connection and try again.")
            .setPositiveButton("Retry") { _, _ ->
                loadWidget()
            }
            .setNegativeButton("Close") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
