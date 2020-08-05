package com.darkweb.genesisvpn.application.aboutManager;

import android.content.pm.ActivityInfo;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;

class aboutViewController {

    /*LOCAL VARIABLE DECLARATION*/
    private FragmentActivity m_context;
    private eventObserver.eventListener m_event;
    private ImageButton m_back_navigation;

    /*INITIALIZATIONS*/

    public aboutViewController(FragmentActivity p_context, eventObserver.eventListener p_event, ImageButton p_back_navigation)
    {
        this.m_context = p_context;
        this.m_event = p_event;
        this.m_back_navigation = p_back_navigation;

        onInitializeView();
    }

    private void onInitializeView(){
        Window window = this.m_context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(m_context.getResources().getColor(R.color.colorPrimary));
    }

    /*HELPER METHODS*/

}
