package com.example.firstpackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private WebView web;
    String weburl = "https://mtmart.in/";
    private ProgressBar pb;
    // private MainActivity ma;

    private SwipeRefreshLayout swipeRefreshLayout;

    RelativeLayout relativeLayout;
    Button Nointernetbtn;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressbar);
        web = (WebView) findViewById(R.id.myweb);
        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pb.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pb.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                internetcheck();
                super.onReceivedError(view, request, error);
            }
        });//opens new page in the app else needed another browser

        web.loadUrl(weburl);


        WebSettings mywebsettings = web.getSettings();
        mywebsettings.setJavaScriptEnabled(true);


        //improve web view performance
        //   web.getSettings().setAllowFileAccess(true);
        web.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//this creates the problem with cache page
        web.getSettings().setAppCacheEnabled(true);
        web.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mywebsettings.setDomStorageEnabled(true);
        mywebsettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mywebsettings.setUseWideViewPort(true);
        mywebsettings.setSavePassword(true);
        mywebsettings.setSaveFormData(true);
        mywebsettings.setEnableSmoothTransition(true);



        //progess bar


        web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);

                if(newProgress < 100 && pb.getVisibility() == ProgressBar.GONE){
                    pb.setVisibility(ProgressBar.VISIBLE);

                }
                if(newProgress == 100){
                    pb.setVisibility(ProgressBar.GONE);
                }
            }
        });
        // pull to refresh
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        web.reload();
                    }
                },2000);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_red_dark)
        );
        //internet connectivity check
        Nointernetbtn = findViewById(R.id.buttonnet);
        relativeLayout = findViewById(R.id.nointernet);
        internetcheck();

        Nointernetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internetcheck();
            }
        });

    }

    public class mywebClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view,url,favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){
            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        if(web.canGoBack()){
            web.goBack();
        }else{
            super.onBackPressed();
        }
    }



    public void internetcheck(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobiledata = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mobiledata.isConnected()){
            web.setVisibility((View.VISIBLE));
            swipeRefreshLayout.setVisibility((View.VISIBLE));
            relativeLayout.setVisibility(View.GONE);
            web.reload();
        }
        else if(wifi.isConnected()){
            web.setVisibility((View.VISIBLE));
            swipeRefreshLayout.setVisibility((View.VISIBLE));
            relativeLayout.setVisibility(View.GONE);
            web.reload();
        }
        else {
            web.setVisibility((View.GONE));
            swipeRefreshLayout.setVisibility((View.GONE));
            relativeLayout.setVisibility(View.VISIBLE);

        }
    }
}