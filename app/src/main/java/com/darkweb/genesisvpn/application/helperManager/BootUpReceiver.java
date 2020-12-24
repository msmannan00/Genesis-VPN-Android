package com.darkweb.genesisvpn.application.helperManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.homeManager.homeController;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;

public class BootUpReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences m_prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean m_is_auto_start_enabled = m_prefs.getBoolean(keys.AUTO_START, false);

        if(m_is_auto_start_enabled){
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Intent i = new Intent(context, homeController.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                proxyController.getInstance().initHydraOnBootLoad();
            }
        }
    }
}