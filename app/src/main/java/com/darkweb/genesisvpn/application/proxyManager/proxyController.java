package com.darkweb.genesisvpn.application.proxyManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.HydraTransportConfig;
import com.anchorfree.sdk.TransportConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.transporthydra.HydraResource;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.androidstudy.networkmanager.Tovuti;
import com.darkweb.genesisvpn.BuildConfig;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.enums.*;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.homeManager.homeController;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.serverManager.serverListModel;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.SessionInfo;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.callbacks.VpnStateListener;
import com.anchorfree.vpnsdk.compat.CredentialsCompat;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.northghost.caketube.CaketubeTransport;
import com.northghost.caketube.OpenVpnTransportConfig;

public class proxyController{

    /*LOCAL VARIABLE DECLARATIONS*/

    private static final String CHANNEL_ID = strings.PC_CHANNEL_ID;
    private UnifiedSDK unifiedSDK = null;
    private homeController m_home_instance = null;
    private Context m_context = null;
    private String server_name = strings.EMPTY_STR;
    private NotificationManager notificationManager = null;

    /*HELPER VARIABLE DECLARATIONS*/

    private boolean m_is_complete_triggered = true;
    private boolean m_is_internet_available = true;
    private boolean m_is_alert_shown = false;
    private boolean m_has_app_started = false;
    private boolean m_is_optimal_server = false;
    private boolean m_thread_running = false;
    private boolean m_vpn_initialized = false;

    private int control_thread_counter = 0;
    private int last_exeption_counter = 0;
    private long m_download_speed = 0;
    private long m_upload_speed = 0;
    private long m_download_speed_current = 0;
    private long m_upload_speed_current = 0;

    /*LOCAL STATE VARIALBES*/

    private VPN_UPDATE isSettingsUpdated = VPN_UPDATE.NOT_UPDATED;
    private REQUEST m_ui_status = REQUEST.IDLE;
    private REQUEST m_request = REQUEST.IDLE;
    private REQUEST m_vpn_status = REQUEST.IDLE;
    private REGISTERATION m_RegisterationStatus = REGISTERATION.UNREGISTERED;
    private requestHandler m_request_status = new requestHandler();

    /*INITIALIZATIONS*/

    private static final proxyController ourInstance = new proxyController();
    public static proxyController getInstance() { return ourInstance; }

    public proxyController(){
    }

    public void initialize(){
        onHomeCommands(HOME_COMMANDS.INITIALIZE, null);
        m_context = m_home_instance.getApplicationContext();
    }
    /*HELPER METHODS*/

    public void onStartVPN() {
        if(!m_vpn_initialized){
            m_vpn_initialized = true;
            initialize();
            sharedControllerManager.getInstance().setProxyController(this);

            if(!m_has_app_started){
                m_has_app_started = true;
                initHydraSdk();
                vpnControllerThread();
                InitVpnStateListener();
                initVPNCallListener();
                initTrafficListener();
            }else {
                if(m_vpn_status == REQUEST.CONNECTED){
                    onHomeCommands(HOME_COMMANDS.ON_CONNECTED, null);
                }
                else if(m_vpn_status == REQUEST.CONNECTING_VPN){
                    onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
                }
                else if(m_vpn_status == REQUEST.DISCONNECTING){
                    onHomeCommands(HOME_COMMANDS.ON_DISCONNECTING, null);
                }
                m_ui_status = m_vpn_status;
            }
            serverControlThread();
        }
    }

    boolean m_auto_connect_request = false;
    public void onAutoConnectInitialization(){
        onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
        m_auto_connect_request = true;
        m_vpn_status = REQUEST.CONNECTING_VPN;
        onStop();
        onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
        m_ui_status = REQUEST.CONNECTING_VPN;
        m_request = REQUEST.CONNECTED;
        onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
    }

    public void autoBoot(){
        if(!status.AUTO_CONNECT){
            onHomeCommands(HOME_COMMANDS.AUTO_CONNECT, null);
            m_request = REQUEST.CONNECTED;
        }
    }

