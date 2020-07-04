package com.darkweb.genesisvpn.application.homeManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.application.aboutManager.aboutController;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.pluginManager.messageManager;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.darkweb.genesisvpn.application.serverManager.serverController;
import com.darkweb.genesisvpn.application.statusManager.status;

class homeModel
{
    /* PRIVATE VARIABLES */

    private boolean m_isUIBlocked = false;
    private AppCompatActivity m_context;

    /*INITIALIZATION*/

    public homeModel(AppCompatActivity p_context){
        this.m_context = p_context;
    }

    /*HANDLERS*/

    void onShare()
    {
        if(!m_isUIBlocked)
        {
            resetUIBlock();
            m_isUIBlocked = true;
            helperMethods.shareApp(m_context);
        }
    }

    void privacyPolicy()
    {
        m_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://infogamesolstudios.blogspot.com/p/privacy-policy-function-var-html5.html")));
    }

    void onRateUs(){
        m_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesisvpn")));
    }

    void onQuit(){
        proxyController.getInstance().onStop();
        helperMethods.quit(m_context);
        m_context.moveTaskToBack(true);
    }

    void contactUS()
    {
        helperMethods.sendEmail(m_context);
    }

    void aboutUS(){
        if(!m_isUIBlocked)
        {
            resetUIBlock();
            m_isUIBlocked = true;
            new Handler().postDelayed(() -> helperMethods.openActivity(aboutController.class, m_context), 400);
        }
    }

    void onServer(long delay){
        if(!m_isUIBlocked)
        {
            resetUIBlock();
            m_isUIBlocked = true;
            if(status.servers_loaded == enums.connection_servers.loaded)
            {
                new Handler().postDelayed(() -> helperMethods.openActivity(serverController.class, m_context), delay);
            }
            else if(status.servers_loaded == enums.connection_servers.internet_error)
            {
                new Handler().postDelayed(() -> messageManager.getInstance().internetError(m_context), delay);
            }
            else
            {
                new Handler().postDelayed(() -> messageManager.getInstance().serverLoading(m_context), delay);
            }
        }
    }

    private void resetUIBlock()
    {
        new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(1000);
                    m_isUIBlocked = false;
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
