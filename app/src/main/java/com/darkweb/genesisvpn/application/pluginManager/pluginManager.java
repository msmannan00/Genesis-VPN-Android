package com.darkweb.genesisvpn.application.pluginManager;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;

import java.util.ArrayList;
import java.util.List;

public class pluginManager
{
    /*Plugin Insances*/
    admanager m_ad_manager;
    analyticmanager m_analytics_manager;
    preferenceManager m_preference_manager;

    /*Private Variables*/
    private static final pluginManager ourInstance = new pluginManager();

    /*Initializations*/
    public static pluginManager getInstance() {
        return ourInstance;
    }

    private pluginManager() {
        m_ad_manager = new admanager(new adCallback());
        m_analytics_manager = new analyticmanager(new analyticsCallback());
        m_preference_manager = new preferenceManager(new preferenceCallback());
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    /*ADVIEW HANDLER*/
    public Object onAdvertTrigger(List<Object> data, enums.AD_ETYPE p_event){
        if(p_event == enums.AD_ETYPE.INITIALIZE){
            boolean m_ads_status = m_preference_manager.getBool(keys.ADS_DISABLED,false);;
            m_ad_manager.initialize(((FragmentActivity)data.get(0)).getApplicationContext(), m_ads_status, (com.google.android.gms.ads.AdView)data.get(1));
        }
        else if(p_event == enums.AD_ETYPE.DISABLE_ADS){
            m_ad_manager.adsDisabler();
        }
        else if(p_event == enums.AD_ETYPE.AD_STATUS){
            return m_ad_manager.isAdDisabled();
        }
        else if(p_event == enums.AD_ETYPE.SHOW_ADVERT){
            m_ad_manager.initBannerAds();
        }
        return null;
    }

    public class adCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type == enums.ETYPE.PLUGIN_DISABLE_ADS){
                m_preference_manager.setBool((String) p_data.get(0),(boolean) p_data.get(1));
            }
            else if(p_event_type == enums.ETYPE.ON_ADVERT_INITIALIZED){
                sharedControllerManager.getInstance().getHomeController().onStartBeatAnimation();
            }
            else if(p_event_type == enums.ETYPE.ON_ADVERT_ALERT){
                sharedControllerManager.getInstance().getPromotionController().onShowAlert((String) p_data.get(0), (String) p_data.get(1));
            }
        }
    }

    /*ANALYTICS HANDLER*/
    public void onAnalyticsTrigger(List<Object> data, enums.ANALYTIC_ETYPE p_event){
        if(p_event == enums.ANALYTIC_ETYPE.INITIALIZE){
            m_analytics_manager.initialize((FragmentActivity)data.get(0));
        }
    }
    public class analyticsCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }

    /*PREFERENCES HANDLER*/
    public Object onPreferenceTrigger(List<Object> data, enums.PREFERENCES_ETYPE p_event){
        if(p_event == enums.PREFERENCES_ETYPE.INITIALIZE){
            m_preference_manager.initialize((Context)data.get(0));
        }
        else if(p_event == enums.PREFERENCES_ETYPE.SET_BOOL){
            m_preference_manager.setBool((String) data.get(0), (boolean) data.get(1));
        }
        else if(p_event == enums.PREFERENCES_ETYPE.SET_INT){
            m_preference_manager.setInt((String) data.get(0), (int) data.get(1));
        }
        else if(p_event == enums.PREFERENCES_ETYPE.SET_STRING){
            m_preference_manager.setString((String) data.get(0), (String) data.get(1));
        }
        else if(p_event == enums.PREFERENCES_ETYPE.GET_BOOL){
            return m_preference_manager.getBool((String) data.get(0), (boolean) data.get(1));
        }
        else if(p_event == enums.PREFERENCES_ETYPE.GET_INT){
            return m_preference_manager.getInt((String) data.get(0), (int) data.get(1));
        }
        else if(p_event == enums.PREFERENCES_ETYPE.GET_STRING){
            return m_preference_manager.getString((String) data.get(0), (String) data.get(1));
        }
        else if(p_event == enums.PREFERENCES_ETYPE.SET_SET){
             m_preference_manager.setSet((String) data.get(0), (ArrayList<String>) data.get(1));
        }
        else if(p_event == enums.PREFERENCES_ETYPE.GET_SET){
            return m_preference_manager.getSet((String) data.get(0), (ArrayList<String>) data.get(1));
        }

        return null;
    }

    public class preferenceCallback implements eventObserver.eventListener{
        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }
}
