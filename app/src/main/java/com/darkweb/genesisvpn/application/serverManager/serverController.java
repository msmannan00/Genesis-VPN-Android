package com.darkweb.genesisvpn.application.serverManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class serverController extends AppCompatActivity {

    /*LOCAL VARIABLE DECLARATION*/

    private boolean isRequestTypeResponse = false;
    private RecyclerView m_list_view;
    private serverViewController m_view_controller;
    private serverModel m_model;
    private serverListModel m_list_model;
    private TabLayout m_tab_layout;
    private ViewPager2 m_pager;
    public Drawable[] m_tabs = new Drawable[2];

    /*INITIALIZATION*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_view);
        initBundle();
        initializeDrawable();
        initializeModel();
        initializeViews();
        initViewPager();
    }

    public void initBundle(){
        isRequestTypeResponse = getIntent().getBooleanExtra(keys.REQUEST_TYPE, false);
        serverFragment.m_type_response = isRequestTypeResponse;
    }

    public void initializeDrawable()
    {
        m_tabs[0]=getDrawable(R.drawable.ic_baseline_user);
        m_tabs[1]=getDrawable(R.drawable.ic_baseline_rescent);
    }

    private void initViewPager() {
        serverViewPageAdapter adapter = new serverViewPageAdapter(this, m_list_model);
        serverFragment.m_pager = m_pager;
        m_pager.setAdapter(adapter);

        new TabLayoutMediator(m_tab_layout, m_pager, (tab, position) -> tab.setIcon(m_tabs[position])).attach();
    }

    public void initializeModel(){
        sharedControllerManager.getInstance().setServerController(this);
        m_model = new serverModel(this, new serverModelCallback());
        m_view_controller = new serverViewController(this, new serverViewCallback());
        m_list_model = serverListModel.getInstance();
        m_list_model.setRecentModel(status.RECENT_SERVERS);
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
        if(isRequestTypeResponse){
            pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.DEFAULT_SERVER, status.DEFAULT_SERVER), enums.PREFERENCES_ETYPE.SET_STRING);
        }else {
            if(!m_list_model.getRecentModel().equals(status.RECENT_SERVERS)){
                status.RECENT_SERVERS.clear();
                status.RECENT_SERVERS.addAll(m_list_model.getRecentModel());
                ArrayList<String> m_recent_name = new ArrayList<>();
                for(int counter=0;counter<status.RECENT_SERVERS.size();counter++){
                    m_recent_name.add(status.RECENT_SERVERS.get(counter).getHeader());
                }
                pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.RECENT_COUNTRIES, m_recent_name), enums.PREFERENCES_ETYPE.SET_SET);
            }
        }
        m_model.quit();
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class serverModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }

    public class serverViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }

}
