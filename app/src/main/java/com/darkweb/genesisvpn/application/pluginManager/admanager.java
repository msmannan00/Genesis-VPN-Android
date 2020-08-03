package com.darkweb.genesisvpn.application.pluginManager;

import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.androidstudy.networkmanager.Tovuti;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class admanager
{

    /*Private Variables*/
    private AdView m_banner_ads = null;
    private Handler m_failure_handler = new Handler();
    private eventObserver.eventListener m_event;
    private AppCompatActivity m_context;

    /*Local Variables*/
    private boolean m_ads_disable = false;
    private boolean m_advert_initialize = false;

    /*Initializations*/
    public admanager(eventObserver.eventListener p_event){
        m_event = p_event;
    }

    public void initialize(AppCompatActivity p_context,boolean p_ads_disabled, com.google.android.gms.ads.AdView p_banner_ads)
    {
        m_ads_disable = p_ads_disabled;
        m_banner_ads = p_banner_ads;
        m_context = p_context;
        m_banner_ads.setVisibility(View.GONE);
    }

    public void initBannerAds()
    {
        if(!m_advert_initialize){
            m_advert_initialize = true;
            if(!m_ads_disable){
                Tovuti.from(m_context).monitor((connectionType, isConnected, isFast) -> {
                    if(isConnected){
                        List<String> testDeviceIds = Collections.singletonList("E6E822D3FE2DC52EE9947D1E042D20A9");
                        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
                        MobileAds.setRequestConfiguration(configuration);
                    }
                });
                final Runnable r = () -> {
                    MobileAds.initialize(m_context, "ca-app-pub-5074525529134731~1412991199");
                    AdRequest request = new AdRequest.Builder()
                            .build();
                    m_banner_ads.loadAd(request);
                    initializeListener();
                };
                m_failure_handler.postDelayed(r, 1000);
            }
            else {
                m_event.invokeObserver(Arrays.asList(keys.ADS_DISABLED, true), enums.ETYPE.ON_ADVERT_INITIALIZED);
            }
        }
    }


    public void initializeListener(){
        m_event.invokeObserver(Arrays.asList(keys.ADS_DISABLED, true), enums.ETYPE.ON_ADVERT_INITIALIZED);
        m_banner_ads.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                m_banner_ads.setVisibility(View.VISIBLE); // banner visible
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                final Runnable r = () -> {
                    initBannerAds();
                };
                m_failure_handler.postDelayed(r, 5000);
            }

            @Override
            public void onAdOpened() {
                m_banner_ads.setVisibility(View.VISIBLE); // banner visible
            }

            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });

    }

    public boolean isAdDisabled(){
        return m_ads_disable;
    }

    /*Helper Methods*/
    public void adsDisabler(AppCompatActivity p_context)
    {
        if(!m_ads_disable)
        {
            if(!m_ads_disable)
            {
                m_ads_disable = true;
                m_banner_ads.setVisibility(View.GONE);
                m_event.invokeObserver(Arrays.asList(keys.ADS_DISABLED, true), enums.ETYPE.PLUGIN_DISABLE_ADS);

                m_event.invokeObserver(Arrays.asList(strings.AD_ADS_DISABLED_TITLE, strings.AD_ADS_DISABLED), enums.ETYPE.ON_ADVERT_ALERT);
            }
            else
            {
                m_event.invokeObserver(Arrays.asList(strings.AD_ADS_ALREADY_DISABLED_TITLE, strings.AD_ADS_ALREADY_DISABLED), enums.ETYPE.ON_ADVERT_ALERT);
            }
        }else if(m_ads_disable){
            m_event.invokeObserver(Arrays.asList(strings.AD_ADS_ALREADY_DISABLED_TITLE, strings.AD_ADS_ALREADY_DISABLED), enums.ETYPE.ON_ADVERT_ALERT);
        }
    }
}
