package com.darkweb.genesisvpn.application.aboutManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import java.util.Collections;
import java.util.List;

public class aboutController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private aboutViewController m_view_controller;
    private aboutModel m_model;

    /* INITIALIZATION */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_view);
        initializeModel();
        initializeViews();
    }

    public void initializeViews(){
        m_view_controller = new aboutViewController(this, new aboutViewCallback());
    }

    public void initializeModel(){
        m_model = new aboutModel(this, new aboutModelCallback());
    }

    /*LAYOUT EVENT HANDLER*/

    public void onBackPressed(View view)
    {
        m_model.quit();
    }

    public void adsDisabler(View view)
    {
        m_model.adsDisabler();
    }


    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class aboutModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type.equals(enums.ETYPE.ABOUT_ADS_REMOVE_CLICK))
            {
                pluginManager.getInstance().onAdvertTrigger(Collections.singletonList(aboutController.this),enums.AD_ETYPE.DISABLE_ADS);
            }
            else if(p_event_type.equals(enums.ETYPE.GENERIC_QUIT))
            {
                helperMethods.quit(aboutController.this);
            }
        }
    }

    public class aboutViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }
}
