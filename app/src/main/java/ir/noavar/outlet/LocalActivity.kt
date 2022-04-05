package ir.noavar.outlet

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity

class LocalActivity : ComponentActivity() {
    lateinit var webView: WebView
    var finishPressed = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local)
        webView = findViewById<View>(R.id.activity_local_webview) as WebView
        //webView.loadUrl("http://192.168.4.1/")
        webView.postUrl(
            "http://192.168.4.1/",
            "local_name=smart_sw12&local_password=12345678&router_name=paradise1&router_password=12345678&setting".toByteArray()
        )

        webView.webViewClient = object : WebViewClient() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                view.loadUrl(request.url.toString())
                return false
            }

            @SuppressLint("NewApi")
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                webView.visibility = View.INVISIBLE
                Toast.makeText(this@LocalActivity, "خطا در اتصال به دستگاه!", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this@LocalActivity, MainActivity::class.java))
                finish()
                finishPressed++
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) webView.goBack() else {
                startActivity(Intent(this@LocalActivity, MainActivity::class.java))
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}