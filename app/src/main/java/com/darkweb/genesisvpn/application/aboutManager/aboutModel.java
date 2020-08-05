package com.darkweb.genesisvpn.application.aboutManager;

import androidx.fragment.app.FragmentActivity;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;

class aboutModel
{
    /* PRIVATE VARIABLES */

    private FragmentActivity m_context;
    private eventObserver.eventListener m_event;

    /*INITIALIZATION*/

    public aboutModel(FragmentActivity p_context, eventObserver.eventListener p_event){
        this.m_context = p_context;
        m_event = p_event;
    }

    /*HANDLERS*/

    void quit()
    {
        m_event.invokeObserver(null, enums.ETYPE.GENERIC_QUIT);
    }
}
