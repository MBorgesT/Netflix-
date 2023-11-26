package com.example.csm.repository;

import android.content.res.Resources;

import com.example.csm.R;

public abstract class Repository {

    protected static String buildUrl(String controllerPath, String endpoint) {
        return Resources.getSystem().getString(R.string.backend_address) + controllerPath + endpoint;
    }

}
