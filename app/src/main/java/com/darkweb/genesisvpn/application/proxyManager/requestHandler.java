package com.darkweb.genesisvpn.application.proxyManager;

import com.darkweb.genesisvpn.application.constants.strings;

public class requestHandler {
    private boolean is_connect_request_completed = true;
    private boolean is_idle_request_completed = true;
    private boolean is_error_reported = false;
    private String reported_error = strings.EMPTY_STR;

    public requestHandler(){

    }

    public void onConnectRequestStart(){
        is_connect_request_completed = false;
        is_error_reported = false;
    }
    public void onIdleRequestStart(){
        is_idle_request_completed = false;
        is_error_reported = false;
    }

    public void onConnectRequestComplete(){
        is_connect_request_completed = true;
    }
    public void onIdleRequestComplete(){
        is_idle_request_completed = true;
    }

    public void onError(String p_error){
        is_error_reported = true;
        reported_error = p_error;
        is_connect_request_completed = true;
    }


    public boolean isConnectRequestCompleted(){
        return is_connect_request_completed;
    }
    public boolean isIdleRequestCompleted(){
        return is_idle_request_completed;
    }
    public boolean isRequestFailed(){
        return is_error_reported;
    }
    public String getErrorMessage(){
        return reported_error;
    }
    public void clearReportedError(){
        reported_error = strings.EMPTY_STR;
    }
}
