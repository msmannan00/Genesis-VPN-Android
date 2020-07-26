package com.darkweb.genesisvpn.application.settingManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.R;
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

public class settingController extends AppCompatActivity {

    /* PRIVATE VIEWS */

    Switch m_auto_connect;
    Switch m_auto_start;
    Switch m_auto_optimal_location;
    RadioButton m_udp_connection;
    RadioButton m_tcp_connection;
    ImageView m_default_server;

    /* PRIVATE VARIABLES */

    private settingViewController m_view_controller;
    private settingModel m_model;

    /* INITIALIZATION */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_view);
        initializeModel();
        initializeViews();
    }

    public void initializeViews(){
        m_view_controller = new settingViewController(this, new settingViewCallback(), m_auto_connect, m_auto_start, m_udp_connection, m_tcp_connection, m_default_server, m_auto_optimal_location);
    }

    public void initializeModel(){
        m_model = new settingModel(this, new settingModelCallback());
        m_auto_connect = findViewById(R.id.p_auto_connect);
        m_auto_start = findViewById(R.id.p_auto_start);
        m_udp_connection = findViewById(R.id.m_udp_connection);
        m_tcp_connection = findViewById(R.id.m_tcp_connection);
        m_default_server = findViewById(R.id.p_default_server);
        m_auto_optimal_location = findViewById(R.id.p_auto_optimal_location);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_view_controller.onUpdateFlag();
    }

    /*LAYOUT EVENT HANDLER*/

    @Override
    public void onBackPressed() {
        onBackPressed(null);
    }

    public void onBackPressed(View view){
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
        if(status.CONNECTION_TYPE == 0 && m_tcp_connection.isChecked()){
            status.CONNECTION_TYPE = 1;
            pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.AUTO_RESET, true), enums.PREFERENCES_ETYPE.SET_BOOL);
            sharedControllerManager.getInstance().getProxyController().onSettingChanged();
        }
        else if(status.CONNECTION_TYPE == 1 && m_udp_connection.isChecked()){
            status.CONNECTION_TYPE = 0;
            pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.AUTO_RESET, false), enums.PREFERENCES_ETYPE.SET_BOOL);
            sharedControllerManager.getInstance().getProxyController().onSettingChanged();
        }
        m_model.quit();
    }

    public void onAutoConnect(View view){
        m_auto_connect.toggle();
    }

    public void onAutoStart(View view){
        m_auto_start.toggle();
    }

    public void onAutoDefaultServer(View view){
        m_auto_optimal_location.toggle();
        status.AUTO_OPTIMAL_LOCATION = m_auto_optimal_location.isChecked();
        m_view_controller.onUpdateFlag();
    }

    public void onChangeDefaultServer(View view){
        if(proxyController.getInstance().isUserRegistered() == enums.REGISTERATION.LOADING_SERVER_SUCCESS){
            helperMethods.openActivityWithBundle(serverController.class, this, keys.REQUEST_TYPE, true);
        }
    }

    public void onChangeConnectionType(View view){
        if(m_tcp_connection.isChecked()){
            m_tcp_connection.setChecked(false);
            m_udp_connection.setChecked(true);
        }else {
            m_tcp_connection.setChecked(true);
            m_udp_connection.setChecked(false);
        }
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class settingModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type.equals(enums.ETYPE.GENERIC_QUIT))
            {
                helperMethods.quit(settingController.this);
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
