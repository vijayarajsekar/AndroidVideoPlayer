package com.demo.videoplayer.utils;

import android.os.Build;
import android.view.View;
import android.view.Window;

public class AppUtils {


    public static void hideSystemUI(final Window view) {

        // This work only for android 4.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            view.getDecorView().setSystemUiVisibility(getFlags());
            view.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        view.getDecorView().setSystemUiVisibility(getFlags());
                    }
                }
            });
        }
    }

    public static void onWindowFocusChanged(Window view, boolean hasFocus) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            view.getDecorView().setSystemUiVisibility(getFlags());
        }
    }

    private static int getFlags() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }
}