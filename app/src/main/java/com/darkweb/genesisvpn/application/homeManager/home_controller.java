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
import com.darkweb.genesisvpn.application.pluginManager.preference_manager;
import com.darkweb.genesisvpn.application.proxyManager.proxy_controller;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class home_controller extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*LOCAL VARIABLE DECLARATION*/

    Button connect_base;
    Button connect_animator;
    ImageButton flag;
    ImageView connect_loading;
    TextView location_info;
    home_view_controller viewController;

    /*INITIALIZATIONS*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);

        initializeModel();
        initializeCrashlytics();
        initializateViews();
        initializeLayout();
        initializeCustomListeners();

        preference_manager.getInstance().initialize();
        proxy_controller.getInstance().startVPN();
        proxy_controller.getInstance().autoStart();
        admanager.getInstance().initialize();
    }

    public void initializeCrashlytics()
    {
        //fabricManager.getInstance().init();
    }

    public void initializeModel(){
        home_model.getInstance().setHomeInstance(this);
    }

    public void initializeLayout(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void initializateViews(){
        connect_base = findViewById(R.id.connect_base);
        connect_animator = findViewById(R.id.connect_animator);
        connect_loading = findViewById(R.id.loading);
        flag = findViewById(R.id.flag);
        location_info = findViewById(R.id.location_info);

        viewController = new home_view_controller(connect_base,connect_animator,connect_loading,flag,location_info);
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
        flag.setOnClickListener(view -> {
            home_ehandler.getInstance().onServer(50);
        });

        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //proxy_controller.getInstance().onOrientationChanged();
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
            home_ehandler.getInstance().aboutUS();
        }
        else if (id == R.id.server)
        {
            home_ehandler.getInstance().onServer(400);
        }
        else if (id == R.id.nav_share)
        {
            home_ehandler.getInstance().onShare();
        }
        else if (id == R.id.nav_help)
        {
            home_ehandler.getInstance().contactUS();
        }
        else if (id == R.id.nav_rate)
        {
            home_ehandler.getInstance().onRateUs();
        }
        else if (id == R.id.ic_menu_privacy)
        {
            home_ehandler.getInstance().privacyPolicy();
        }
        else if (id == R.id.nav_quit)
        {
            home_ehandler.getInstance().onQuit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*EVENT HANDLERS OVERRIDE*/

    public void onStart(View view)
    {
        home_ehandler.getInstance().onStart();
    }

    public void onServer(MenuItem item){
        home_ehandler.getInstance().onServer(400);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //proxy_controller.getInstance().onAppResumed();
        //status.app_status = enums.app_status.resumed;
        //proxy_controller.getInstance().onUpdateCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //status.app_status = enums.app_status.paused;
        //proxy_controller.getInstance().onUpdateCheck();
    }

    /*EVENT VIEW REDIRECTIONS*/

    public void onStartView()
    {
        viewController.onStartView();
    }

    public void onConnected()
    {
        viewController.onConnected();
    }

    public void onDisConnected()
    {
        viewController.onDisConnected();
    }

    public void onConnecting()
    {
        viewController.onConnecting();
    }

    public void onStopping()
    {
        viewController.onStopping();
    }

    public void onSetFlag(String location)
    {
        viewController.onSetFlag(location);
    }

    public void onHideFlag()
    {
        viewController.onHideFlag();
    }

}

