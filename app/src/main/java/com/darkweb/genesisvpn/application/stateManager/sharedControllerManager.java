package com.darkweb.genesisvpn.application.stateManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.homeManager.homeController;
import com.darkweb.genesisvpn.application.serverManager.serverModel;

public class sharedControllerManager
{

    /*LOCAL VARIABLE DECLARATION*/

    homeController m_home_controller;

    /*INITIALIZATIONS*/

    private static final sharedControllerManager ourInstance = new sharedControllerManager();

    public static sharedControllerManager getInstance() {
        return ourInstance;
    }

    /*HANDLERS*/
    public void setHomeController(homeController p_home_controller){
        this.m_home_controller = p_home_controller;
    }
    public homeController getHomeController(){
        return this.m_home_controller;
    }

}
