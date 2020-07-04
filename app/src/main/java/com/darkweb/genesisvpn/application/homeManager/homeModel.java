package com.darkweb.genesisvpn.application.homeManager;

public class homeModel {

    /*INSTANCE DECLARATIONS*/

    private static final homeModel ourInstance = new homeModel();
    public static homeModel getInstance() {
        return ourInstance;
    }

    /*INITIALIZATIONS*/

    private homeController homeInstance;

    /*INSTANCE GETTERS SETTERS*/
    public homeController getHomeInstance()
    {
        return homeInstance;
    }
    void setHomeInstance(homeController homeInstance)
    {
        this.homeInstance = homeInstance;
    }

}
