package ir.noavar.outlet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class LocalActivity extends AppCompatActivity {

    WebView webView;
    int finishPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        webView = (WebView) findViewById(R.id.activity_local_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("http://192.168.4.1/");

        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }

            @SuppressLint("NewApi")
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                webView.setVisibility(View.INVISIBLE);
                Toast.makeText(LocalActivity.this, "خطا در اتصال به دستگاه!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LocalActivity.this, MainActivity.class));
                finish();
                finishPressed++;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack())
                webView.goBack();
            else {
                startActivity(new Intent(LocalActivity.this, MainActivity.class));
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}