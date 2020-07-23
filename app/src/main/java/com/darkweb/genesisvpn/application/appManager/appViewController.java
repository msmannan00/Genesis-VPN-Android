package com.darkweb.genesisvpn.application.appManager;


import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.constants;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;

class appViewController {

    /*LOCAL VARIABLE DECLARATION*/
    private AppCompatActivity m_context;
    private eventObserver.eventListener m_event;

    /*INITIALIZATIONS*/

    public appViewController(AppCompatActivity p_context, eventObserver.eventListener p_event)
    {
        this.m_context = p_context;
        m_event = p_event;
        onInitializeView();
    }

    private void onInitializeView(){
        Window window = this.m_context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(m_context.getResources().getColor(R.color.colorPrimary));
        m_context. setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }


    /*HELPER METHODS*/

}
