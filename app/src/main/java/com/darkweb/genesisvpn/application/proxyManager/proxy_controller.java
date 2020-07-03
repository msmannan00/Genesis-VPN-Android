package com.darkweb.genesisvpn.application.proxyManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.fireshield.FireshieldCategory;
import com.anchorfree.sdk.fireshield.FireshieldConfig;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.vpnservice.credentials.AppPolicy;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.homeManager.home_model;
import com.darkweb.genesisvpn.application.pluginManager.preference_manager;
import com.darkweb.genesisvpn.application.serverManager.list_model;
import com.darkweb.genesisvpn.application.status.status;
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


public class proxy_controller {

    /*INITIALIZATIONS*/

    private static final proxy_controller ourInstance = new proxy_controller();
    public static proxy_controller getInstance() {
        return ourInstance;
    }

    /*LOCAL VARIABLE DECLARATIONS*/

    private boolean isLoading = false;
    private static final String CHANNEL_ID = "vpn";

    private String server_name = strings.emptySTR;


    /*HELPER METHODS*/

    public void autoStart()
    {
        stateManager();
        stateUIUpdater();

        if(!preference_manager.getInstance().getBool(keys.app_initialized_key,false))
        {
            preference_manager.getInstance().setBool(keys.app_initialized_key,true);
            home_model.getInstance().getHomeInstance().onStartView();
        }
    }

    private void stateUIUpdater()
    {
        UnifiedSDK.addVpnStateListener(new VpnStateListener() {
            @Override
            public void vpnStateChanged(VPNState vpnState) {
                if(vpnState.name().equals("IDLE"))
                {
                    if(status.connection_status != enums.connection_status.connected && status.connection_status != enums.connection_status.reconnecting && status.connection_status != enums.connection_status.restarting)
                    {
                        home_model.getInstance().getHomeInstance().onDisConnected();
                        status.connection_status = enums.connection_status.no_status;
                        isLoading = false;
                    }
                }
                else if(vpnState.name().equals("CONNECTED") && status.connection_status != enums.connection_status.unconnected)
                {
                    home_model.getInstance().getHomeInstance().onConnected();
                    status.connection_status = enums.connection_status.no_status;
                    onUpdateFlag();
                    isLoading = false;
                }
                else if(vpnState.name().equals("PAUSED") || vpnState.name().equals("CONNECTING_VPN") || vpnState.name().equals("CONNECTING_PERMISSIONS"))
                {
                    home_model.getInstance().getHomeInstance().onConnecting();
                    //status.connection_status = enums.connection_status.no_status;
                }
                else if(vpnState.name().equals("DISCONNECTINGN"))
                {
                    home_model.getInstance().getHomeInstance().onStopping();
                    //status.connection_status = enums.connection_status.no_status;
                }
            }
            @Override
            public void vpnError(@NonNull VpnException e) {

            }
        });
    }

    private void stateManager()
    {
        new Thread()
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        sleep(1000);

                        if(status.connection_status == enums.connection_status.no_status || status.app_status == enums.app_status.paused || (isLoading && status.connection_status != enums.connection_status.unconnected)){
                            continue;
                        }

                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }


