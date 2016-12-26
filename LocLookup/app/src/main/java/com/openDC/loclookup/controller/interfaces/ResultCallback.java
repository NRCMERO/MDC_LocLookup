package com.openDC.loclookup.controller.interfaces;

public interface ResultCallback {
    void returnResultToCaller(int responseCode, String result);
}