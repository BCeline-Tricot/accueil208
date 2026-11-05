package fr.accueil208;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView w;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        w = new WebView(this);
        setContentView(w);
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        w.setBackgroundColor(0xFF10151C);
        w.addJavascriptInterface(new Bridge(), "Android");
        w.setWebViewClient(new WebViewClient());
        w.setWebChromeClient(new WebChromeClient());
        w.loadUrl("file:///android_asset/index.html");

        BroadcastReceiver r = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                String j = i.getStringExtra("json");
                if (j != null) w.evaluateJavascript("canUpdate(" + j + ")", null);
            }
        };
        IntentFilter f = new IntentFilter("fr.accueil208.CAN");
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(r, f, Context.RECEIVER_EXPORTED);
        else registerReceiver(r, f);
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        if (focus) w.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void onBackPressed() { }

    class Bridge {
        @JavascriptInterface
        public void launch(String list) {
            for (String p : list.split(",")) {
                Intent i = p.trim().equals("settings") ? new Intent(Settings.ACTION_SETTINGS)
                        : getPackageManager().getLaunchIntentForPackage(p.trim());
                if (i != null) { i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i); return; }
            }
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Appli introuvable", Toast.LENGTH_SHORT).show());
        }
    }
}
