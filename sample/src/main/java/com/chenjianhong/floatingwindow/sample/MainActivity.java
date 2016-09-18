package com.chenjianhong.floatingwindow.sample;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chenjianhong.floatingwindow.library.FloatingWindow;

public class MainActivity extends AppCompatActivity {

    private FloatingWindow floatingWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createFloatingWindow();
    }

    private void createFloatingWindow() {
        //检查权限,低于6.0
        int p = PermissionChecker.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW);
        if (p == PermissionChecker.PERMISSION_DENIED) {
            Toast.makeText(this, "缺少悬浮窗权限,请配置权限", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 0);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //大于6.0
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(intent);
                } else {
                    floatingWindow = new FloatingWindow(this);
                }

            } else {
                floatingWindow = new FloatingWindow(this);
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (floatingWindow != null && floatingWindow.isShowing())
            floatingWindow.hide();
    }

    public void showWindow(View view) {
        if (floatingWindow == null)
            createFloatingWindow();
        else {
            if (!floatingWindow.isShowing())
                floatingWindow.show();
        }
    }

    public void hideWindow(View view) {
        if (floatingWindow != null) {
            if (floatingWindow.isShowing()) {
                floatingWindow.hide();
            }
        }
    }

    public void setMyContent(View view) {
        if (floatingWindow != null) {
            TextView textView = new TextView(this);
            textView.setTextSize(28);
            textView.setText("这是新的内容TextView");
            textView.setTextColor(Color.BLUE);
            textView.setBackgroundColor(Color.GRAY);
            floatingWindow.setContentView(textView);
        }

    }
}
