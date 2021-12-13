package com.hyphenate.easeim.section.base;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hyphenate.easeim.R;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class WebViewActivity extends BaseInitActivity {
    private EaseTitleBar titleBar;
    private ProgressBar progressBar;
    private WebView webview;
    private String url;
    private boolean showTitle;

    public static void actionStart(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String url, boolean showTitle) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("showTitle", showTitle);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        url = intent.getStringExtra("url");
        showTitle = intent.getBooleanExtra("showTitle", true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_base_webview;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        webview = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress_bar);

        if(!showTitle) {
            titleBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                if(webview.canGoBack()) {
                    webview.goBack();
                }else {
                    onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
    }

    @Override
    protected void initData() {
        super.initData();
        if(!TextUtils.isEmpty(url)) {
            webview.loadUrl(url);
        }

        WebSettings settings = webview.getSettings();

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        settings.setLoadsImagesAutomatically(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                titleBar.setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}

