package com.darkweb.genesisvpn.application.constants;

public class enums {

    public enum SERVER {
        ALL, RECENT
    }

    public enum REQUEST
    {
        UNKNOWN,
        CONNECTED,
        IDLE,
        RECONFIGURING,
        PAUSED,
        CONNECTING_CREDENTIALS,
        CONNECTING_PERMISSIONS,
        CONNECTING_VPN,
        DISCONNECTING,
        CHANGING_SERVER,
        CONNECTING_SERVER,
        ERROR;
    }
    public enum TRIGGER
    {
        TOOGLE,
        CHANGE_SERVER
    }
    public enum REGISTERATION
    {
        UNREGISTERED,
        REGISTERING,
        REGISTERATION_FAILURE,
        REGISTERATION_SUCCESS,
        LOADING_SERVER,
        LOADING_SERVER_FAILURE,
        LOADING_SERVER_SUCCESS,
        INTERNET_ERROR
    }

    public enum ETYPE {
        ABOUT_ADS_REMOVE_CLICK,
        HOME_ALERT,
        GENERIC_QUIT,
        PLUGIN_DISABLE_ADS
    }

    public enum AD_ETYPE {
        INITIALIZE, DISABLE_ADS
    }

    public enum ANALYTIC_ETYPE {
        INITIALIZE
    }

    public enum PREFERENCES_ETYPE {
        INITIALIZE, SET_STRING, GET_STRING, SET_BOOL, GET_BOOL, SET_SET, GET_SET
    }

}


