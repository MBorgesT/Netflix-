package com.example.csm.service;

import com.example.csm.R;

public abstract class Service {

    protected static String buildUrl(String controllerPath, String endpoint) {
        return R.string.backend_address + controllerPath + endpoint;
    }

}
