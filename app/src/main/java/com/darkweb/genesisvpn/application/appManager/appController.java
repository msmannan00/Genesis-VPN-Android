package com.darkweb.genesisvpn.application.appManager;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.Arrays;
import java.util.List;

public class appController extends Fragment {

    /*VIEW DECLARATION*/
    private TabLayout m_tab_layout;
    private ViewPager2 m_pager;
    private ImageButton m_back_navigation;
    private ImageButton m_alert_clear_popup;

    /*LOCAL VARIABLE DECLARATION*/

    private appModel m_model;
    private appListModel m_list_model;
    private appViewController m_view_controller;
    private View.OnClickListener m_on_click_listener;
    public Drawable[] m_tabs = new Drawable[3];

    /*ALERT VIEWS*/

    private ConstraintLayout m_alert_dialog;
    private TextView m_alert_title;
    private TextView m_alert_description;
    private Button m_alert_dismiss;
    private Button m_alert_clear_data;

    /*INITIALIZATION*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.app_view, null);
        initializeDrawable();
        initializeViews(root);
        initializeModel();
        initViewPager();
        initializeClickListeners();
        initializeListeners();
        return root;
    }

    public void initializeDrawable()
    {
        m_tabs[0]=getActivity().getDrawable(R.drawable.ic_baseline_system);
        m_tabs[1]=getActivity().getDrawable(R.drawable.ic_baseline_user);
        m_tabs[2]=getActivity().getDrawable(R.drawable.ic_baseline_rescent);
    }

    private void initViewPager() {
        appViewPageAdapter adapter = new appViewPageAdapter(getActivity(), m_list_model);
        m_pager.setAdapter(adapter);
        appFragment.m_pager = m_pager;
        new TabLayoutMediator(m_tab_layout, m_pager, (tab, position) -> tab.setIcon(m_tabs[position])).attach();
    }

    public void initializeModel(){
        m_model = new appModel(getActivity(), new appModelCallback());
        m_view_controller = new appViewController(getActivity(), new appViewCallback(),m_alert_dialog,m_alert_title, m_alert_description);
        m_list_model = new appListModel();
        m_list_model.setModel(status.DISABLED_APPS);
    }

    public void initializeViews(View p_view){
        m_tab_layout = p_view.findViewById(R.id.m_tab_layout);
        m_pager = p_view.findViewById(R.id.m_pager);
        m_alert_dialog = p_view.findViewById(R.id.m_alert_dialog);
        m_alert_title = p_view.findViewById(R.id.m_alert_title);
        m_alert_description = p_view.findViewById(R.id.m_alert_description);
        m_alert_dismiss = p_view.findViewById(R.id.m_alert_dismiss);
        m_back_navigation = p_view.findViewById(R.id.m_back_navigation);
        m_alert_clear_data = p_view.findViewById(R.id.m_alert_clear_data);
        m_alert_clear_popup = p_view.findViewById(R.id.m_alert_clear_popup);
    }

    public void initializeClickListeners(){
        m_on_click_listener = (View v) -> {
            if(v.getId()==R.id.m_back_navigation){
                m_model.quit();
            }
            else if(v.getId()==R.id.m_alert_dismiss){
                m_view_controller.onAlertDismiss();
            }
            else if(v.getId()==R.id.m_alert_clear_popup){
                m_view_controller.onShowAlert(strings.AF_CLEAR_DESC, strings.AF_CLEAR_TITLE ,true);
            }
            else if(v.getId()==R.id.m_alert_clear_data){
                onClearData();
            }
        };
    }

    public void initializeListeners(){
        m_alert_dismiss.setOnClickListener(m_on_click_listener);
        m_back_navigation.setOnClickListener(m_on_click_listener);
        m_alert_clear_data.setOnClickListener(m_on_click_listener);
        m_alert_clear_popup.setOnClickListener(m_on_click_listener);
    }

    /*EVENT HANDLER*/

    public boolean onBackPressed()
    {
        if(m_view_controller.isAlertViewShwoing()){
            m_view_controller.onAlertDismiss();
            return false;
        }
        else {
            if(!status.DISABLED_APPS.equals(m_list_model.getModel())){
                status.DISABLED_APPS.clear();
                status.DISABLED_APPS.addAll(m_list_model.getModel());
                proxyController.getInstance().onSettingChanged();

                pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.DISABLED_APPS, status.DISABLED_APPS), enums.PREFERENCES_ETYPE.SET_SET);
            }
            return true;
        }
    }

    public void onClearData() {
        if(m_pager.getVisibility() == View.VISIBLE && m_pager.getAlpha() == 1){
            m_list_model.getModel().clear();
            status.DISABLED_APPS.clear();
            pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.DISABLED_APPS, status.DISABLED_APPS), enums.PREFERENCES_ETYPE.SET_SET);
            m_pager.getAdapter().notifyDataSetChanged();
            m_pager.invalidate();
            initViewPager();
        }
        m_view_controller.onAlertDismiss();
    }


    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class appModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type.equals(enums.ETYPE.GENERIC_QUIT))
            {
                if(m_view_controller.isAlertViewShwoing()){
                    m_view_controller.onAlertDismiss();
                }
                else {
                    getActivity().onBackPressed();
                }
            }
        }
    }

    public class appViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }

}
