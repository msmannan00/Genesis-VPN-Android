package com.darkweb.genesisvpn.application.proxyManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.response.AvailableCountries;
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
import com.anchorfree.partner.api.data.Country;
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

public class proxyController implements  VpnStateListener{

    /*LOCAL VARIABLE DECLARATIONS*/

    private UnifiedSDK unifiedSDK = null;
    private static final String CHANNEL_ID = strings.PC_CHANNEL_ID;
    private homeController m_home_instance;
    private String server_name = strings.EMPTY_STR;
    NotificationManager notificationManager = null;

    /*HELPER VARIABLE DECLARATIONS*/

    private boolean m_is_complete_triggered = true;
    private boolean m_is_internet_available = true;
    private boolean m_is_alert_shown = false;
    private int control_thread_counter = 0;
    private int last_exeption_counter = 0;
    private long m_download_speed = 0;
    private long m_upload_speed = 0;
    private long m_download_speed_current = 0;
    private long m_upload_speed_current = 0;
    private boolean m_is_complete = false;

    /*LOCAL STATE VARIALBES*/

    REQUEST m_ui_status = REQUEST.IDLE;
    REQUEST m_request = REQUEST.IDLE;
    REQUEST m_vpn_status = REQUEST.IDLE;
    REGISTERATION m_RegisterationStatus = REGISTERATION.UNREGISTERED;
    requestHandler m_request_status = new requestHandler();

    /*INITIALIZATIONS*/

    private static final proxyController ourInstance = new proxyController();
    public static proxyController getInstance() { return ourInstance; }

    public proxyController(){
        m_home_instance = sharedControllerManager.getInstance().getHomeController();
    }

    /*HELPER METHODS*/

