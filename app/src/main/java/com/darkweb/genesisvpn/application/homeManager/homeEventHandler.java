package com.darkweb.genesisvpn.application.homeManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.darkweb.genesisvpn.application.aboutManager.aboutController;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.pluginManager.messageManager;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.darkweb.genesisvpn.application.serverManager.serverController;
import com.darkweb.genesisvpn.application.status.status;

class homeEventHandler
{
    /*INITIALIZATION*/

    private static final homeEventHandler ourInstance = new homeEventHandler();
    private boolean isUIBlocked = false;

    static homeEventHandler getInstance() {
        return ourInstance;
    }

    /*HANDLERS*/

    void onShare()
    {
        if(!isUIBlocked)
        {
            resetUIBlock();
            isUIBlocked = true;
            helperMethods.shareApp();
        }
    }

    void privacyPolicy()
    {
        homeModel.getInstance().getHomeInstance().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://infogamesolstudios.blogspot.com/p/privacy-policy-function-var-html5.html")));
    }

    void onRateUs(){
        homeModel.getInstance().getHomeInstance().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.darkweb.genesisvpn")));
    }

    void onQuit(){
        proxyController.getInstance().onStop();
        helperMethods.quit(homeModel.getInstance().getHomeInstance());
        homeModel.getInstance().getHomeInstance().moveTaskToBack(true);
    }

    void contactUS()
    {
        helperMethods.sendEmail();
    }

    void aboutUS(){
        if(!isUIBlocked)
        {
            resetUIBlock();
            isUIBlocked = true;
            new Handler().postDelayed(() -> helperMethods.openActivity(aboutController.class), 400);
        }
    }

    void onServer(long delay){
        if(!isUIBlocked)
        {
            resetUIBlock();
            isUIBlocked = true;
            if(status.servers_loaded == enums.connection_servers.loaded)
            {
                new Handler().postDelayed(() -> helperMethods.openActivity(serverController.class), delay);
            }
            else
            {
                new Handler().postDelayed(() -> messageManager.getInstance().serverLoading(), delay);
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
                    isUIBlocked = false;
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    void onStart()
    {
        homeModel.getInstance().getHomeInstance().onStartView();
    }

}
