package com.darkweb.genesisvpn.application.serverManager;

import com.darkweb.genesisvpn.application.helperManager.helperMethods;

class serverEventHandler
{

    /*INITIALIZATION*/

    private static final serverEventHandler ourInstance = new serverEventHandler();

    static serverEventHandler getInstance() {
        return ourInstance;
    }

    /*HANDLERS*/

    void quit()
    {
        helperMethods.quit(serverModel.getInstance().getServerInstance());
    }
}
