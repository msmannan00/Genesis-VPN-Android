package com.darkweb.genesisvpn.application.serverManager;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.List;

public class serverController extends Fragment {

    /*LOCAL VIEWS*/
    private TabLayout m_tab_layout;
    private ViewPager2 m_pager;

    /*LOCAL VARIABLE DECLARATION*/

    private boolean isRequestTypeResponse = false;
    private boolean isRunning = false;
    private boolean isChangedWhileRunning = false;
    private View.OnClickListener m_on_click_listener;
    private serverViewController m_view_controller;
    private serverModel m_model;
    private serverListModel m_list_model;
    private ImageButton m_back_navigation;
    public Drawable[] m_tabs = new Drawable[2];

    /*INITIALIZATION*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.server_view, null);
        isRunning = true;
        initBundle();
        initializeDrawable();
        initializeModel();
        initializeViews(root);
        initViewPager();
        initializeClickListeners();
        initializeListeners();
        return root;
    }

    public void onInitialize(){

    }

    public void initBundle(){
        if(this.getArguments() != null){
            isRequestTypeResponse = this.getArguments().getBoolean(keys.REQUEST_TYPE, false);
            serverFragment.m_type_response = isRequestTypeResponse;
        }
        else {
            serverFragment.m_type_response = false;
        }
    }

    public void initializeDrawable()
    {
        m_tabs[0]=getActivity().getDrawable(R.drawable.ic_baseline_user);
        m_tabs[1]=getActivity().getDrawable(R.drawable.ic_baseline_rescent);
    }

    public void initViewPager() {
        if(getActivity()!=null){
            serverViewPageAdapter adapter = new serverViewPageAdapter(getActivity(), m_list_model);
            serverFragment.m_pager = m_pager;
            m_pager.setAdapter(adapter);

            new TabLayoutMediator(m_tab_layout, m_pager, (tab, position) -> tab.setIcon(m_tabs[position])).attach();
        }
    }

    public void onDataChanged(){
        if (sharedControllerManager.getInstance().getHomeController().isFragmentVisible(serverController.this.getClass().getName())) {
            isChangedWhileRunning = true;
        }else {
            initializeModel();
            initViewPager();
        }
    }

    public void initializeModel(){
        sharedControllerManager.getInstance().setServerController(this);
        m_model = new serverModel(getActivity(), new serverModelCallback());
        m_view_controller = new serverViewController(getActivity(), new serverViewCallback());


        m_list_model = serverListModel.getInstance();
        m_list_model.onInitialize(new serverListModelCallback());
        //m_list_model.setRecentModel(status.RECENT_SERVERS);
    }

    public void initializeViews(View p_view){
        m_tab_layout = p_view.findViewById(R.id.m_tab_layout);
        m_pager = p_view.findViewById(R.id.m_pager);
        m_back_navigation = p_view.findViewById(R.id.m_back_navigation);
    }

    public void initializeClickListeners(){
        m_on_click_listener = (View v) -> {
            if(v.getId()==R.id.m_back_navigation){
                m_model.quit();
            }
        };
    }

    public void initializeListeners(){
        m_back_navigation.setOnClickListener(m_on_click_listener);
    }

    /*EVENT HANDLER*/

    public boolean onBackPressed()
    {
        isRunning = false;
        if(isRequestTypeResponse){
            pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.DEFAULT_SERVER, status.DEFAULT_SERVER), enums.PREFERENCES_ETYPE.SET_STRING);
        }
        if(isChangedWhileRunning){
            isChangedWhileRunning = false;
            initializeModel();
            initViewPager();
        }
        return true;
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class serverModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type.equals(enums.ETYPE.GENERIC_QUIT))
            {
                getActivity().onBackPressed();
            }
        }
    }

    public class serverViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }

    public class serverListModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type.equals(enums.ETYPE.ON_LOAD_LIST))
            {
                initViewPager();
            }
        }
    }

}
