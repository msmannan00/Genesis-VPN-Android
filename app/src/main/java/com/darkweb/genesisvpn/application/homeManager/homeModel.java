package com.darkweb.genesisvpn.application.homeManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.FrameLayout;
import androidx.fragment.app.FragmentActivity;
import com.darkweb.genesisvpn.application.aboutManager.aboutController;
import com.darkweb.genesisvpn.application.appManager.appController;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.enums.REGISTERATION;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.promotionManager.promotionController;
import com.darkweb.genesisvpn.application.serverManager.serverController;
import com.darkweb.genesisvpn.application.settingManager.settingController;
import java.util.Arrays;
import java.util.Collections;

class homeModel
{
    /* PRIVATE VARIABLES */

    private boolean m_isUIBlocked = false;
    private FragmentActivity m_context;
    private eventObserver.eventListener m_event;

    /*INITIALIZATION*/

    public homeModel(FragmentActivity p_context, eventObserver.eventListener p_event){
        this.m_context = p_context;
        m_event = p_event;
    }

    /*HANDLERS*/

    void onShare()
    {
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            helperMethods.shareApp(m_context);
        }
    }

    void onPrivacyPolicy()
    {
        m_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strings.HO_PRIVACY_URL)));
    }

    void onRateUs(){
        m_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strings.HO_RATE_US_URL)));
    }

    void onContactUS()
    {
        helperMethods.sendEmail(m_context);
    }

    void onAboutUS(FrameLayout p_fragment_conteiner){
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            m_event.invokeObserver(Collections.singletonList(false), enums.ETYPE.START_FRAGMENT_ANIMATION);
            boolean m_response =helperMethods.openFragment(p_fragment_conteiner, new aboutController(), m_context, false);
            m_event.invokeObserver(Collections.singletonList(false), enums.ETYPE.OPEN_FRAGMENT);
        }
    }

    void onSettings(FrameLayout p_fragment_container){
        m_event.invokeObserver(Collections.singletonList(false), enums.ETYPE.START_FRAGMENT_ANIMATION);
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            boolean m_response =  helperMethods.openFragment(p_fragment_container, new settingController(), m_context, false);
            m_event.invokeObserver(Collections.singletonList(m_response), enums.ETYPE.OPEN_FRAGMENT);
        }
    }

    void onPromotion(FrameLayout p_fragment_conteiner){
        m_event.invokeObserver(Collections.singletonList(false), enums.ETYPE.START_FRAGMENT_ANIMATION);
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            boolean m_response = helperMethods.openFragment(p_fragment_conteiner, new promotionController(), m_context, false);
            m_event.invokeObserver(Collections.singletonList(false), enums.ETYPE.OPEN_FRAGMENT);
        }
    }

    void onAppManager(FrameLayout p_fragment_conteiner){
        m_event.invokeObserver(Collections.singletonList(false), enums.ETYPE.START_FRAGMENT_ANIMATION);
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            boolean m_response = helperMethods.openFragment(p_fragment_conteiner, new appController(), m_context, false);
            m_event.invokeObserver(Collections.singletonList(m_response), enums.ETYPE.OPEN_FRAGMENT);
        }
    }

    void onLocation(String mServerName){
        if(!m_isUIBlocked)
        {
            if(!mServerName.equals(strings.EMPTY_STR)){
                m_isUIBlocked = true;
                new Handler().postDelayed(() -> helperMethods.onOpenURL(m_context, strings.HO_IP_LOCATION + mServerName), 0);
            }else {
                m_event.invokeObserver(Arrays.asList(strings.SE_LOCATION_FAILURE_INFO, strings.SE_LOCATION_FAILURE, (long)700), enums.ETYPE.HOME_ALERT);
            }
        }
    }

    void onSpeedTest(){
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            new Handler().postDelayed(() -> helperMethods.onOpenURL(m_context, strings.HO_SPEED_TEST), 0);
        }
    }

    void onServer(long p_delay, REGISTERATION p_registeration_status, FrameLayout p_fragment_conteiner){
        if(!m_isUIBlocked)
        {
            if(p_registeration_status == REGISTERATION.LOADING_SERVER_SUCCESS){
                m_isUIBlocked = true;
                m_event.invokeObserver(Collections.singletonList(false), enums.ETYPE.START_FRAGMENT_ANIMATION);
                boolean m_response = helperMethods.openFragmentWithBundle(p_fragment_conteiner, new serverController(), m_context, keys.REQUEST_TYPE, false);
                m_event.invokeObserver(Collections.singletonList(m_response), enums.ETYPE.OPEN_FRAGMENT);
                new Handler().postDelayed(() -> {
                }, 0);
            }
            else if(p_registeration_status == REGISTERATION.LOADING_SERVER_FAILURE){
                m_event.invokeObserver(Arrays.asList(strings.SE_SERVER_FAILURE, strings.SE_REQUEST_FAILURE, p_delay), enums.ETYPE.HOME_ALERT);
            }
            else if(p_registeration_status == REGISTERATION.INTERNET_ERROR){
                m_event.invokeObserver(Arrays.asList(strings.SE_INTERNET_CONNECTION_ERROR, strings.SE_REQUEST_FAILURE, p_delay), enums.ETYPE.HOME_ALERT);
            }
            else {
                m_event.invokeObserver(Arrays.asList(strings.SE_SERVER_MESSAGE_DESC, strings.SE_REQUEST_INITIALIZING, p_delay), enums.ETYPE.HOME_ALERT);
            }
        }
    }

    public void onResetUIBlock()
    {
        m_isUIBlocked = false;
    }
}
