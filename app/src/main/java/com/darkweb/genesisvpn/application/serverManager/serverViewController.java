package com.darkweb.genesisvpn.application.serverManager;


import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;

class serverViewController {

    /*LOCAL VARIABLE DECLARATION*/
    private FragmentActivity m_context;
    private eventObserver.eventListener m_event;

    /*INITIALIZATIONS*/

    public serverViewController(FragmentActivity p_context, eventObserver.eventListener p_event)
    {
        this.m_context = p_context;
        this.m_event = p_event;

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