    public void onStartVPN() {
        initHydraSdk();
        vpnControllerThread();
        serverControlThread();
        initVPNCallListener();
        initTrafficListener();
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
                            m_home_instance.onUpdateDownloadSpeed(((m_download_speed - m_download_speed_current)));
                            m_home_instance.onUpdateUploadSpeed(((m_upload_speed - m_upload_speed_current)));
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
                    m_home_instance.onConnecting();
                    break;
                }
                case CONNECTED: {
                    m_ui_status = REQUEST.DISCONNECTING;
                    m_home_instance.onDisconnecting();
                    m_request = REQUEST.IDLE;
                    break;
                }
                case CONNECTING_VPN:{
                    m_ui_status = REQUEST.DISCONNECTING;
                    m_home_instance.onDisconnecting();
                    m_request = REQUEST.IDLE;
                    break;
                }
                case CONNECTING_CREDENTIALS:{
                    m_ui_status = REQUEST.DISCONNECTING;
                    m_home_instance.onDisconnecting();
                    m_request = REQUEST.IDLE;
                    break;
                }
                case CONNECTING_PERMISSIONS: {
                    m_ui_status = REQUEST.DISCONNECTING;
                    m_home_instance.onDisconnecting();
                    m_request = REQUEST.IDLE;
                    break;
                }
                case PAUSED: {
                    m_ui_status = REQUEST.CONNECTING_VPN;
                    m_home_instance.onConnecting();
                    m_request = REQUEST.CONNECTED;
                    break;
                }
                case DISCONNECTING: {
                    m_ui_status = REQUEST.CONNECTING_VPN;
                    m_home_instance.onConnecting();
                    m_request = REQUEST.CONNECTED;
                    break;
                }
                case ERROR: {
                    m_ui_status = REQUEST.DISCONNECTING;
                    m_home_instance.onDisconnecting();
                    m_request = REQUEST.IDLE;
                    break;
                }
                case UNKNOWN: {
                    m_ui_status = REQUEST.DISCONNECTING;
                    m_home_instance.onDisconnecting();
                    m_request = REQUEST.IDLE;
                    break;
                }
            }
        } else if(p_trigger_request == TRIGGER.CHANGE_SERVER){
            m_ui_status = REQUEST.CONNECTING_VPN;
            m_home_instance.onConnecting();
            m_request = REQUEST.CHANGING_SERVER;
        }
    }

    public void onComplete(){
        if(!m_is_complete_triggered){
            m_is_complete_triggered = true;
            if(m_vpn_status == REQUEST.CONNECTED){
                m_home_instance.onAlertDismiss(null);
            }
            switch (m_vpn_status) {
                case IDLE: {
                    m_home_instance.onIdle();
                    m_home_instance.onSetFlag(strings.EMPTY_STR);
                    break;
                }
                case CONNECTED: {
                    m_home_instance.onConnected();
                    onUpdateFlag();
                    break;
                }
            }
        }
    }

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
                if(m_vpn_status == REQUEST.CONNECTED){
                    m_request = REQUEST.IDLE;
                    m_ui_status = REQUEST.DISCONNECTING;
                    m_home_instance.onDisconnecting();
                }
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
    }

    @Override
    public void vpnError(@NonNull VpnException e) {

    }

    public void onAppsFiltered(){
        if(m_vpn_status == REQUEST.CONNECTED && m_request == REQUEST.CONNECTED){
            onRestart();
        }else if(m_vpn_status != REQUEST.CONNECTED && m_request == REQUEST.CONNECTED){
            onStop();
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
        if(!m_is_internet_available){
             return;
        }
        control_thread_counter = 0;
        AsyncTask.execute(() -> {
            m_request_status.onConnectRequestStart();
            List<String> fallbackOrder = new ArrayList<>();
            fallbackOrder.add(HydraTransport.TRANSPORT_ID);
            fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
            fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
            unifiedSDK.getVPN().start(new SessionConfig.Builder()
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withTransportFallback(fallbackOrder)
                    .withTransport(HydraTransport.TRANSPORT_ID)
                    .exceptApps(status.DISABLED_APPS)
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
        if(!m_is_internet_available){
             return;
        }
        control_thread_counter = 0;
        AsyncTask.execute(() -> {
            m_request_status.onConnectRequestStart();
            List<String> fallbackOrder = new ArrayList<>();
            fallbackOrder.add(HydraTransport.TRANSPORT_ID);
            fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
            fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
            unifiedSDK.getVPN().restart(new SessionConfig.Builder()
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withTransportFallback(fallbackOrder)
                    .withTransport(HydraTransport.TRANSPORT_ID)
                    .exceptApps(status.DISABLED_APPS)
                    .withVirtualLocation(server_name)
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
        });
    }

    public void onCloseSmooth() {
        status.HAS_APPLICATION_STOPPED = true;
        m_home_instance.moveTaskToBack(true);
        unifiedSDK.getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                if(status.HAS_APPLICATION_STOPPED){
                    m_home_instance.finishAndRemoveTask();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }

            @Override
            public void error(@NonNull VpnException e) {
                if(status.HAS_APPLICATION_STOPPED){
                    m_home_instance.finishAndRemoveTask();
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

    public void onRegister(){
        m_RegisterationStatus = REGISTERATION.REGISTERING;
        AuthMethod authMethod = AuthMethod.anonymous();
        unifiedSDK.getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {
                m_RegisterationStatus = REGISTERATION.REGISTERATION_SUCCESS;
            }

            @Override
            public void failure(@NonNull VpnException e) {
                m_RegisterationStatus = REGISTERATION.REGISTERATION_FAILURE;
            }
        });
    }

    public void onForceStop(){
        if(m_ui_status != REQUEST.IDLE && m_ui_status != REQUEST.DISCONNECTING && m_ui_status != REQUEST.PAUSED)
        {
            m_ui_status = REQUEST.DISCONNECTING;
            m_home_instance.onDisconnecting();
            m_request = REQUEST.IDLE;
            onStop();
        }
    }

    private void onLoadServer() {
        m_RegisterationStatus = REGISTERATION.LOADING_SERVER;
        unifiedSDK.getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull AvailableCountries availableCountries) {
                ArrayList<String> m_recent = (ArrayList<String>) pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.RECENT_COUNTRIES, null), enums.PREFERENCES_ETYPE.GET_SET);
                serverListModel.getInstance().setCountryModel(availableCountries.getCountries(), m_recent);
                m_RegisterationStatus = REGISTERATION.LOADING_SERVER_SUCCESS;
                Tovuti.from(m_home_instance).stop();
            }

            @Override
            public void failure(@NonNull VpnException e) {
                m_RegisterationStatus = REGISTERATION.LOADING_SERVER_FAILURE;
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
                    m_home_instance.onSetFlag(server_name);
                }

                @Override
                public void failure(@NonNull VpnException e) {
                }
            });
        }
    }

    public void onChooseServer(Country server)
    {
        server_name = server.getCountry();
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
                                    if(m_request_status.getErrorMessage().equals(strings.SE_VPN_PERMISSION) && android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){
                                        m_home_instance.onShowAlert(strings.SE_VPN_PERMISSION, false);
                                    }
                                    else if(!m_request_status.getErrorMessage().equals(strings.SE_UNKNOWN_EXCEPTION) && !m_request_status.getErrorMessage().equals(strings.SE_PERMISSION_CANCELLED) && !m_request_status.getErrorMessage().equals("User cancelled vpn start") && !m_request_status.getErrorMessage().equals("User cancelled vpn stop") && !m_request_status.getErrorMessage().contains("wrong state")){
                                        m_home_instance.onShowAlert(m_request_status.getErrorMessage(), false);
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
                                if(!m_request_status.isConnectRequestCompleted() && m_request == REQUEST.CONNECTED && m_vpn_status != REQUEST.CONNECTED && m_request_status.isIdleRequestCompleted()){
                                    m_home_instance.onShowAlert(strings.SE_VPN_POOR_NETWORK, false);
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
                                            m_home_instance.onConnecting();
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
        Tovuti.from(m_home_instance).monitor((connectionType, isConnected, isFast) -> m_is_internet_available = isConnected);

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
                        if(last_exeption_counter >=30 && !m_is_internet_available){
                            last_exeption_counter = 0;
                            m_home_instance.onShowAlert(strings.SE_INTERNET_CONNECTION_ERROR, false);
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
        return m_RegisterationStatus;
    }

    private void initHydraSdk() {
        createNotificationChannel();
        SharedPreferences prefs = m_home_instance.getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);

        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST))
                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, BuildConfig.BASE_CARRIER_ID))
                .build();
        unifiedSDK = UnifiedSDK.getInstance(clientInfo);
        UnifiedSDK.setLoggingLevel(Log.VERBOSE);
        UnifiedSDK.setReconnectionEnabled(false);
        UnifiedSDK.addVpnStateListener(this);
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
}