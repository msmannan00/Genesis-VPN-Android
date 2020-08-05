package com.darkweb.genesisvpn.application.pluginManager;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.flurry.android.FlurryAgent;

public class analyticmanager
{
    /*Private Variables*/
    eventObserver.eventListener m_event;

    /*Initializations*/
    public analyticmanager(eventObserver.eventListener p_event){
        m_event = p_event;
    }

    public void initialize(FragmentActivity p_context){
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(p_context, "27Z4TFRQ4B49FRRTPP5W");

    }

}
