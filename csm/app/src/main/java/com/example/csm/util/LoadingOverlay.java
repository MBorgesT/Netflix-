package com.example.csm.util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.csm.R;

public class LoadingOverlay {

    private Activity activity;
    private ViewGroup rootLayout;
    private View overlayView;

    public LoadingOverlay(Activity activity) {
        this.activity = activity;
        rootLayout = activity.findViewById(android.R.id.content);
        overlayView = LayoutInflater.from(activity).inflate(R.layout.loading_overlay, rootLayout, false);
    }

    public void showOverlay() {
        if (overlayView.getParent() == null) {
            rootLayout.addView(overlayView);
        }
    }

    public void hideOverlay() {
        if (overlayView.getParent() != null) {
            rootLayout.removeView(overlayView);
        }
    }
}