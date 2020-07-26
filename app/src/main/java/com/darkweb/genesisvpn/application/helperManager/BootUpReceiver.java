package com.darkweb.genesisvpn.application.helperManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;

public class BootUpReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        proxyController.getInstance().onTriggered(enums.TRIGGER.TOOGLE);
    }
}