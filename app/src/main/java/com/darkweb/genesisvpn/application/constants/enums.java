package com.darkweb.genesisvpn.application.constants;

public class enums {

    public enum VPN_UPDATE {
        UPDATED, UPDATING, NOT_UPDATED
    }

    public enum SERVER {
        ALL, RECENT
    }

    public enum HOME_COMMANDS {
        ON_CONNECTED, ON_CONNECTING, ON_IDLE, ON_DISCONNECTING, INITIALIZE, AUTO_CONNECT, UPDATE_DOWNLOAD_SPEED, UPDATE_UPLOAD_SPEED, ON_ALERT_DISMISS, ON_CLEAR_FLAG, ON_MOVE_TASK_BACK, ON_FINISH_TASK, ON_SET_FLAG, ON_SHOW_ALERT
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
        ERROR
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
        OPEN_FRAGMENT,
        GENERIC_QUIT,
        PLUGIN_DISABLE_ADS,
        SHOW_ADVERT,
        ON_ADVERT_INITIALIZED,
        ON_ADVERT_ALERT,
        ON_LOAD_LIST,
    }

    public enum AD_ETYPE {
        INITIALIZE, DISABLE_ADS, AD_STATUS, SHOW_ADVERT
    }

    public enum ANALYTIC_ETYPE {
        INITIALIZE
    }

    public enum PREFERENCES_ETYPE {
        INITIALIZE, SET_STRING, GET_STRING, SET_BOOL, GET_BOOL, SET_SET, GET_SET, SET_INT, GET_INT
    }

}


