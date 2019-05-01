package com.ar4i.browser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    //==========================================start ui============================================
    WebView webView;
    ProgressBar pbProgress;
    SearchView searchView;
    //-------------------------------------------end ui---------------------------------------------


    //==========================================start fields========================================
    private static final int MAX_PROGRESS_VALUE = 100;
    private static final int MIN_PROGRESS_VALUE = 0;
    //-------------------------------------------end fields-----------------------------------------


    //==========================================start lifecycle=====================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }
        initWebViewSettings();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }
    //-------------------------------------------end lifecycle--------------------------------------


    //==========================================start extends AppCompatActivity=====================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem actionSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) actionSearch.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        searchView.setQueryHint(getString(R.string.hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    loadUrl(getString(R.string.shema) + query);
                    clearSearchView();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                return true;
            case R.id.action_forward:
                if (webView.canGoForward()) {
                    webView.goForward();
                }
                return true;
            case R.id.action_home:
                loadUrl(getString(R.string.home_domain));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }
    //-------------------------------------------end extends AppCompatActivity----------------------


    //==========================================start private methods===============================
    private void initView() {
        webView = findViewById(R.id.v_web);
        pbProgress = findViewById(R.id.pb_progress);
        pbProgress.setMax(MAX_PROGRESS_VALUE);
    }

    private void initWebViewSettings() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        WebBackForwardList webBackForwardList = webView.copyBackForwardList();
        if (webBackForwardList != null && webBackForwardList.getSize() > 0) {
            loadUrl(webBackForwardList.getItemAtIndex(webBackForwardList.getSize() - 1).getUrl());
        } else {
            loadUrl(getString(R.string.home_domain));
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                webView.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pbProgress.setProgress(newProgress);
                if (newProgress >= MAX_PROGRESS_VALUE) {
                    pbProgress.setProgress(MIN_PROGRESS_VALUE);
                }
            }
        });
    }

    private void loadUrl(String url) {
        webView.loadUrl(url);
    }

    public void clearSearchView() {
        searchView.setQuery(getString(R.string.empty), false);
        searchView.setIconified(true);
    }
    //-------------------------------------------end private methods--------------------------------
}
