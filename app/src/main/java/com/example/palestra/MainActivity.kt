package com.example.palestra

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class WebAppInterface(private val context: Context) {
    @JavascriptInterface
    fun loadData(): String? {
        val file = File(context.filesDir, "dati-palestra.json")
        return if (file.exists()) file.readText() else null
    }

    @JavascriptInterface
    fun saveData(json: String): Boolean {
        return try {
            File(context.filesDir, "dati-palestra.json").writeText(json)
            true
        } catch (e: Exception) {
            false
        }
    }

    @JavascriptInterface
    fun exportToDownloads(json: String): String {
        return try {
            val filename = "palestra-data.json"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, filename)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { os ->
                        os.write(json.toByteArray())
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, filename)
                file.writeText(json)
            }
            filename
        } catch (e: Exception) {
            "Errore: ${e.message}"
        }
    }
}

class MainActivity : AppCompatActivity() {
    private var uploadCallback: ValueCallback<Array<Uri>>? = null

    private val openDocLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uploadCallback?.onReceiveValue(if (uri != null) arrayOf(uri) else null)
        uploadCallback = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView = findViewById<WebView>(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = false
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                uploadCallback?.onReceiveValue(null)
                uploadCallback = filePathCallback
                openDocLauncher.launch(arrayOf("*/*"))
                return true
            }
        }
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(WebAppInterface(applicationContext), "Android")
        webView.loadUrl("file:///android_asset/palestra.html")
    }

    override fun onBackPressed() {
        val webView = findViewById<WebView>(R.id.webview)
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
