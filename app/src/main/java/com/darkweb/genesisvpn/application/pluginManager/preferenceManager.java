package com.darkweb.genesisvpn.application.pluginManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.application.helperManager.eventObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class preferenceManager
{
    /*Private Declarations*/

    eventObserver.eventListener m_event;
    private SharedPreferences m_prefs;
    private SharedPreferences.Editor m_edit;

    /*Initializations*/
    public preferenceManager(eventObserver.eventListener p_event){
        m_event = p_event;
    }

    @SuppressLint("CommitPrefEdits")
    public void initialize(Context m_context)
    {
        m_prefs = PreferenceManager.getDefaultSharedPreferences(m_context);
        m_edit = m_prefs.edit();
    }

    /*Saving Preferences*/

    public void setString(String p_valueKey, String p_value)
    {
        m_edit.putString(p_valueKey, p_value);
        m_edit.commit();
    }

    public void setBool(String p_valueKey, boolean p_value)
    {
        m_edit.putBoolean(p_valueKey, p_value);
        m_edit.commit();
    }

    public void setInt(String p_valueKey, int p_value)
    {
        m_edit.putInt(p_valueKey, p_value);
        m_edit.commit();
    }

    public void setSet(String p_valueKey, ArrayList<String> p_value){
        m_edit.putString(p_valueKey, TextUtils.join(",", p_value));
        m_edit.commit();
    }

    /*Recieving Preferences*/

    public String getString(String p_valueKey, String p_valueDefault)
    {
        return m_prefs.getString(p_valueKey, p_valueDefault);
    }

    public boolean getBool(String p_valueKey, boolean p_valueDefault)
    {
        return m_prefs.getBoolean(p_valueKey, p_valueDefault);
    }

    public ArrayList<String> getSet(String p_valueKey, ArrayList<String> p_value){
        return new ArrayList( Arrays.asList( m_prefs.getString(p_valueKey, "").split(",") ) );
    }

    public int getInt(String p_valueKey, int p_valueDefault)
    {
        return m_prefs.getInt(p_valueKey, p_valueDefault);
    }

}
