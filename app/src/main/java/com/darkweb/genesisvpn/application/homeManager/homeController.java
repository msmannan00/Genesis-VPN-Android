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

    Button connect_base;
    Button connect_animator;
    ImageButton flag;
    ImageView connect_loading;
    TextView location_info;
    homeViewController viewController;

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

        preferenceManager.getInstance().initialize();
        proxyController.getInstance().startVPN();
        admanager.getInstance().initialize();

    }

    public void initializeCrashlytics()
    {
        //fabricManager.getInstance().init();
    }

    public void initializeModel(){
        homeModel.getInstance().setHomeInstance(this);
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
        connect_base = findViewById(R.id.connect_base);
        connect_animator = findViewById(R.id.connect_animator);
        connect_loading = findViewById(R.id.loading);
        flag = findViewById(R.id.flag);
        location_info = findViewById(R.id.location_info);
        viewController = new homeViewController(connect_base,connect_animator,connect_loading,flag,location_info);
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
        flag.setOnClickListener(view -> homeEventHandler.getInstance().onServer(50));

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
            homeEventHandler.getInstance().aboutUS();
        }
        else if (id == R.id.server)
        {
            homeEventHandler.getInstance().onServer(400);
        }
        else if (id == R.id.nav_share)
        {
            homeEventHandler.getInstance().onShare();
        }
        else if (id == R.id.nav_help)
        {
            homeEventHandler.getInstance().contactUS();
        }
        else if (id == R.id.nav_rate)
        {
            homeEventHandler.getInstance().onRateUs();
        }
        else if (id == R.id.ic_menu_privacy)
        {
            homeEventHandler.getInstance().privacyPolicy();
        }
        else if (id == R.id.nav_quit)
        {
            homeEventHandler.getInstance().onQuit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*EVENT HANDLERS OVERRIDE*/

    public void onStart(View view)
    {
        homeEventHandler.getInstance().onStart();
    }

    public void onServer(MenuItem item){
        homeEventHandler.getInstance().onServer(400);
    }

    /*EVENT VIEW REDIRECTIONS*/

    public void onStartView()
    {
        proxyController.getInstance().triggeredManual();
    }

    public void onConnected()
    {
        viewController.onConnected();
    }

    public void onStopped()
    {
        viewController.onStopped();
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

