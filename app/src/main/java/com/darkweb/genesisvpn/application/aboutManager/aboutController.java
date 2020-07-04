package com.darkweb.genesisvpn.application.aboutManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.pluginManager.admanager;

public class aboutController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private aboutViewController m_view_controller;
    private aboutModel m_model;

    /* INITIALIZATION */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_view);
        initializeHandlers();
        initializeViews();
    }

    public void initializeViews(){
        m_view_controller = new aboutViewController(this);
    }

    public void initializeHandlers(){
        m_model = new aboutModel(this);
    }

    public void adsDisabler(View view)
    {
        admanager.getInstance().adsDisabler(this);
    }

    /*EVENT HANDLER*/

    public void onBackPressed(View view)
    {
        m_model.quit();
    }
}
