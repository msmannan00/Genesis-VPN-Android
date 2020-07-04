package com.darkweb.genesisvpn.application.aboutManager;

import android.content.pm.ActivityInfo;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.homeManager.homeModel;

class aboutViewController {

    /*LOCAL VARIABLE DECLARATION*/
    private AppCompatActivity m_context;

    /*INITIALIZATIONS*/

    public aboutViewController(AppCompatActivity p_context)
    {
        this.m_context = p_context;
        onInitializeView();
    }

    private void onInitializeView(){
        Window window = this.m_context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(homeModel.getInstance().getHomeInstance().getResources().getColor(R.color.colorPrimary));
        m_context. setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }


    /*HELPER METHODS*/

}
