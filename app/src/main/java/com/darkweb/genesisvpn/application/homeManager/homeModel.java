package com.darkweb.genesisvpn.application.homeManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.application.aboutManager.aboutController;
import com.darkweb.genesisvpn.application.appManager.appController;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.enums.REGISTERATION;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.promotionManager.promotionController;
import com.darkweb.genesisvpn.application.serverManager.serverController;
import com.darkweb.genesisvpn.application.settingManager.settingController;

import java.util.Arrays;

class homeModel
{
    /* PRIVATE VARIABLES */

    private boolean m_isUIBlocked = false;
    private AppCompatActivity m_context;
    private eventObserver.eventListener m_event;

    /*INITIALIZATION*/

    public homeModel(AppCompatActivity p_context, eventObserver.eventListener p_event){
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

    void onQuit(){
        status.HAS_APPLICATION_STOPPED = true;
    }

    void onContactUS()
    {
        helperMethods.sendEmail(m_context);
    }

    void onAboutUS(){
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            new Handler().postDelayed(() -> helperMethods.openActivity(aboutController.class, m_context), 400);
        }
    }

    void onSettings(int m_delay){
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            new Handler().postDelayed(() -> helperMethods.openActivity(settingController.class, m_context), m_delay);
        }
    }

    void onPromotion(int m_delay){
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            new Handler().postDelayed(() -> helperMethods.openActivity(promotionController.class, m_context), m_delay);
        }
    }

    void onAppManager(int m_delay){
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            new Handler().postDelayed(() -> helperMethods.openActivity(appController.class, m_context), m_delay);
        }
    }

    void onLocation(int m_delay){
        if(!m_isUIBlocked)
        {
            m_isUIBlocked = true;
            new Handler().postDelayed(() -> helperMethods.onOpenURL(m_context, strings.HO_IP_LOCATION), m_delay);
        }
    }

    void onServer(long p_delay, REGISTERATION p_registeration_status){
        if(!m_isUIBlocked)
        {
            if(p_registeration_status == REGISTERATION.LOADING_SERVER_SUCCESS){
                m_isUIBlocked = true;
                new Handler().postDelayed(() -> helperMethods.openActivityWithBundle(serverController.class, m_context, keys.REQUEST_TYPE, false), p_delay);
            }
            else if(p_registeration_status == REGISTERATION.LOADING_SERVER_FAILURE){
                m_isUIBlocked = true;
                m_event.invokeObserver(Arrays.asList(strings.SE_SERVER_FAILURE, strings.SE_REQUEST_FAILURE, p_delay), enums.ETYPE.HOME_ALERT);
            }
            else if(p_registeration_status == REGISTERATION.INTERNET_ERROR){
                m_isUIBlocked = true;
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
