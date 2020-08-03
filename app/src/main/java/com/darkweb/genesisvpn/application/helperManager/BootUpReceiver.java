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

        if (m_is_auto_start_enabled && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("FUCJJJJ","F11");
            proxyController.getInstance().initHydraOnBootLoad(context);
            Log.i("FUCJJJJ","F22");

            //Intent i = new Intent(context, homeController.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //i.putExtra(keys.IS_AUTO_BOOT,true);
            //context.startActivity(i);
        }
    }
}