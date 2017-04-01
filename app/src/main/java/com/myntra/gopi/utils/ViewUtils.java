package com.myntra.gopi.utils;

import android.view.View;

public class ViewUtils {

    public static void enableVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    public static void setGone(View... views) {
        if (views == null) {
            return;
        }
        for (View view : views) {
            enableVisibility(view, View.GONE);
        }
    }

    public static void setVisibleView(View... views) {
        if (views == null) {
            return;
        }
        for (View view : views) {
            enableVisibility(view, View.VISIBLE);
        }
    }
}