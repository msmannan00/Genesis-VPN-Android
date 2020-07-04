package com.darkweb.genesisvpn.application.homeManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.darkweb.genesisvpn.R;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.darkweb.genesisvpn.application.helperManager.OnClearFromRecentService;
import com.darkweb.genesisvpn.application.pluginManager.admanager;
import com.darkweb.genesisvpn.application.pluginManager.preferenceManager;
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

public class homeController extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*LOCAL VARIABLE DECLARATION*/

    Button m_connect_base;
    Button m_connect_animator;
    ImageButton m_flag;
    ImageView m_connect_loading;
    TextView m_location_info;
    homeViewController m_view_controller;
    homeModel m_model;

    /*INITIALIZATIONS*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);

        initializeModel();
        initializeCrashlytics();
        initializeViews();
        initializeLayout();
        initializeCustomListeners();

        preferenceManager.getInstance().initialize(this);
        proxyController.getInstance().startVPN();
        admanager.getInstance().initialize(this);

    }

    public void initializeCrashlytics()
    {
        //fabricManager.getInstance().init();
    }

    public void initializeModel(){
        sharedControllerManager.getInstance().setHomeController(this);
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
        m_location_info = findViewById(R.id.location_info);
        m_view_controller = new homeViewController(m_connect_base, m_connect_animator, m_connect_loading, m_flag, m_location_info,this);
        m_model = new homeModel(this);
    }

    /*EVENT HANDLERS DEFAULTS*/

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void initializeCustomListeners()
    {
        m_flag.setOnClickListener(view -> m_model.onServer(50));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
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
            m_model.aboutUS();
        }
        else if (id == R.id.server)
        {
            m_model.onServer(400);
        }
        else if (id == R.id.nav_share)
        {
            m_model.onShare();
        }
        else if (id == R.id.nav_help)
        {
            m_model.contactUS();
        }
        else if (id == R.id.nav_rate)
        {
            m_model.onRateUs();
        }
        else if (id == R.id.ic_menu_privacy)
        {
            m_model.privacyPolicy();
        }
        else if (id == R.id.nav_quit)
        {
            m_model.onQuit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*EVENT HANDLERS OVERRIDE*/

    public void onStart(View view)
    {
        int ss = 3/0;
        proxyController.getInstance().triggeredManual();
    }

    public void onServer(MenuItem item){
        m_model.onServer(400);
    }

    /*EVENT VIEW REDIRECTIONS*/

    public void onConnected()
    {
        m_view_controller.onConnected();
    }

    public void onStopped()
    {
        m_view_controller.onStopped();
    }

    public void onConnecting()
    {
        m_view_controller.onConnecting();
    }

    public void onStopping()
    {
        m_view_controller.onStopping();
    }

    public void onSetFlag(String location)
    {
        m_view_controller.onSetFlag(location);
    }

    public void onHideFlag()
    {
        m_view_controller.onHideFlag();
    }

}

