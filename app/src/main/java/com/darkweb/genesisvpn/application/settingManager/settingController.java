package com.darkweb.genesisvpn.application.settingManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.appManager.appController;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.darkweb.genesisvpn.application.serverManager.serverController;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;

import java.util.Arrays;
import java.util.List;

import static com.darkweb.genesisvpn.application.constants.strings.SE_SERVER_MESSAGE_DESC;

public class settingController extends Fragment {

    /* PRIVATE VIEWS */

    private Switch m_auto_connect;
    private Switch m_auto_start;
    private Switch m_auto_optimal_location;
    private RadioButton m_udp_connection;
    private RadioButton m_tcp_connection;
    private RadioButton m_def_connection;
    private ImageView m_default_server;
    private ImageButton m_back_navigation;

    /* PRIVATE VIEWS CONTAINERS */

    private LinearLayout m_change_server_container;
    private LinearLayout m_connect_startup_container;
    private LinearLayout m_connect_launch_container;
    private LinearLayout m_connect_type_UDP;
    private LinearLayout m_connect_type_TCP;
    private LinearLayout m_connect_type_AUTO;
    private LinearLayout m_auto_optimal_container;
    private LinearLayout m_proxy_filter_container;

    /* PRIVATE ALERT VARIABLES */

    private ConstraintLayout m_alert_dialog;
    private TextView m_alert_title;
    private TextView m_alert_description;
    private Button m_alert_dismiss;
    private View.OnClickListener m_on_click_listener;

    /* PRIVATE VARIABLES */

    private settingViewController m_view_controller;
    private settingModel m_model;
    private boolean isInteractionBlocked = false;

