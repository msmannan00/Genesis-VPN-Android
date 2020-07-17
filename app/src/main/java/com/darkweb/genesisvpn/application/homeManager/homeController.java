package com.darkweb.genesisvpn.application.homeManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.anchorfree.partner.api.data.Country;
import com.darkweb.genesisvpn.R;
import android.os.Handler;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.enums.TRIGGER;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.helperManager.OnClearFromRecentService;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class homeController extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*LOCAL VARIABLE DECLARATION*/

    Button m_connect_base;
    Button m_connect_animator;
    ImageButton m_flag;
    ImageView m_connect_loading;
    TextView m_location_info;
    homeViewController m_view_controller;
    homeModel m_model;
    TextView m_connect_label;
    ConstraintLayout m_alert_dialog;
    TextView m_alert_title;
    TextView m_alert_description;
    com.google.android.gms.ads.AdView m_banner_ads;
    proxyController m_proxy_controller;

    /*INITIALIZATIONS*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);

        initializeCrashHandler();
        initializeModel();
        initializeViews();
        initializeLayout();
        initializeCustomListeners();

        initializePluginManager();
        m_proxy_controller.onStartVPN();

    }

    public void initializeCrashHandler(){
         Thread.setDefaultUncaughtExceptionHandler(
                 (thread, e) -> {
                     status.HAS_APPLICATION_STOPPED = true;
                     m_proxy_controller.onForceClose();
                     homeController.this.finishAndRemoveTask();
                     new Thread(){
                         public void run(){
                             try {
                                 sleep(500);
                                 android.os.Process.killProcess(android.os.Process.myPid());
                             } catch (InterruptedException ex) {
                                 ex.printStackTrace();
                             }
                         }
                     }.start();
                 });
    }

    public void initializePluginManager()
    {
        pluginManager.getInstance().onPreferenceTrigger(Collections.singletonList(this), enums.PREFERENCES_ETYPE.INITIALIZE);
        pluginManager.getInstance().onAdvertTrigger(Arrays.asList(this, m_banner_ads), enums.AD_ETYPE.INITIALIZE);
        pluginManager.getInstance().onAnalyticsTrigger(Collections.singletonList(this), enums.ANALYTIC_ETYPE.INITIALIZE);
    }

    public void initializeModel(){
        sharedControllerManager.getInstance().setHomeController(this);
        m_proxy_controller = proxyController.getInstance();
    }

    public void initializeLayout(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void initializeViews(){
        m_connect_base = findViewById(R.id.connect_base);
        m_connect_animator = findViewById(R.id.connect_animator);
        m_connect_loading = findViewById(R.id.loading);
        m_flag = findViewById(R.id.flag);
        m_connect_label = findViewById(R.id.connect_label);
        m_location_info = findViewById(R.id.location_info);
        m_alert_dialog = findViewById(R.id.alert_dialog);
        m_alert_title = findViewById(R.id.alert_title);
        m_alert_description = findViewById(R.id.alert_description);
        m_banner_ads = findViewById(R.id.banner_ads);

        m_view_controller = new homeViewController(m_connect_base, m_connect_animator, m_connect_loading, m_flag, m_location_info, m_connect_label, m_alert_dialog,m_alert_title, m_alert_description, this);
        m_model = new homeModel(this, new homeModelCallback());
    }

    /*EVENT HANDLERS DEFAULTS*/

    public void onAlertDismiss(View view) {
        m_view_controller.onAlertDismiss();
    }

    public void onAlertStop(View view) {
        m_view_controller.onAlertDismiss();
        m_proxy_controller.onForceStop();
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initializeCustomListeners()
    {
        m_flag.setOnClickListener(view -> {
            m_proxy_controller.clearExceptionCounter();
            m_model.onServer(50, m_proxy_controller.isUserRegistered());
        });
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        m_connect_base.setOnTouchListener((view, motionEvent) -> {
            m_view_controller.animateButtonClicked(motionEvent);
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onVPNClose(){
        m_proxy_controller.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isFinishing()){
           status.HAS_APPLICATION_STOPPED = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about)
        {
            m_model.onAboutUS();
        }
        else if (id == R.id.server)
        {
            m_proxy_controller.clearExceptionCounter();
            m_model.onServer(400,m_proxy_controller.isUserRegistered());
        }
        else if (id == R.id.nav_share)
        {
            m_model.onShare();
        }
        else if (id == R.id.nav_help)
        {
            m_model.onContactUS();
        }
        else if (id == R.id.nav_rate)
        {
            m_model.onRateUs();
        }
        else if (id == R.id.ic_menu_privacy)
        {
            m_model.onPrivacyPolicy();
        }
        else if (id == R.id.nav_quit)
        {
            m_proxy_controller.onCloseSmooth();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*EVENT HANDLERS OVERRIDE*/

    public void onStart(View view)
    {
        m_proxy_controller.onTriggered(TRIGGER.TOOGLE);
    }

    public void onServer(MenuItem item){
        m_proxy_controller.clearExceptionCounter();
        m_model.onServer(400,m_proxy_controller.isUserRegistered());
    }

    public void onChooseServer(Country server){
        m_proxy_controller.onChooseServer(server);
    }

    /*EVENT VIEW CALLBACK HANDLER*/

    public class homeModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type == enums.ETYPE.HOME_ALERT){
                new Handler().postDelayed(() -> m_view_controller.onShowAlert((String) p_data.get(0),(String) p_data.get(1), true),(long) p_data.get(2));
            }
        }
    }


    /*EVENT VIEW REDIRECTIONS*/

    public void onShowAlert(String p_error, boolean p_is_forced){
        m_view_controller.onShowAlert(p_error, "Request Failure", p_is_forced);
    }

    public void onConnected()
    {
        m_view_controller.onConnected();
    }

    public void onIdle()
    {
        m_view_controller.onIdle();
    }

    public void onConnecting()
    {
        m_view_controller.onConnecting();
    }

    public void onDisconnecting()
    {
        m_view_controller.onDisconnecting();
    }

    public void onSetFlag(String location)
    {
        m_view_controller.onSetFlag(location);
    }

}

