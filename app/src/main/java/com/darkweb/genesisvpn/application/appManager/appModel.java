package com.darkweb.genesisvpn.application.appManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;

class appModel
{
    /* PRIVATE VARIABLES */

    private AppCompatActivity m_context;
    private eventObserver.eventListener m_event;

    /*INITIALIZATION*/

    public appModel(AppCompatActivity p_context, eventObserver.eventListener p_event){
        this.m_context = p_context;
        m_event = p_event;
    }

    /*HANDLERS*/

    void quit()
    {
        helperMethods.quit(this.m_context);
    }
}
