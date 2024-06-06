package com.developerali.lshistutorial.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ActivityWebViewBinding;

public class WebView extends AppCompatActivity {

    ActivityWebViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String link = getIntent().getStringExtra("share");

        binding.webView.setWebViewClient(new WebViewClient());
        WebSettings settings = binding.webView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDatabaseEnabled(false);
        settings.setDomStorageEnabled(false);
        settings.setGeolocationEnabled(false);
        settings.setSaveFormData(false);
        binding.progressBar3.setVisibility(View.VISIBLE);

        binding.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 80){
                    binding.progressBar3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(android.webkit.WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedIcon(android.webkit.WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
        });

        binding.webView.loadUrl(link);

    }
}