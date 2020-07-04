package com.darkweb.genesisvpn.application.proxyManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.darkweb.genesisvpn.BuildConfig;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.homeManager.homeController;
import com.darkweb.genesisvpn.application.pluginManager.messageManager;
import com.darkweb.genesisvpn.application.pluginManager.preferenceManager;
import com.darkweb.genesisvpn.application.serverManager.listModel;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;
import com.darkweb.genesisvpn.application.statusManager.status;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private static final String CHANNEL_ID = "vpn";
    private String server_name = strings.emptySTR;
    private boolean wasServerChanged = false;
    private homeController m_home_instance;
    private boolean m_is_ergister_user_error_shown = false;

    /*INITIALIZATIONS*/

    private static final proxyController ourInstance = new proxyController();
    public static proxyController getInstance() {
        return ourInstance;
    }
    public proxyController(){
        m_home_instance = sharedControllerManager.getInstance().getHomeController();
    }

    /*HELPER METHODS*/

    public void autoStart()
    {
        UnifiedSDK.addVpnStateListener(this);
        if(!preferenceManager.getInstance().getBool(keys.app_initialized_key,false))
        {
            preferenceManager.getInstance().setBool(keys.app_initialized_key,true);
            m_home_instance.onStart(null);
        }
    }

    public void triggeredManual(){
        wasServerChanged = false;
        onTriggered();
    }

    public void onTriggered(){
        if(status.vpn_status == enums.vpn_status.DISCONNECTING){
            return;
        }
        Log.i("FUCK1: ",status.vpn_status+"");
        switch (status.vpn_status) {
            case IDLE: {
                Log.i("FUCK3: ",status.vpn_status+"");
                onStart();
                m_home_instance.onConnecting();
                break;
            }
            case CONNECTED: {
                status.vpn_status = enums.vpn_status.CONNECTED;
                if(!wasServerChanged){
                    m_home_instance.onStopping();
                }else {
                    m_home_instance.onConnecting();
                }
                onStop();
                break;
            }
            case CONNECTING_VPN:{
                status.vpn_status = enums.vpn_status.DISCONNECTING;
                if(!wasServerChanged){
                    m_home_instance.onStopping();
                }
                onStop();
                break;
            }
            case CONNECTING_CREDENTIALS:{
                status.vpn_status = enums.vpn_status.DISCONNECTING;
                if(!wasServerChanged){
                    m_home_instance.onStopping();
                }
                onStop();
                break;
            }
            case CONNECTING_PERMISSIONS: {
                status.vpn_status = enums.vpn_status.DISCONNECTING;
                if(!wasServerChanged){
                    m_home_instance.onStopping();
                }
                onStop();
                break;
            }
            case PAUSED: {
                onStart();
                break;
            }
            case DISCONNECTING: {
                onRestart();
                break;
            }
        }
    }

    @Override
    public void vpnStateChanged(@NonNull VPNState vpnState) {
        Log.i("FUCK2: ",vpnState.name()+"---"+status.vpn_status);
        switch (vpnState) {
            case DISCONNECTING: {
                status.vpn_status = enums.vpn_status.DISCONNECTING;
                if(!wasServerChanged){
                    m_home_instance.onStopping();
                }
                break;
            }
            case IDLE: {
                status.vpn_status = enums.vpn_status.IDLE;
                if(!wasServerChanged){
                    m_home_instance.onStopped();
                    m_home_instance.onHideFlag();
                }else {
                    wasServerChanged = false;
                    onStart();
                }
                break;
            }
            case CONNECTED: {
                if(status.vpn_status != enums.vpn_status.DISCONNECTING){
                    status.vpn_status = enums.vpn_status.CONNECTED;
                    m_home_instance.onConnected();
                }
                onUpdateFlag();
                break;
            }
            case CONNECTING_VPN:{
                m_home_instance.onConnecting();
                status.vpn_status = enums.vpn_status.CONNECTING_VPN;
                break;
            }
            case CONNECTING_CREDENTIALS:{
                status.vpn_status = enums.vpn_status.CONNECTING_CREDENTIALS;
                break;
            }
            case CONNECTING_PERMISSIONS: {
                status.vpn_status = enums.vpn_status.CONNECTING_PERMISSIONS;
                break;
            }
            case PAUSED: {
                status.vpn_status = enums.vpn_status.PAUSED;
                break;
            }
        }
    }

    @Override
    public void vpnError(@NonNull VpnException e) {
        if(e.getMessage().equals("NetworkRelatedException")){
            messageManager.getInstance().internetError(m_home_instance);
            onStop();
        }
    }

    /* STATE MANAGER */
    public void onStart(){
        List<String> fallbackOrder = new ArrayList<>();
        fallbackOrder.add(HydraTransport.TRANSPORT_ID);
        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
        List<String> bypassDomains = new LinkedList<>();
        bypassDomains.add("*facebook.com");
        bypassDomains.add("*wtfismyip.com");
        unifiedSDK.getVPN().start(new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .withReason(TrackingConstants.GprReasons.M_UI)
                .withTransportFallback(fallbackOrder)
                .withTransport(HydraTransport.TRANSPORT_ID)
                .withVirtualLocation(server_name)
                .build(), new CompletableCallback() {
            @Override
            public void complete() {
            }

            @Override
            public void error(@NonNull VpnException e) {
            }
        });
    }

    public void onRestart(){
        List<String> fallbackOrder = new ArrayList<>();
        fallbackOrder.add(HydraTransport.TRANSPORT_ID);
        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
        List<String> bypassDomains = new LinkedList<>();
        bypassDomains.add("*facebook.com");
        bypassDomains.add("*wtfismyip.com");

        unifiedSDK.getVPN().restart(new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .withTransportFallback(fallbackOrder)
                .withTransport(HydraTransport.TRANSPORT_ID)
                .withVirtualLocation(server_name)
                .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                .build(), new CompletableCallback() {
            @Override
            public void complete() {
            }

            @Override
            public void error(@NonNull VpnException e) {
           }
        });
    }

    public void onStop() {

        unifiedSDK.getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
            }

            @Override
            public void error(@NonNull VpnException e) {
            }
        });
    }

    /* COUNTRY SERVER MANAGER */

    public void chooseServer(Country server)
    {
        wasServerChanged = true;
        server_name = server.getCountry();
        onTriggered();
    }

    /* First Time Installations & Connection */

    public void startVPN() {
        initHydraSdk();
        registerUser();
        autoStart();
    }

    private void initHydraSdk() {
        createNotificationChannel();
        SharedPreferences prefs = getPrefs();

        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST))
                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, BuildConfig.BASE_CARRIER_ID))
                .build();
        unifiedSDK = UnifiedSDK.getInstance(clientInfo);
        UnifiedSDK.setLoggingLevel(Log.VERBOSE);
        UnifiedSDK.setReconnectionEnabled(false);
    }

    public void registerUser() {
        AuthMethod authMethod = AuthMethod.anonymous();
        unifiedSDK.getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {
                loadServers();
            }

            @Override
            public void failure(@NonNull VpnException e) {
                registerUser();
                if(!m_is_ergister_user_error_shown){
                    m_is_ergister_user_error_shown = true;
                    status.servers_loaded = enums.connection_servers.internet_error;
                    vpnError(e);
                }
            }
        });
    }

    private void loadServers() {
        unifiedSDK.getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull AvailableCountries availableCountries) {
                listModel.getInstance().setModel(availableCountries.getCountries());
                status.servers_loaded = enums.connection_servers.loaded;
            }

            @Override
            public void failure(@NonNull VpnException e) {
                loadServers();
            }
        });
    }

    private void setDefaultFlag(String m_flag)
    {
        m_home_instance.onSetFlag(m_flag);
    }

    private void setCurrentFlag()
    {
        m_home_instance.onSetFlag(server_name);
    }

    private void onUpdateFlag()
    {
        Log.i("BREAKER TEXT","BREAKER TEXT");
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                if (vpnState == VPNState.CONNECTED) {
                    UnifiedSDK.getStatus(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            server_name = CredentialsCompat.getServerCountry(sessionInfo.getCredentials());
                            setCurrentFlag();
                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                vpnError(e);
            }
        });
    }

    private SharedPreferences getPrefs() {
        return m_home_instance.getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(status.vpn_status == enums.vpn_status.CONNECTED)
            {
                CharSequence name = "Sample VPN";
                String description = "VPN notification";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = m_home_instance.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}