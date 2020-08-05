package com.darkweb.genesisvpn.application.serverManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;

class serverModel
{
    /* PRIVATE VARIABLES */

    private FragmentActivity m_context;
    private eventObserver.eventListener m_event;

    /*INITIALIZATION*/

    public serverModel(FragmentActivity p_context, eventObserver.eventListener p_event){
        this.m_context = p_context;
        m_event = p_event;
    }

    /*HANDLERS*/

    void quit()
    {
        m_event.invokeObserver(null, enums.ETYPE.GENERIC_QUIT);
    }
}
