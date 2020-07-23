package com.darkweb.genesisvpn.application.pluginManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.application.helperManager.eventObserver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class preferenceManager
{
    /*Private Declarations*/

    eventObserver.eventListener m_event;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    /*Initializations*/
    public preferenceManager(eventObserver.eventListener p_event){
        m_event = p_event;
    }

    @SuppressLint("CommitPrefEdits")
    public void initialize(AppCompatActivity m_context)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(m_context);
        edit = prefs.edit();
    }

    /*Saving Preferences*/

    public void setString(String p_valueKey, String p_value)
    {
        edit.putString(p_valueKey, p_value);
        edit.commit();
    }

    public void setBool(String p_valueKey, boolean p_value)
    {
        edit.putBoolean(p_valueKey, p_value);
        edit.commit();
    }

    /*Recieving Preferences*/

    public String getString(String p_valueKey, String p_valueDefault)
    {
        return prefs.getString(p_valueKey, p_valueDefault);
    }

    public boolean getBool(String p_valueKey, boolean p_valueDefault)
    {
        return prefs.getBoolean(p_valueKey, p_valueDefault);
    }

    public void setSet(String p_valueKey, ArrayList<String> p_value){
        Set<String> set = new HashSet<>(p_value);
        edit.putStringSet(p_valueKey, set);
        edit.commit();
    }

    public ArrayList<String> getSet(String p_valueKey, ArrayList<String> p_value){
        Set<String> m_temo_set = new HashSet<>(new ArrayList<>());
        Set<String> m_response =  prefs.getStringSet(p_valueKey, m_temo_set);
        return new ArrayList<>(m_response);
    }

}
