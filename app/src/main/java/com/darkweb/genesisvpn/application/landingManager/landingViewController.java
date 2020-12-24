package com.darkweb.genesisvpn.application.landingManager;

import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;

class landingViewController
{
    /*Private Variables*/
    private AppCompatActivity mContext;

    /*Initializations*/

    landingViewController(AppCompatActivity mContext, eventObserver.eventListener event){
        this.mContext = mContext;
        initStatusBarColor();
    }

    void initStatusBarColor(){
        Window window = mContext.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(mContext.getResources().getColor(R.color.colorPrimary));
    }

}
