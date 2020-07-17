package com.darkweb.genesisvpn.application.helperManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;

public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedControllerManager.getInstance().getHomeController().onVPNClose();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        sharedControllerManager.getInstance().getHomeController().onVPNClose();
    }
}