    public void initTrafficListener(){
        UnifiedSDK.addTrafficListener((l, l1) -> {
            m_download_speed = l1;
            m_upload_speed = l;
        });

        Handler handler = new Handler();
        new Thread(){
            public void run(){
                try {
                    while (true){
                        sleep(1000);
                        handler.post(() -> {
                            onHomeCommands(HOME_COMMANDS.UPDATE_DOWNLOAD_SPEED, null);
                            onHomeCommands(HOME_COMMANDS.UPDATE_UPLOAD_SPEED, null);
                            m_download_speed_current = m_download_speed;
                            m_upload_speed_current = m_upload_speed;
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void onTriggered(TRIGGER p_trigger_request){

        if(p_trigger_request == TRIGGER.TOOGLE){
            switch (m_ui_status) {
                case IDLE: {
                    m_ui_status = REQUEST.CONNECTING_VPN;
                    m_request = REQUEST.CONNECTED;
                    onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
                    break;
                }
                case CONNECTED: {
                    m_ui_status = REQUEST.DISCONNECTING;
                    onHomeCommands(HOME_COMMANDS.ON_DISCONNECTING, null);
                    m_request = REQUEST.IDLE;
                    break;
                }
                case CONNECTING_VPN:{
                    m_ui_status = REQUEST.DISCONNECTING;
                    onHomeCommands(HOME_COMMANDS.ON_DISCONNECTING, null);
                    m_request = REQUEST.IDLE;
                    break;
                }
                case CONNECTING_CREDENTIALS:{
                    m_ui_status = REQUEST.DISCONNECTING;
                    onHomeCommands(HOME_COMMANDS.ON_DISCONNECTING, null);
                    m_request = REQUEST.IDLE;
                    break;
                }
                case CONNECTING_PERMISSIONS: {
                    if(m_request == REQUEST.IDLE){
                        m_ui_status = REQUEST.CONNECTING_VPN;
                        onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
                        m_request = REQUEST.CONNECTED;
                    }else {
                        m_ui_status = REQUEST.DISCONNECTING;
                        onHomeCommands(HOME_COMMANDS.ON_DISCONNECTING, null);
                        m_request = REQUEST.IDLE;
                    }
                    break;
                }
                case PAUSED: {
                    m_ui_status = REQUEST.CONNECTING_VPN;
                    onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
                    m_request = REQUEST.CONNECTED;
                    break;
                }
                case DISCONNECTING: {
                    m_ui_status = REQUEST.CONNECTING_VPN;
                    onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
                    m_request = REQUEST.CONNECTED;
                    break;
                }
            }
        } else if(p_trigger_request == TRIGGER.CHANGE_SERVER){
            onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
            m_ui_status = REQUEST.CONNECTING_VPN;
            onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
            m_request = REQUEST.CHANGING_SERVER;
        }
    }

    public void onInitDefaultServer(){
        if(server_name.equals(strings.EMPTY_STR)){
            if(status.AUTO_OPTIMAL_LOCATION){
                server_name = UnifiedSDK.COUNTRY_OPTIMAL;
            }else {
                if(status.DEFAULT_SERVER.equals(strings.EMPTY_STR)){
                    server_name = UnifiedSDK.COUNTRY_OPTIMAL;
                }else {
                    server_name = status.DEFAULT_SERVER;
                }
            }
        }
    }

    public void onComplete(){

        if(!m_is_complete_triggered){
            if(m_vpn_status == REQUEST.CONNECTED){
                onHomeCommands(HOME_COMMANDS.ON_ALERT_DISMISS, null);
            }
        }
        switch (m_vpn_status) {
            case IDLE: {
                onHomeCommands(HOME_COMMANDS.ON_IDLE, null);
                if(!m_is_complete_triggered) {
                    onHomeCommands(HOME_COMMANDS.ON_CLEAR_FLAG, null);
                }
                break;
            }
            case CONNECTED: {
                onHomeCommands(HOME_COMMANDS.ON_CONNECTED, null);
                if(!m_is_complete_triggered) {
                    onUpdateFlag();
                }
                break;
            }
            case ERROR: {
                onStop();
                break;
            }
            case UNKNOWN: {
                onStop();
                break;
            }
        }
        m_is_complete_triggered = true;
    }

    public void InitVpnStateListener() {
        UnifiedSDK.addVpnStateListener(new VpnStateListener(){

            @Override
            public void vpnStateChanged(@NonNull VPNState vpnState) {
                switch (vpnState) {
                    case IDLE: {
                        m_vpn_status = REQUEST.IDLE;
                        break;
                    }
                    case CONNECTED: {
                        m_vpn_status = REQUEST.CONNECTED;
                        break;
                    }
                    case CONNECTING_VPN:{
                        m_vpn_status = REQUEST.CONNECTING_VPN;
                        break;
                    }
                    case CONNECTING_CREDENTIALS:{
                        m_vpn_status = REQUEST.CONNECTING_CREDENTIALS;
                        break;
                    }
                    case CONNECTING_PERMISSIONS: {
                        m_vpn_status = REQUEST.CONNECTING_PERMISSIONS;
                        break;
                    }
                    case PAUSED: {
                        m_vpn_status = REQUEST.PAUSED;
                        break;
                    }
                    case DISCONNECTING: {
                        m_vpn_status = REQUEST.DISCONNECTING;
                        break;
                    }
                    case ERROR: {
                        m_vpn_status = REQUEST.ERROR;
                        break;
                    }
                    case UNKNOWN: {
                        m_vpn_status = REQUEST.UNKNOWN;
                        break;
                    }
                }

                m_ui_status = m_vpn_status;
            }

            @Override
            public void vpnError(@NonNull VpnException e) {

            }
        });
    }

    public void onSettingChanged(boolean onFlagClearInstant){

        if(m_vpn_status == REQUEST.CONNECTED && m_request == REQUEST.CONNECTED){
            m_vpn_status = REQUEST.CONNECTING_VPN;
            onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
            m_request = REQUEST.CHANGING_SERVER;
            onStop();
        }else if(m_vpn_status != REQUEST.CONNECTED && m_request == REQUEST.CONNECTED){
            m_vpn_status = REQUEST.CONNECTING_VPN;
            onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
            m_request = REQUEST.CHANGING_SERVER;
            onStop();
        }
        if(onFlagClearInstant) {
            onHomeCommands(HOME_COMMANDS.ON_CLEAR_FLAG_INSTANT, null);
        }else {
            onHomeCommands(HOME_COMMANDS.ON_CLEAR_FLAG, null);
        }
    }

    /* STATE MANAGER */
    public void initVPNCallListener(){
        UnifiedSDK.addVpnCallListener(parcelable -> {
            if (parcelable instanceof HydraResource){
            }
        });
    }

    public void onStart(){
        if(!m_user_registered){
            return;
        }
        if(isSettingsUpdated == VPN_UPDATE.NOT_UPDATED){
            updateVPN();
        }else if(isSettingsUpdated == VPN_UPDATE.UPDATING){
            m_vpn_status = REQUEST.IDLE;
            onStop();
            return;
        }

        if(!m_is_internet_available){
            m_vpn_status = REQUEST.IDLE;
            onStop();
            return;
        }
        control_thread_counter = 0;
        AsyncTask.execute(() -> {
            String m_transport;
            if(status.CONNECTION_TYPE == 1){
            }
            if(status.CONNECTION_TYPE == 2){
                m_transport = HydraTransport.TRANSPORT_ID;
            }
            else {
                m_transport = CaketubeTransport.TRANSPORT_ID_UDP;
            }

            if(server_name.equals(strings.EMPTY_STR)){
                if(status.AUTO_OPTIMAL_LOCATION){
                    server_name = UnifiedSDK.COUNTRY_OPTIMAL;
                    m_is_optimal_server = true;
                }else {
                    if(status.DEFAULT_SERVER.equals(strings.EMPTY_STR)){
                        server_name = UnifiedSDK.COUNTRY_OPTIMAL;
                        m_is_optimal_server = true;
                    }else {
                        server_name = status.DEFAULT_SERVER;
                    }
                }
            }else if(server_name.equals(strings.HO_OPTIMAL_SERVER)){
                server_name = UnifiedSDK.COUNTRY_OPTIMAL;
                m_is_optimal_server = true;
            }

            m_request_status.onConnectRequestStart();
            List<String> fallbackOrder = new ArrayList<>();
            fallbackOrder.add(HydraTransport.TRANSPORT_ID);
            fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
            fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
            unifiedSDK.getVPN().start(new SessionConfig.Builder()
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withTransportFallback(fallbackOrder)
                    .withTransport(m_transport)
                    .exceptApps(status.DISABLED_APPS)
                    .keepVpnOnReconnect(true)
                    .withVirtualLocation(server_name)
                    .build(), new CompletableCallback() {
                @Override
                public void complete() {
                    m_request_status.onConnectRequestComplete();
                }

                @Override
                public void error(@NonNull VpnException e) {
                    m_request_status.onError(helperMethods.createErrorMessage(e));
                    m_request_status.onConnectRequestComplete();
                    m_is_alert_shown = false;
                }
            });
        });
    }

    public void onRestart(){
        if(!m_user_registered){
            return;
        }
        if(isSettingsUpdated == VPN_UPDATE.NOT_UPDATED){
            updateVPN();
        }else if(isSettingsUpdated == VPN_UPDATE.UPDATING){
            onStop();
            m_vpn_status = REQUEST.IDLE;
            return;
        }


        if(!m_is_internet_available){
            onStop();
            m_vpn_status = REQUEST.IDLE;
            return;
        }
        control_thread_counter = 0;
        AsyncTask.execute(() -> {
            String m_transport;
            if(status.CONNECTION_TYPE == 1){
                m_transport = CaketubeTransport.TRANSPORT_ID_TCP;
            }
            else if(status.CONNECTION_TYPE == 2){
                m_transport = HydraTransport.TRANSPORT_ID;
            }
            else {
                m_transport = CaketubeTransport.TRANSPORT_ID_UDP;
            }

            if(server_name.equals(strings.EMPTY_STR)){
                if(status.AUTO_OPTIMAL_LOCATION){
                    server_name = UnifiedSDK.COUNTRY_OPTIMAL;
                    m_is_optimal_server = true;
                }else {
                    if(status.DEFAULT_SERVER.equals(strings.EMPTY_STR)){
                        server_name = UnifiedSDK.COUNTRY_OPTIMAL;
                        m_is_optimal_server = true;
                    }else {
                        server_name = status.DEFAULT_SERVER;
                    }
                }
            }else if(server_name.equals(strings.HO_OPTIMAL_SERVER)){
                server_name = UnifiedSDK.COUNTRY_OPTIMAL;
                m_is_optimal_server = true;
            }

            try {
                m_request_status.onConnectRequestStart();
                List<String> fallbackOrder = new ArrayList<>();
                fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                unifiedSDK.getVPN().restart(new SessionConfig.Builder()
                        .withVirtualLocation(server_name)
                        .withReason(TrackingConstants.GprReasons.M_UI)
                        .withTransportFallback(fallbackOrder)
                        .withTransport(m_transport)
                        .exceptApps(status.DISABLED_APPS)
                        .keepVpnOnReconnect(true)
                        .build(), new CompletableCallback() {
                    @Override
                    public void complete() {
                        m_request_status.onConnectRequestComplete();
                    }

                    @Override
                    public void error(@NonNull VpnException e) {
                        m_request_status.onConnectRequestComplete();
                        m_request_status.onError(helperMethods.createErrorMessage(e));
                        m_is_alert_shown = false;
                    }
                });
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

    public void onCloseSmooth() {
        status.HAS_APPLICATION_STOPPED = true;
        onHomeCommands(HOME_COMMANDS.ON_MOVE_TASK_BACK, null);
        unifiedSDK.getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                if(status.HAS_APPLICATION_STOPPED){
                    onHomeCommands(HOME_COMMANDS.ON_FINISH_TASK, null);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }

            @Override
            public void error(@NonNull VpnException e) {
                if(status.HAS_APPLICATION_STOPPED){
                    onHomeCommands(HOME_COMMANDS.ON_FINISH_TASK, null);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
    }

    public void onForceClose() {
        unifiedSDK.getVPN().stop(null, null);
    }

    public void onStop() {
        AsyncTask.execute(() -> {
            m_request_status.onIdleRequestStart();
            unifiedSDK.getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                @Override
                public void complete() {
                    m_request_status.onIdleRequestComplete();
                }

                @Override
                public void error(@NonNull VpnException e) {
                    m_request_status.onIdleRequestComplete();
                    m_request_status.onError(helperMethods.createErrorMessage(e));
                    m_is_alert_shown = false;
                }
            });
        });
    }

    private boolean m_user_registered = false;
    public void onRegister(){
        m_RegisterationStatus = REGISTERATION.REGISTERING;
        AuthMethod authMethod = AuthMethod.anonymous();
        unifiedSDK.getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {
                if(m_auto_connect_request && m_request == REQUEST.IDLE) {
                    m_request = REQUEST.CHANGING_SERVER;
                }
                m_RegisterationStatus = REGISTERATION.REGISTERATION_SUCCESS;
                m_user_registered = true;
                // m_user_registered = true;
            }

            @Override
            public void failure(@NonNull VpnException e) {
                m_RegisterationStatus = REGISTERATION.REGISTERATION_FAILURE;
            }
        });
    }

    public void onForceStop(){
        m_request = REQUEST.IDLE;
        m_ui_status = REQUEST.CONNECTING_VPN;
    }

    boolean isServerLoaded = false;
    private void onLoadServer() {
        m_RegisterationStatus = REGISTERATION.LOADING_SERVER;
        unifiedSDK.getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull AvailableCountries availableCountries) {
                if(!isServerLoaded){
                    if(m_auto_connect_request && m_request == REQUEST.IDLE) {
                        //m_request = REQUEST.CHANGING_SERVER;
                    }
                    m_user_registered = true;
                    isServerLoaded = true;
                    ArrayList<String> m_recent = (ArrayList<String>) pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.RECENT_COUNTRIES, null), enums.PREFERENCES_ETYPE.GET_SET);
                    serverListModel.getInstance().setCountryModel(availableCountries.getCountries(), m_recent);
                    m_RegisterationStatus = REGISTERATION.LOADING_SERVER_SUCCESS;
                    Tovuti.from(m_context).stop();
                    onInitDefaultServer();
                    m_home_instance.initializeServerModel();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                m_RegisterationStatus = REGISTERATION.LOADING_SERVER_FAILURE;
            }
        });
    }

    public void onUpdateFlagStatus(){
        UnifiedSDK.getStatus(new Callback<SessionInfo>() {
            @Override
            public void success(@NonNull SessionInfo sessionInfo) {
                if(m_vpn_status == REQUEST.CONNECTED){
                    onHomeCommands(HOME_COMMANDS.ON_SET_FLAG_FORCED, CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
    }

    private void onUpdateFlag()
    {
        if(!status.HAS_APPLICATION_STOPPED){
            UnifiedSDK.getStatus(new Callback<SessionInfo>() {
                @Override
                public void success(@NonNull SessionInfo sessionInfo) {
                    server_name = CredentialsCompat.getServerCountry(sessionInfo.getCredentials());
                    onHomeCommands(HOME_COMMANDS.ON_SET_FLAG, null);
                    serverListModel.getInstance().addModelToServer(server_name);

                    ArrayList<String> m_recent_name = new ArrayList<>();
                    for(int counter=0;counter<serverListModel.getInstance().getRecentModel().size();counter++){
                        m_recent_name.add(serverListModel.getInstance().getRecentModel().get(counter).getHeader());
                    }
                    pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.RECENT_COUNTRIES, m_recent_name), enums.PREFERENCES_ETYPE.SET_SET);

                    if(sharedControllerManager.getInstance().getServerController()!=null){
                        sharedControllerManager.getInstance().getServerController().onDataChanged();
                    }
                }

                @Override
                public void failure(@NonNull VpnException e) {
                }
            });
        }
    }

    public void onChooseServer(String p_get_country)
    {
        m_home_instance.onSetFlagInstant();
        m_is_optimal_server = false;
        server_name = p_get_country;
        onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
        onTriggered(TRIGGER.CHANGE_SERVER);
    }
    /* First Time Installations & Connection */

    public void vpnControllerThread() {
        new Thread(){
            public void run(){
                try {
                    while (true){
                        try {
                            /* PRE TASKS */
                            sleep(1000);
                            last_exeption_counter+=1;
                            if(m_request_status.isRequestFailed()){
                                if(!m_is_alert_shown){
                                    m_is_alert_shown = true;
                                    if(m_request_status.getErrorMessage().equals(strings.SE_VPN_PERMISSION) && android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP && m_request != REQUEST.IDLE){
                                        onHomeCommands(HOME_COMMANDS.ON_SHOW_ALERT, strings.SE_VPN_PERMISSION);
                                    }
                                    else if(!m_request_status.getErrorMessage().equals(strings.EMPTY_STR)  && m_request != REQUEST.IDLE && !m_request_status.getErrorMessage().equals(strings.SE_UNKNOWN_EXCEPTION) && !m_request_status.getErrorMessage().equals(strings.SE_PERMISSION_CANCELLED) && !m_request_status.getErrorMessage().equals("User cancelled vpn start") && !m_request_status.getErrorMessage().equals("User cancelled vpn stop") && !m_request_status.getErrorMessage().contains("wrong state")){
                                        onHomeCommands(HOME_COMMANDS.ON_SHOW_ALERT, m_request_status.getErrorMessage());
                                        last_exeption_counter=0;
                                        sleep(5000);
                                        last_exeption_counter=5;
                                    }
                                    m_request_status.clearReportedError();
                                }
                            }


                            if(!m_request_status.isConnectRequestCompleted() && m_request == REQUEST.CONNECTED && m_vpn_status != REQUEST.CONNECTED) {
                                control_thread_counter += 1;
                            }else {
                                control_thread_counter = 0;
                            }

                            if(control_thread_counter > 60 && m_is_internet_available){
                                control_thread_counter = 0;
                                last_exeption_counter=0;
                                if(!m_request_status.isConnectRequestCompleted()  && m_request != REQUEST.IDLE && m_request == REQUEST.CONNECTED && m_vpn_status != REQUEST.CONNECTED && m_request_status.isIdleRequestCompleted()){
                                    onHomeCommands(HOME_COMMANDS.ON_SHOW_ALERT, strings.SE_VPN_POOR_NETWORK);
                                    m_request = REQUEST.RECONFIGURING;
                                    onStop();
                                }
                            }

                            /*STATE HANDLER*/
                            if(m_request == m_vpn_status && (m_vpn_status == REQUEST.CONNECTED || m_vpn_status == REQUEST.IDLE)){
                                onComplete();
                            }else {
                                m_is_complete_triggered = false;
                                if(m_request_status.isConnectRequestCompleted()) {
                                    if(m_request == REQUEST.CONNECTING_SERVER) {
                                        if(m_vpn_status == REQUEST.IDLE){
                                            m_request = REQUEST.CONNECTED;
                                            onStart();
                                        }
                                    }
                                    else if (m_request == REQUEST.CONNECTED) {
                                        if(m_vpn_status != REQUEST.CONNECTED){
                                            onHomeCommands(HOME_COMMANDS.ON_CONNECTING, null);
                                            onStart();
                                        }
                                    }
                                    else if(m_request == REQUEST.CHANGING_SERVER){
                                        if(m_vpn_status == REQUEST.CONNECTED){
                                            m_request = REQUEST.CONNECTED;
                                            onRestart();
                                        }else if(m_vpn_status == REQUEST.IDLE) {
                                            m_request = REQUEST.CONNECTED;
                                            onStart();
                                        }
                                    }
                                }
                                if(m_request_status.isIdleRequestCompleted()){
                                    if(m_request == REQUEST.IDLE){
                                        if(m_vpn_status != REQUEST.IDLE && m_vpn_status != REQUEST.DISCONNECTING){
                                            onStop();
                                        }
                                        else if(m_vpn_status == REQUEST.IDLE){
                                            onHomeCommands(HOME_COMMANDS.ON_IDLE, null);
                                        }
                                    }
                                    else if(m_request == REQUEST.CHANGING_SERVER || m_request == REQUEST.CONNECTING_SERVER){
                                        m_request = REQUEST.CONNECTING_SERVER;
                                        onStop();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    public void clearExceptionCounter(){
        last_exeption_counter = 0;
    }

    public void serverControlThread() {
        Tovuti.from(m_context).monitor((connectionType, isConnected, isFast) -> m_is_internet_available = isConnected);

        if(m_thread_running){
            return;
        }
        m_thread_running = true;

        new Thread(){
            public void run(){
                final int[] m_count = {0};
                try {
                    sleep(1000);
                    onRegister();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true){
                    try {
                        sleep(5000);
                        if(last_exeption_counter >=30 && !m_is_internet_available && m_request != REQUEST.IDLE){
                            last_exeption_counter = 0;
                            onHomeCommands(HOME_COMMANDS.ON_SHOW_ALERT, strings.SE_INTERNET_CONNECTION_ERROR);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(m_is_internet_available || m_RegisterationStatus == REGISTERATION.LOADING_SERVER_SUCCESS && m_count[0] < 10) {
                        if(m_RegisterationStatus != REGISTERATION.LOADING_SERVER_SUCCESS){
                            if (m_RegisterationStatus != REGISTERATION.LOADING_SERVER_SUCCESS) {
                                if (!UnifiedSDK.getInstance().getBackend().isLoggedIn() && m_RegisterationStatus != REGISTERATION.REGISTERING) {
                                    m_RegisterationStatus = REGISTERATION.REGISTERING;
                                    if (m_RegisterationStatus == REGISTERATION.UNREGISTERED || m_RegisterationStatus == REGISTERATION.REGISTERATION_FAILURE || m_RegisterationStatus == REGISTERATION.INTERNET_ERROR) {
                                        m_count[0] += 1;
                                        onRegister();
                                    }
                                } else if (m_RegisterationStatus != REGISTERATION.LOADING_SERVER) {
                                    m_RegisterationStatus = REGISTERATION.REGISTERATION_SUCCESS;
                                    if (m_RegisterationStatus == REGISTERATION.LOADING_SERVER_FAILURE || m_RegisterationStatus == REGISTERATION.REGISTERATION_SUCCESS || m_RegisterationStatus == REGISTERATION.INTERNET_ERROR) {
                                        m_RegisterationStatus = REGISTERATION.LOADING_SERVER;
                                        m_count[0] += 1;
                                        onLoadServer();
                                    }
                                }
                            }
                        }
                        /*REGISTERATION HANDLER*/
                    }
                    else {
                        m_RegisterationStatus = REGISTERATION.INTERNET_ERROR;
                    }
                }
            }
        }.start();
    }

    public REGISTERATION isUserRegistered(){
        if(isServerLoaded){
            return REGISTERATION.LOADING_SERVER_SUCCESS;
        }
        else{
            return m_RegisterationStatus;
        }
    }

    public void initHydraOnBootLoad(){
        onHomeCommands(HOME_COMMANDS.ON_CONNECTED, null);
        m_request = REQUEST.CONNECTING_VPN;
        m_request = REQUEST.CONNECTED;
    }

    private void initHydraSdk() {
        createNotificationChannel();
        SharedPreferences prefs = m_home_instance.getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);

        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST))
                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, BuildConfig.BASE_CARRIER_ID))
                .build();
        unifiedSDK = UnifiedSDK.getInstance(clientInfo);
        updateVPN();
    }

    public void updateVPN(){
        isSettingsUpdated = VPN_UPDATE.UPDATING;
        List<TransportConfig> transports = new ArrayList<>();
        transports.add(HydraTransportConfig.create());
        transports.add(OpenVpnTransportConfig.tcp());
        transports.add(OpenVpnTransportConfig.udp());
        UnifiedSDK.update(transports, new CompletableCallback(){
            @Override
            public void complete() {
                isSettingsUpdated = VPN_UPDATE.UPDATED;
            }

            @Override
            public void error(@NonNull VpnException e) {
                isSettingsUpdated = VPN_UPDATE.NOT_UPDATED;
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(m_vpn_status == REQUEST.CONNECTED)
            {
                CharSequence name = strings.PC_SAMPLE_VPN;
                String description = strings.PC_VPN_NOTIFICATION;
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                notificationManager = m_home_instance.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public String getServerName(){
        if(m_is_optimal_server) {
            return  strings.HO_OPTIMAL_SERVER;
        }
        else {
            return server_name;
        }
    }

    public void onResetServer(){
        server_name = strings.EMPTY_STR;
        if(m_vpn_status != REQUEST.IDLE){
            onSettingChanged(true);
        }
    }

    public void onSetServer(String p_server){
        server_name = p_server;
    }

    public void onHomeCommands(HOME_COMMANDS p_commands, Object p_date){
        if(p_commands == HOME_COMMANDS.INITIALIZE){
            m_home_instance = sharedControllerManager.getInstance().getHomeController();
        }
        else if(m_home_instance != null){
            m_home_instance.runOnUiThread(() -> {
                if(p_commands == HOME_COMMANDS.AUTO_CONNECT){
                    m_home_instance.onAutoConnect();
                }
                else if(p_commands == HOME_COMMANDS.UPDATE_DOWNLOAD_SPEED){
                    m_home_instance.onUpdateDownloadSpeed(((m_download_speed - m_download_speed_current)));
                }
                else if(p_commands == HOME_COMMANDS.UPDATE_UPLOAD_SPEED){
                    m_home_instance.onUpdateUploadSpeed(((m_upload_speed - m_upload_speed_current)));
                }
                else if(p_commands == HOME_COMMANDS.ON_CONNECTED){
                    m_ui_status = REQUEST.CONNECTED;
                    m_home_instance.onConnected();
                }
                else if(p_commands == HOME_COMMANDS.ON_SET_FLAG_FORCED){
                    m_home_instance.onSetFlag(p_date.toString());
                }
                else if(p_commands == HOME_COMMANDS.ON_IDLE){
                    m_ui_status = REQUEST.IDLE;
                    m_home_instance.onIdle();
                }
                else if(p_commands == HOME_COMMANDS.ON_CONNECTING){
                    m_ui_status = REQUEST.CONNECTING_VPN;
                    m_home_instance.onConnecting();
                }
                else if(p_commands == HOME_COMMANDS.ON_DISCONNECTING){
                    m_ui_status = REQUEST.DISCONNECTING;
                    m_home_instance.onDisconnecting();
                }
                else if(p_commands == HOME_COMMANDS.ON_ALERT_DISMISS){
                    m_home_instance.onAlertDismissProxy(null);
                }
                else if(p_commands == HOME_COMMANDS.ON_CLEAR_FLAG){
                    m_home_instance.onSetFlag(strings.EMPTY_STR);
                }
                else if(p_commands == HOME_COMMANDS.ON_CLEAR_FLAG_INSTANT){
                    m_home_instance.onSetFlagInstant();
                }
                else if(p_commands == HOME_COMMANDS.ON_MOVE_TASK_BACK){
                    m_home_instance.moveTaskToBack(true);
                }
                else if(p_commands == HOME_COMMANDS.ON_FINISH_TASK){
                    m_home_instance.finishAndRemoveTask();
                }
                else if(p_commands == HOME_COMMANDS.ON_SET_FLAG){
                    m_home_instance.onSetFlag(server_name);
                }
                else if(p_commands == HOME_COMMANDS.ON_SHOW_ALERT){
                    m_home_instance.onShowAlert(p_date.toString(), false);
                }
            });
        }
    }
}