    /* INITIALIZATION */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.setting_view, null);
        initializeModel(root);
        initializeViews();
        initializeClickListeners();
        initializeListeners();
        return root;
    }

    public void onResumeFragment(){
        m_view_controller.onUpdateFlag();
        isInteractionBlocked = false;
    }

    public void initializeViews(){
        m_view_controller = new settingViewController(this.getActivity(), new settingViewCallback(), m_auto_connect, m_auto_start, m_udp_connection, m_tcp_connection, m_default_server, m_auto_optimal_location, m_change_server_container, m_def_connection, m_alert_dialog,m_alert_title, m_alert_description);
    }

    public void initializeModel(View root){
        m_model = new settingModel(getActivity(), new settingModelCallback());
        m_auto_connect = root.findViewById(R.id.p_auto_connect);
        m_auto_start = root.findViewById(R.id.p_auto_start);
        m_udp_connection = root.findViewById(R.id.m_udp_connection);
        m_tcp_connection = root.findViewById(R.id.m_tcp_connection);
        m_def_connection = root.findViewById(R.id.m_def_connection);
        m_default_server = root.findViewById(R.id.p_default_server);
        m_auto_optimal_location = root.findViewById(R.id.p_auto_optimal_location);
        m_change_server_container = root.findViewById(R.id.m_change_server_container);
        m_alert_dialog = root.findViewById(R.id.m_alert_dialog);
        m_alert_title = root.findViewById(R.id.m_alert_title);
        m_alert_description = root.findViewById(R.id.m_alert_description);
        m_back_navigation = root.findViewById(R.id.m_back_navigation);
        m_connect_startup_container = root.findViewById(R.id.m_connect_startup_container);
        m_connect_launch_container = root.findViewById(R.id.m_connect_launch_container);
        m_connect_type_UDP = root.findViewById(R.id.m_connect_type_UDP);
        m_connect_type_TCP = root.findViewById(R.id.m_connect_type_TCP);
        m_connect_type_AUTO = root.findViewById(R.id.m_connect_type_AUTO);
        m_auto_optimal_container = root.findViewById(R.id.m_auto_optimal_container);
        m_proxy_filter_container = root.findViewById(R.id.m_proxy_filter_container);
        m_alert_dismiss = root.findViewById(R.id.m_alert_dismiss);
    }

    public void initializeClickListeners(){
        m_on_click_listener = (View v) -> {
            if(isInteractionBlocked){
            }
            else if(v.getId()==R.id.m_back_navigation){
                m_model.quit();
            }
            else if(v.getId()==R.id.m_connect_startup_container){
                m_auto_start.toggle();
            }
            else if(v.getId()==R.id.m_connect_launch_container){
                m_auto_connect.toggle();
            }
            else if(v.getId()==R.id.m_connect_type_UDP){
                m_view_controller.onUpdateConnectionType(v.getId());
            }
            else if(v.getId()==R.id.m_connect_type_TCP){
                m_view_controller.onUpdateConnectionType(v.getId());
            }
            else if(v.getId()==R.id.m_connect_type_AUTO){
                m_view_controller.onUpdateConnectionType(v.getId());
            }
            else if(v.getId()==R.id.m_auto_optimal_container){
                m_auto_optimal_location.toggle();
                m_view_controller.onDefaultServerToggle(m_auto_optimal_location.isChecked());
            }
            else if(v.getId()==R.id.m_proxy_filter_container){
                isInteractionBlocked = true;
                helperMethods.openFragmentWithBundle((FrameLayout) getFragmentManager().getFragments().get(0).getView().getParent(), new appController(), getActivity(), keys.REQUEST_TYPE, true);
            }
            else if(v.getId()==R.id.m_change_server_container){
                if(proxyController.getInstance().isUserRegistered() == enums.REGISTERATION.LOADING_SERVER_SUCCESS){
                    isInteractionBlocked = true;
                    helperMethods.openFragmentWithBundle((FrameLayout) getFragmentManager().getFragments().get(0).getView().getParent(), new serverController(), getActivity(), keys.REQUEST_TYPE, true);
                }else{
                    m_view_controller.onShowAlert(SE_SERVER_MESSAGE_DESC, strings.SE_REQUEST_INITIALIZING, false);
                }
            }
            else if(v.getId()==R.id.m_alert_dismiss || v.getId()==R.id.m_alert_dialog){
                m_view_controller.onAlertDismiss();
            }
        };
    }

    public void initializeListeners(){
        m_back_navigation.setOnClickListener(m_on_click_listener);
        m_connect_startup_container.setOnClickListener(m_on_click_listener);
        m_connect_launch_container.setOnClickListener(m_on_click_listener);
        m_connect_type_UDP.setOnClickListener(m_on_click_listener);
        m_connect_type_TCP.setOnClickListener(m_on_click_listener);
        m_connect_type_AUTO.setOnClickListener(m_on_click_listener);
        m_alert_dialog.setOnClickListener(m_on_click_listener);
        m_auto_optimal_container.setOnClickListener(m_on_click_listener);
        m_proxy_filter_container.setOnClickListener(m_on_click_listener);
        m_change_server_container.setOnClickListener(m_on_click_listener);
        m_alert_dismiss.setOnClickListener(m_on_click_listener);
    }

    /*LAYOUT EVENT HANDLER*/

    @Override
    public void onResume() {
        super.onResume();
        m_view_controller.onUpdateFlag();
    }

    public boolean onBackPressed(){
        if(m_view_controller.isAlertViewShwoing()){
            m_view_controller.onAlertDismiss();
            return false;
        }
        else {
            if(status.AUTO_CONNECT != m_auto_connect.isChecked()){
                status.AUTO_CONNECT = m_auto_connect.isChecked();
                pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.AUTO_CONNECT, status.AUTO_CONNECT), enums.PREFERENCES_ETYPE.SET_BOOL);
            }
            if(status.AUTO_OPTIMAL_LOCATION != m_auto_optimal_location.isChecked()){
                status.AUTO_OPTIMAL_LOCATION = m_auto_optimal_location.isChecked();
                pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.AUTO_OPTIMAL_LOCATION, status.AUTO_OPTIMAL_LOCATION), enums.PREFERENCES_ETYPE.SET_BOOL);
            }
            if(status.AUTO_START != m_auto_start.isChecked()){
                status.AUTO_START = m_auto_start.isChecked();
                pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.AUTO_START, status.AUTO_START), enums.PREFERENCES_ETYPE.SET_BOOL);
            }
            if(status.CONNECTION_TYPE != 1 && m_tcp_connection.isChecked()){
                status.CONNECTION_TYPE = 1;
                pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.CONNECTION_TYPE, 1), enums.PREFERENCES_ETYPE.SET_INT);
                sharedControllerManager.getInstance().getProxyController().onSettingChanged();
            }
            else if(status.CONNECTION_TYPE != 0 && m_udp_connection.isChecked()){
                status.CONNECTION_TYPE = 0;
                pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.CONNECTION_TYPE, 0), enums.PREFERENCES_ETYPE.SET_INT);
                sharedControllerManager.getInstance().getProxyController().onSettingChanged();
            }
            else if(status.CONNECTION_TYPE != 2 && m_def_connection.isChecked()){
                status.CONNECTION_TYPE = 2;
                pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.CONNECTION_TYPE, 2), enums.PREFERENCES_ETYPE.SET_INT);
                sharedControllerManager.getInstance().getProxyController().onSettingChanged();
            }
            return true;
        }
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public void onFragmentClose(){
        if(m_view_controller.isAlertViewShwoing()){
            m_view_controller.onAlertDismiss();
        }
        else {
            getActivity().onBackPressed();
        }
    }

    public class settingModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type.equals(enums.ETYPE.GENERIC_QUIT))
            {
                if(m_view_controller.isAlertViewShwoing()){
                    m_view_controller.onAlertDismiss();
                }else {
                    onFragmentClose();
                    isInteractionBlocked = false;
                }
            }
        }
    }

    public class settingViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }
}
