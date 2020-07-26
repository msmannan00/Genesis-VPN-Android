package com.darkweb.genesisvpn.application.settingManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;

class settingModel
{
    /* PRIVATE VARIABLES */

    private AppCompatActivity m_context;
    private eventObserver.eventListener m_event;

    /*INITIALIZATION*/

    public settingModel(AppCompatActivity p_context, eventObserver.eventListener p_event){
        this.m_context = p_context;
        m_event = p_event;
    }

    /*HANDLERS*/

    void quit()
    {
        m_event.invokeObserver(null, enums.ETYPE.GENERIC_QUIT);
    }

}
