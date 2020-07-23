package com.darkweb.genesisvpn.application.appManager;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.Arrays;
import java.util.List;

public class appController extends AppCompatActivity {

    /*LOCAL VARIABLE DECLARATION*/

    private appModel m_model;
    private appListModel m_list_model;
    private appViewController m_view_controller;
    private TabLayout m_tab_layout;
    private ViewPager2 m_pager;
    public Drawable[] m_tabs = new Drawable[3];

    /*INITIALIZATION*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_view);
        initializeDrawable();
        initializeViews();
        initializeModel();
        initViewPager();
    }
    public void initializeDrawable()
    {
        m_tabs[0]=getDrawable(R.drawable.ic_baseline_system);
        m_tabs[1]=getDrawable(R.drawable.ic_baseline_user);
        m_tabs[2]=getDrawable(R.drawable.ic_baseline_rescent);
    }

    private void initViewPager() {
        appViewPageAdapter adapter = new appViewPageAdapter(this, m_list_model);
        m_pager.setAdapter(adapter);

        new TabLayoutMediator(m_tab_layout, m_pager, (tab, position) -> tab.setIcon(m_tabs[position])).attach();
    }

    public void initializeModel(){
        m_model = new appModel(this, new appModelCallback());
        m_view_controller = new appViewController(this, new appViewCallback());
        m_list_model = new appListModel();
        m_list_model.setModel(status.DISABLED_APPS);
    }

    public void initializeViews(){
        m_tab_layout = findViewById(R.id.tabs);
        m_pager = findViewById(R.id.view_pager);
    }

    /*EVENT HANDLER*/

    @Override
    public void onBackPressed() {
        onBackPressed(null);
    }

    public void onBackPressed(View view)
    {
        if(!status.DISABLED_APPS.equals(m_list_model.getModel())){
            status.DISABLED_APPS.clear();
            status.DISABLED_APPS.addAll(m_list_model.getModel());
            proxyController.getInstance().onAppsFiltered();

            pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.DISABLED_APPS, status.DISABLED_APPS), enums.PREFERENCES_ETYPE.SET_SET);
        }
        m_model.quit();
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class appModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }

    public class appViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }

}
