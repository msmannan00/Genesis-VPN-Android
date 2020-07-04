package com.darkweb.genesisvpn.application.serverManager;

public class serverModel
{
    /*INSTANCE DECLARATIONS*/

    private serverController serverInstance;

    /*INITIALIZATIONS*/

    private static final serverModel ourInstance = new serverModel();

    public static serverModel getInstance() {
        return ourInstance;
    }

    /*INSTANCE GETTERS SETTERS*/

    serverController getServerInstance(){
        return serverInstance;
    }
    void setServerInstance(serverController serverInstance){
        this.serverInstance = serverInstance;
    }


}
