package com.sethidu.nfcconnect;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView status, count;
    private NfcAdapter adapter;
    private static final String CHANNEL = "https://www.youtube.com/@ViolinSethidu";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        adapter = NfcAdapter.getDefaultAdapter(this);
        buildUi();
        refresh();
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER); return t;
    }
    private void buildUi() {
        int bg = Color.rgb(16,17,22), panel = Color.rgb(29,30,39), white = Color.WHITE, muted = Color.rgb(174,176,192);
        ScrollView scroll = new ScrollView(this); scroll.setBackgroundColor(bg);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(24),dp(34),dp(24),dp(28));
        scroll.addView(root); setContentView(scroll);

        TextView brand = text("S E T H I D U", 13, 0xFFB9A7FF); brand.setTypeface(null, Typeface.BOLD); root.addView(brand);
        TextView title = text("NFC CONNECT", 28, white); title.setTypeface(null, Typeface.BOLD); LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-1,-2); tp.topMargin=dp(12); root.addView(title,tp);
        TextView sub = text("Your digital music creator card", 14, muted); root.addView(sub);

        TextView orb = text("ϟ", 58, 0xFFB9A7FF); orb.setBackgroundColor(panel); LinearLayout.LayoutParams op = new LinearLayout.LayoutParams(dp(116),dp(116)); op.gravity=Gravity.CENTER; op.topMargin=dp(30); op.bottomMargin=dp(24); root.addView(orb,op);
        TextView name = text("VIOLIN SETHIDU", 20, white); name.setTypeface(null,Typeface.BOLD); root.addView(name);
        TextView url = text(CHANNEL, 13, 0xFFB9A7FF); url.setPadding(0,dp(8),0,dp(22)); root.addView(url);

        status = text("Checking NFC…", 15, white); status.setBackgroundColor(panel); status.setPadding(dp(14),dp(18),dp(14),dp(18)); root.addView(status);
        count = text("Successful card reads: 0", 14, muted); LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1,-2); cp.topMargin=dp(16); root.addView(count,cp);

        Button settings = new Button(this); settings.setText("OPEN NFC SETTINGS"); settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NFC_SETTINGS)));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1,-2); bp.topMargin=dp(28); root.addView(settings,bp);
        TextView note = text("Keep NFC enabled and the screen unlocked. This phone emulates an NFC Forum Type 4 Tag containing your YouTube link.", 13, muted); note.setPadding(dp(8),dp(24),dp(8),dp(8)); root.addView(note);
        TextView warning = text("IMPORTANT: A receiving phone may not show a link notification for an HCE card. That depends on its NFC reader and Android version; this app cannot force a notification on another phone.", 12, 0xFFFFC978); warning.setPadding(dp(12),dp(16),dp(12),dp(16)); warning.setBackgroundColor(panel); LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(-1,-2); wp.topMargin=dp(12); root.addView(warning,wp);
    }

    private void refresh() {
        if (adapter == null) status.setText("This phone does not report NFC support.");
        else if (!adapter.isEnabled()) status.setText("NFC is OFF — turn it on to use your card.");
        else if (!getPackageManager().hasSystemFeature("android.hardware.nfc.hce")) status.setText("NFC is available, but HCE card emulation is not supported.");
        else {
            boolean defaultService = false;
            try { defaultService = CardEmulation.getInstance(adapter).isDefaultServiceForCategory(new android.content.ComponentName(this, SethiduHostApduService.class), CardEmulation.CATEGORY_OTHER); } catch (Exception ignored) {}
            status.setText(defaultService ? "READY — NFC card emulation is available." : "NFC is ON — Android will route compatible card requests to Sethidu NFC Connect.");
        }
        count.setText("Successful card reads: " + getSharedPreferences("card", MODE_PRIVATE).getInt("reads",0));
    }
    @Override protected void onResume() { super.onResume(); if (status != null) refresh(); }
}
