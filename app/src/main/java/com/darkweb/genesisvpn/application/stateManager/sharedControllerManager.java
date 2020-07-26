package com.darkweb.genesisvpn.application.stateManager;

import com.darkweb.genesisvpn.application.homeManager.homeController;
import com.darkweb.genesisvpn.application.promotionManager.promotionController;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.darkweb.genesisvpn.application.serverManager.serverController;

public class sharedControllerManager
{

    /*LOCAL VARIABLE DECLARATION*/

    homeController m_home_controller;
    serverController m_server_controller;
    proxyController m_proxy_controller;
    promotionController m_promotion_controller;

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
    public void setServerController(serverController p_server_controller){
        this.m_server_controller = p_server_controller;
    }
    public serverController getServerController(){
        return this.m_server_controller;
    }

    public void setProxyController(proxyController p_proxy_controller){
        this.m_proxy_controller = p_proxy_controller;
    }
    public proxyController getProxyController(){
        return this.m_proxy_controller;
    }

    public void setPromotionController(promotionController p_promotion_controller){
        this.m_promotion_controller = p_promotion_controller;
    }
    public promotionController getPromotionController(){
        return this.m_promotion_controller;
    }

}
