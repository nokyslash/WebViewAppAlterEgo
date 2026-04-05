package home.lernestop.webviewappalterego;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final int INTERVAL = 2000;
//    private static final String FIRST_USE = "FirstUse";
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final String START_URL = "https://www.alterego.nat.cu/";

    private ProgressBar progressLoad;
    private SwipeRefreshLayout refreshInd;
    private WebView visor;

    private long timeFirstBack;
    private ValueCallback<Uri[]> mFilePathCallback;

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        checkFirstUse();

        progressLoad = findViewById(R.id.pbLoad);
        refreshInd   = findViewById(R.id.refreshInd);
        visor        = findViewById(R.id.wvVisor);

        visor.getSettings().setJavaScriptEnabled(true);
        visor.getSettings().setDomStorageEnabled(true);
        visor.setWebViewClient(new WebViewClient(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if (request.getUrl().toString().startsWith("https://www.alterego.nat.cu/"))return false;
                    else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                        startActivity(intent);
                        return true;
                    }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https://www.alterego.nat.cu/"))return false;
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }
        });
        visor.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressLoad.setProgress(0);
                progressLoad.setVisibility(View.VISIBLE);
                progressLoad.incrementProgressBy(newProgress);
                if (newProgress == 100){
                    progressLoad.setVisibility(View.GONE);
                    if (refreshInd.isRefreshing())refreshInd.setRefreshing(false);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if(mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }
        });

        visor.loadUrl(START_URL);
        visor.setOnTouchListener( new OnSwipeWebviewTouchListener(getApplicationContext(), new TouchListener() {
            @Override
            public void onSwipeLeft() {
                if (visor.canGoForward()) {
                    Snackbar.make(visor, R.string.navegation_to_forward, BaseTransientBottomBar.LENGTH_SHORT).show();
                    visor.goForward();
                }
            }

            @Override
            public void onSwipeRight() {
                if (visor.canGoBack()) {
                    Snackbar.make(visor, R.string.navegation_to_back, BaseTransientBottomBar.LENGTH_SHORT).show();
                    visor.goBack();
                }
            }
        }));

        refreshInd.setOnRefreshListener(() -> visor.reload());
    }

    @Override
    public void onBackPressed() {
        if (visor.canGoBack())visor.goBack();
        else {
            if (timeFirstBack + INTERVAL > System.currentTimeMillis())super.onBackPressed();
            else Snackbar.make(visor, R.string.message_exit, BaseTransientBottomBar.LENGTH_SHORT).show();
            timeFirstBack = System.currentTimeMillis();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null)
            super.onActivityResult(requestCode, resultCode, data);

        Uri[] results = null;

        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* private void checkFirstUse() {
        SharedPreferences preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if (preferences.getBoolean(FIRST_USE, true)){
            Toast.makeText(getBaseContext(), "es el primer uso, ir a splash", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_USE, false);
            editor.apply();
        }
        else {
            Toast.makeText(getBaseContext(), "No es el primer uso, ir a main", Toast.LENGTH_SHORT).show();
        }
    }*/
}