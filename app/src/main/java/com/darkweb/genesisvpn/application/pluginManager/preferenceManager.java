package com.darkweb.genesisvpn.application.pluginManager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.darkweb.genesisvpn.application.homeManager.homeModel;

public class preferenceManager
{
    /*Private Declarations*/

    private static final preferenceManager ourInstance = new preferenceManager();
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    public static preferenceManager getInstance()
    {
        return ourInstance;
    }

    /*Initializations*/

    private preferenceManager()
    {
    }

    public void initialize()
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(homeModel.getInstance().getHomeInstance());
        edit = prefs.edit();
    }

    /*Saving Preferences*/

    public void setString(String valueKey, String value)
    {
        edit.putString(valueKey, value);
        edit.commit();
    }

    public void setBool(String valueKey, boolean value)
    {
        edit.putBoolean(valueKey, value);
        edit.commit();
    }

    /*Recieving Preferences*/

    public String getString(String valueKey, String valueDefault)
    {
        return prefs.getString(valueKey, valueDefault);
    }

    public boolean getBool(String valueKey, boolean valueDefault)
    {
        return prefs.getBoolean(valueKey, valueDefault);
    }

}