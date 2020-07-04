package com.darkweb.genesisvpn.application.pluginManager;

import com.crashlytics.android.Crashlytics;
import com.darkweb.genesisvpn.application.homeManager.homeModel;

import io.fabric.sdk.android.Fabric;

public class fabricManager
{
    /*Private Variables*/

    private static final fabricManager ourInstance = new fabricManager();

    /*Initializations*/

    public static fabricManager getInstance()
    {
        return ourInstance;
    }

    private fabricManager()
    {
    }

    public void init()
    {
         Fabric.with(homeModel.getInstance().getHomeInstance(), new Crashlytics());
         analyticmanager.getInstance().initialize(homeModel.getInstance().getHomeInstance());
         analyticmanager.getInstance().logUser();
    }
}