                    isConnected(new Callback<Boolean>() {

                        @Override
                        public void success(@NonNull Boolean aBoolean) {
                            if(aBoolean){

                                if(status.connection_status == enums.connection_status.restarting)
                                {

                                    List<String> bypassDomains = new LinkedList<>();
                                    bypassDomains.add("*facebook.com");
                                    bypassDomains.add("*wtfismyip.com");

                                    UnifiedSDK.getInstance().getVPN().restart(new SessionConfig.Builder()
                                            .withReason(TrackingConstants.GprReasons.M_UI)
                                            .addDnsRule(TrafficRule.Builder.block().fromAssets(""))
                                            .addDnsRule(TrafficRule.Builder.proxy().fromAssets(""))
                                            .addDnsRule(TrafficRule.Builder.vpn().fromAssets(""))
                                            .addDnsRule(TrafficRule.Builder.vpn().fromDomains(new ArrayList<>()))
                                            .addDnsRule(TrafficRule.Builder.vpn().fromFile(""))
                                            .addDnsRule(TrafficRule.Builder.vpn().fromResource(0))
                                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                                            .exceptApps(new ArrayList<>())
                                            .forApps(new ArrayList<>())
                                            .withVirtualLocation(server_name)
                                            .withPolicy(AppPolicy.newBuilder().build())
                                            .withFireshieldConfig(new FireshieldConfig.Builder()
                                                    .addCategory(FireshieldCategory.Builder.block(""))
                                                    .addCategory(FireshieldCategory.Builder.blockAlertPage(""))
                                                    .addCategory(FireshieldCategory.Builder.bypass(""))
                                                    .addCategory(FireshieldCategory.Builder.custom("", ""))
                                                    .addCategory(FireshieldCategory.Builder.proxy(""))
                                                    .addCategory(FireshieldCategory.Builder.vpn(""))
                                                    .build())
                                            .build(), new CompletableCallback() {
                                        @Override
                                        public void complete() {
                                            if( status.connection_status != enums.connection_status.restarting)
                                            {
                                                status.connection_status = enums.connection_status.no_status;
                                            }
                                            new Thread()
                                            {
                                                public void run()
                                                {
                                                    if(status.connection_status == enums.connection_status.unconnected)
                                                    {
                                                        disconnectConnection();
                                                    }
                                                }
                                            }.start();
                                        }

                                        @Override
                                        public void error(@NonNull VpnException e) {
                                            failureHandler(enums.error_handler.disconnect_fallback);
                                        }
                                    });
                                }

                                if(status.connection_status == enums.connection_status.connected)
                                {
                                    status.connection_status = enums.connection_status.no_status;
                                }
                                else if(status.connection_status == enums.connection_status.unconnected)
                                {
                                    disconnectConnection();
                                }
                            }
                            else
                            {
                                if(status.connection_status == enums.connection_status.connected || status.connection_status == enums.connection_status.restarting || status.connection_status == enums.connection_status.reconnecting)
                                {
                                    isLoading = true;
                                    if(!UnifiedSDK.getInstance().getBackend().isLoggedIn())
                                    {
                                        connect();
                                    }
                                    else
                                    {
                                        List<String> fallbackOrder = new ArrayList<>();
                                        fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                                        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                                        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                                        List<String> bypassDomains = new LinkedList<>();
                                        bypassDomains.add("*facebook.com");
                                        bypassDomains.add("*wtfismyip.com");

                                        UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder()
                                                .withReason(TrackingConstants.GprReasons.M_UI)
                                                .withTransportFallback(fallbackOrder)
                                                .withTransport(HydraTransport.TRANSPORT_ID)
                                                .withVirtualLocation(server_name)
                                                .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                                                .build(), new CompletableCallback() {
                                            @Override
                                            public void complete() {
                                                if(status.connection_status != enums.connection_status.restarting)
                                                {
                                                    status.connection_status = enums.connection_status.no_status;
                                                }
                                                new Thread()
                                                {
                                                    public void run()
                                                    {
                                                        if(status.connection_status == enums.connection_status.unconnected)
                                                        {
                                                            disconnectConnection();
                                                        }
                                                    }
                                                }.start();
                                            }

                                            @Override
                                            public void error(@NonNull VpnException e) {
                                                failureHandler(enums.error_handler.disconnect_fallback);
                                            }
                                        });
                                    }
                                }
                                else if(status.connection_status == enums.connection_status.unconnected)
                                {
                                    if(UnifiedSDK.getInstance().getBackend().isLoggedIn())
                                    {
                                        disconnectConnection();
                                    }
                                }
                            }
                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            failureHandler(enums.error_handler.disconnect_fallback);
                        }
                    });
                }
            }
        }.start();
    }

    public void closeService()
    {
        isLoading = true;
        status.connection_status = enums.connection_status.no_status;
        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
            }

            @Override
            public void error(@NonNull VpnException e) {

            }
        });
    }


    public void disconnectConnection() {

        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                isLoading = false;
                if(status.connection_status != enums.connection_status.restarting)
                {
                    if(status.connection_status != enums.connection_status.reconnecting)
                    {
                        status.connection_status = enums.connection_status.no_status;
                    }
                    else
                    {
                        status.connection_status = enums.connection_status.connected;
                    }
                }
            }

            @Override
            public void error(@NonNull VpnException e) {
                isLoading = false;
                status.connection_status = enums.connection_status.no_status;
            }

        });
    }

    private void failureHandler(enums.error_handler fallback)
    {
        if(enums.error_handler.disconnect_fallback == fallback){

            status.connection_status = enums.connection_status.unconnected;
            status.connection_status = enums.connection_status.no_status;
            isLoading = false;
        }
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
                e.printStackTrace();
            }
        });
    }

    public void chooseServer(Country server)
    {
        server_name = server.getCountry();
        isLoading = true;
        status.connection_status = enums.connection_status.restarting;
        disconnectConnection();
    }

    private void setCurrentFlag()
    {
        home_model.getInstance().getHomeInstance().onSetFlag(server_name);
    }


    private void isConnected(Callback<Boolean> callback) {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                callback.success(vpnState == VPNState.CONNECTED);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                callback.success(false);
            }
        });
    }





















    /*First Time Installations*/

    public void connect() {
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {
                isLoading = false;
                if(status.connection_status != enums.connection_status.unconnected)
                {
                    status.connection_status = enums.connection_status.connected;
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                isLoading = false;
            }
        });
    }


    public void startVPN() {
        initHydraSdk();
        loadServers();
    }

    private void loadServers() {
        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull AvailableCountries availableCountries) {
                list_model.getInstance().setModel(availableCountries.getCountries());
                status.servers_loaded = enums.connection_servers.loaded;
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    private void initHydraSdk() {
        createNotificationChannel();
        /*SharedPreferences prefs = getPrefs();

        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST))
                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, BuildConfig.BASE_CARRIER_ID))
                .build();

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(home_model.getInstance().getHomeInstance().getResources().getString(R.string.app_name))
                .channelId(CHANNEL_ID)
                .build();*/

        UnifiedSDK.setLoggingLevel(Log.VERBOSE);

        UnifiedSDK.getInstance().getVPN().updateConfig(new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .addDnsRule(TrafficRule.Builder.block().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.bypass().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.proxy().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromAssets(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromDomains(new ArrayList<>()))
                .addDnsRule(TrafficRule.Builder.vpn().fromFile(""))
                .addDnsRule(TrafficRule.Builder.vpn().fromResource(0))
                .exceptApps(new ArrayList<>())
                .withTransport("")
                .withSessionId("")
                .forApps(new ArrayList<>())
                .withVirtualLocation("")
                .withPolicy(AppPolicy.newBuilder().build())
                .withFireshieldConfig(new FireshieldConfig.Builder()
                        .addCategory(FireshieldCategory.Builder.block(""))
                        .addCategory(FireshieldCategory.Builder.blockAlertPage(""))
                        .addCategory(FireshieldCategory.Builder.bypass(""))
                        .addCategory(FireshieldCategory.Builder.custom("", ""))
                        .addCategory(FireshieldCategory.Builder.proxy(""))
                        .addCategory(FireshieldCategory.Builder.vpn(""))
                        .build())
                .build(), new CompletableCallback() {
            @Override
            public void complete() {

            }

            @Override
            public void error(VpnException e) {

            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(status.connection_status == enums.connection_status.connected)
            {
                CharSequence name = "Sample VPN";
                String description = "VPN notification";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = home_model.getInstance().getHomeInstance().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}