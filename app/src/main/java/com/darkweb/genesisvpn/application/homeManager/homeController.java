package com.darkweb.genesisvpn.application.homeManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import com.darkweb.genesisvpn.R;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.MenuItem;
import com.darkweb.genesisvpn.application.aboutManager.aboutController;
import com.darkweb.genesisvpn.application.appManager.appController;
import com.darkweb.genesisvpn.application.constants.constants;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.enums.TRIGGER;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.OnClearFromRecentService;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.promotionManager.promotionController;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.darkweb.genesisvpn.application.serverManager.serverController;
import com.darkweb.genesisvpn.application.settingManager.settingController;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.Menu;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class homeController extends FragmentActivity {

    /*LOCAL VARIABLE DECLARATION*/
    DrawerLayout m_drawer;
    FrameLayout m_fragment_container;

    TextView m_alert_title;
    TextView m_connect_label;
    TextView m_alert_description;
    TextView m_download_speed;
    TextView m_upload_speed;

    Button m_connect_base;
    Button m_connect_animator;
    Button m_connect_animator_initial;
    Button m_connection_toggle;
    Button m_stop_alert_btn;
    Button m_dismiss_alert_btn;

    ImageView m_speed_base;
    ImageView m_connect_loading;
    ImageView m_blocker;
    ImageButton m_flag;

    homeViewController m_view_controller;
    homeModel m_model;
    ConstraintLayout m_alert_dialog;
    com.google.android.gms.ads.AdView m_banner_ads;
    proxyController m_proxy_controller;

    /*INITIALIZATIONS*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);
        initializeBoot();
        initializeCrashHandler();
        initializeModel();
        initializeProxy();
        initializeViews();
        initializeCustomListeners();
        initializePluginManager();
        initializeVPN();
        initializeFragment();
        postInitializations();
    }

    private void postInitializations() {
        proxyController.getInstance().onAutoConnectInitialization();
    }

    private void initializeProxy() {
        m_proxy_controller = proxyController.getInstance();
    }

    public void initializeBoot(){
        boolean isAutoBoot = getIntent().getBooleanExtra(keys.IS_AUTO_BOOT, false);
        if(isAutoBoot){
            moveTaskToBack(true);
        }
    }

    public void initializeVPN(){
        m_proxy_controller.onStartVPN();
        boolean isAutoBoot = getIntent().getBooleanExtra(keys.IS_AUTO_BOOT, false);
        if(isAutoBoot && status.AUTO_START){
           m_proxy_controller.autoBoot();
        }
    }

    public void initializeCrashHandler(){
        if(!status.DEVELOPER_MODE){
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
    }

    public void initializePluginManager()
    {
        m_speed_base.setTranslationZ(35);

        pluginManager.getInstance().onPreferenceTrigger(Collections.singletonList(this), enums.PREFERENCES_ETYPE.INITIALIZE);
        pluginManager.getInstance().onAdvertTrigger(Arrays.asList(this, m_banner_ads), enums.AD_ETYPE.INITIALIZE);
        pluginManager.getInstance().onAnalyticsTrigger(Collections.singletonList(this), enums.ANALYTIC_ETYPE.INITIALIZE);

        m_speed_base.setTranslationZ(35);
        status.AUTO_CONNECT = (boolean)pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.AUTO_CONNECT, false), enums.PREFERENCES_ETYPE.GET_BOOL);
        status.AUTO_OPTIMAL_LOCATION = (boolean)pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.AUTO_OPTIMAL_LOCATION, true), enums.PREFERENCES_ETYPE.GET_BOOL);
        status.AUTO_START = (boolean)pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.AUTO_START, false), enums.PREFERENCES_ETYPE.GET_BOOL);
        status.DISABLED_APPS = (ArrayList<String>)pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.DISABLED_APPS, null), enums.PREFERENCES_ETYPE.GET_SET);
        status.DEFAULT_SERVER = (String) pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.DEFAULT_SERVER, ""), enums.PREFERENCES_ETYPE.GET_STRING);

        m_view_controller = new homeViewController(new homeViewCallback(),m_connect_base, m_connect_animator, m_connect_loading, m_flag /*, m_location_info*/, m_connect_label, m_alert_dialog,m_alert_title, m_alert_description, this, m_download_speed, m_upload_speed, m_connection_toggle, m_stop_alert_btn, m_drawer, m_speed_base, m_connect_animator_initial, m_dismiss_alert_btn, m_fragment_container, m_blocker);

        if(!status.AUTO_CONNECT){
            onLoadAdvert();
        }

        m_speed_base.setTranslationZ(35);
    }

    public void initializeModel(){
        new Thread(){
            public void run(){
                constants.INSTALLED_APPS.clear();
                constants.SYSTEM_APPS.clear();
                constants.STARRED_APPS.clear();
                constants.INSTALLED_APPS = helperMethods.getUserInstalledApps(homeController.this);
                constants.SYSTEM_APPS = helperMethods.getSystemInstalledApps(homeController.this);
                constants.STARRED_APPS = helperMethods.getStarredApps(homeController.this);
            }
        }.start();
        sharedControllerManager.getInstance().setHomeController(this);
    }

    public void initializeViews(){
        m_connect_base = findViewById(R.id.connect_base);
        m_connect_animator = findViewById(R.id.connect_animator);
        m_connect_loading = findViewById(R.id.loading);
        m_flag = findViewById(R.id.icon);
        m_connect_label = findViewById(R.id.connect_label);
        m_stop_alert_btn = findViewById(R.id.m_stop_alert_btn);
        m_alert_dialog = findViewById(R.id.home_alert_dialog);
        m_alert_title = findViewById(R.id.home_alert_title);
        m_alert_description = findViewById(R.id.home_alert_description);
        m_banner_ads = findViewById(R.id.banner_ads);
        m_download_speed = findViewById(R.id.p_download);
        m_upload_speed = findViewById(R.id.p_upload);
        m_connection_toggle = findViewById(R.id.m_connection_toggle);
        m_drawer = findViewById(R.id.drawer_layout);
        m_speed_base = findViewById(R.id.m_speed_base);
        m_connect_animator_initial = findViewById(R.id.connect_animator_initial);
        m_dismiss_alert_btn = findViewById(R.id.m_dismiss_alert_btn);
        m_fragment_container = findViewById(R.id.m_fragment_container);
        m_blocker = findViewById(R.id.m_blocker);

        m_model = new homeModel(this, new homeModelCallback());
    }

    public void initializeFragment(){
        helperMethods.openFragment(m_fragment_container, new aboutController(), this, false);
        helperMethods.openFragment(m_fragment_container, new appController(), this,false);
        helperMethods.openFragment(m_fragment_container, new promotionController(), this, false);
        helperMethods.openFragment(m_fragment_container, new settingController(), this, false);
   }

   public void initializeServerModel(){
        if(m_fragment_container.getVisibility() != View.VISIBLE){
            helperMethods.openFragment(m_fragment_container, new serverController(), this,  false);
        }
   }

    /*EVENT HANDLERS DEFAULTS*/

    private PopupWindow popupWindow = null;
    public void onOptionMenu(View view){
        if(popupWindow!=null){
            popupWindow.dismiss();
        }
        LayoutInflater layoutInflater
                = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.home_menu, null);


        popupWindow = new PopupWindow(
                popupView,
                ActionMenuView.LayoutParams.WRAP_CONTENT,
                ActionMenuView.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        View parent = view.getRootView();
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.showAtLocation(parent, Gravity.TOP|Gravity.END,(int)helperMethods.pxToDp(this,0),(int)helperMethods.pxToDp(this,70));
    }

    public void onOpenDrawer(View view){
        m_view_controller.onOpenDrawer();
    }

    public void onCloseDrawer(View view){
        m_view_controller.onCloseDrawer();
    }

    public void onConnectionInfo(View view){
        String m_text = m_connection_toggle.getText().toString();
        if(m_text.equals("TCP")){
            m_view_controller.onShowAlert(strings.HO_CONNECTION_TCP, strings.HO_CONNECTION_TITLE_TCP, true, false);
        }else if(m_text.equals("UDP")){
            m_view_controller.onShowAlert(strings.HO_CONNECTION_UDP, strings.HO_CONNECTION_TITLE_UDP, true, false);
        }else {
            m_view_controller.onShowAlert(strings.HO_CONNECTION_AUTO, strings.HO_CONNECTION_TITLE_AUTO, true, false);
        }
    }

    public void onPopupDismiss(){
        if(popupWindow!=null){
            popupWindow.dismiss();
        }
        m_view_controller.onCloseDrawer();
    }

    public void onConnectionToggle(View view) {
        String m_text = ((TextView) view).getText().toString();
        if(m_text.equals("TCP")){
            m_view_controller.onConnectionToggle(2);
            status.CONNECTION_TYPE = 2;
            pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.CONNECTION_TYPE, status.CONNECTION_TYPE), enums.PREFERENCES_ETYPE.SET_INT);
            m_proxy_controller.onSettingChanged(false);
        }else if(m_text.equals("UDP")){
            m_view_controller.onConnectionToggle(1);
            status.CONNECTION_TYPE = 1;
            pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.CONNECTION_TYPE, status.CONNECTION_TYPE), enums.PREFERENCES_ETYPE.SET_INT);
            m_proxy_controller.onSettingChanged(false);
        }else {
            m_view_controller.onConnectionToggle(0);
            status.CONNECTION_TYPE = 0;
            pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.CONNECTION_TYPE, status.CONNECTION_TYPE), enums.PREFERENCES_ETYPE.SET_INT);
            m_proxy_controller.onSettingChanged(false);
        }
        List<Fragment> m_fragments = getSupportFragmentManager().getFragments();
        if(m_fragments.get(0).getClass().getName().endsWith(settingController.class.getName())){
            sharedControllerManager.getInstance().getSettingController().initialize();
        }
        m_view_controller.onAlertDismiss();
        onPopupDismiss();
    }

    public void onAlertDismiss(View view) {
        m_view_controller.onAlertDismiss();
    }

    public void onAlertDismissProxy(View view) {
        m_view_controller.onAlertDismissProxy();
    }

    public void onAlertStop(View view) {
        onPopupDismiss();
        m_view_controller.onAlertDismiss();
        m_proxy_controller.onForceStop();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count < 1 && getSupportFragmentManager().getFragments().size()==0 || m_fragment_container.getVisibility() == View.INVISIBLE) {
            if(m_drawer.isDrawerOpen(GravityCompat.START)){
                m_view_controller.onCloseDrawer();
            }
            else{
                if(m_view_controller.isAlertViewShwoing()){
                    m_view_controller.onAlertDismiss();
                }
                else {
                    this.moveTaskToBack(true);
                }
            }
        }
        else if(!m_blocker.isClickable()){
            List<Fragment> m_fragments = getSupportFragmentManager().getFragments();
            boolean m_close_triggered_status = false;

            if(count >= 1){
                if(getSupportFragmentManager().getBackStackEntryAt(0).getName().endsWith("serverController") || getSupportFragmentManager().getBackStackEntryAt(0).getName().endsWith("appController")){
                    if(getSupportFragmentManager().getBackStackEntryAt(0).getName().endsWith("appController"))
                    {
                        ((appController)m_fragments.get(1)).onBackPressed();
                    }
                    ((settingController)m_fragments.get(0)).onResumeFragment();
                    for(int count_frag=1;count_frag<m_fragments.size();count_frag++){
                        getSupportFragmentManager().popBackStack();
                        getSupportFragmentManager().beginTransaction().remove(m_fragments.get(count_frag)).commit();
                    }
                    return;
                }
            }else {
                if(m_fragments.get(0).getClass().getName().endsWith("settingController")){
                    m_close_triggered_status = ((settingController)m_fragments.get(0)).onBackPressed();
                }
                else if((m_fragments.get(0).getClass().getName().endsWith("promotionController"))){
                    m_close_triggered_status = ((promotionController)m_fragments.get(0)).onBackPressed();
                }
                else if((m_fragments.get(0).getClass().getName().endsWith("aboutController"))){
                    m_close_triggered_status = ((aboutController)m_fragments.get(0)).onBackPressed();
                }
                else if((m_fragments.get(0).getClass().getName().endsWith("appController"))){
                    m_close_triggered_status = ((appController)m_fragments.get(0)).onBackPressed();
                }
                else if((m_fragments.get(0).getClass().getName().endsWith("serverController"))){
                    m_close_triggered_status = ((serverController)m_fragments.get(0)).onBackPressed();
                }
                if(m_close_triggered_status){
                    m_fragment_container.bringToFront();
                    m_view_controller.onCloseFragment();
                }
                for(int count_frag=1;count_frag<m_fragments.size();count_frag++){
                    getSupportFragmentManager().beginTransaction().remove(m_fragments.get(count_frag)).commit();
                    getSupportFragmentManager().popBackStackImmediate();
                }
            }
            m_model.onResetUIBlock();
            onResume();
        }
    }

    public boolean isFragmentVisible(String p_fragment_name){
        List<Fragment> m_fragments = getSupportFragmentManager().getFragments();
        if(m_fragments.get(0).getClass().getName().endsWith(p_fragment_name) && m_fragment_container.getVisibility()==View.VISIBLE){
            return true;
        }else {
            return false;
        }
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
            m_model.onServer(500, m_proxy_controller.isUserRegistered(), m_fragment_container);
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
           if(m_view_controller!=null){
               m_view_controller.onConnectionUpdate();
           }
        }
        if(m_model!=null){
            m_model.onResetUIBlock();
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
        if (id == R.id.action_server) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onNavigationItem(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_app)
        {
            onAppManager(700);
        }
        else if (id == R.id.speed_test)
        {
            m_model.onSpeedTest(500);
        }
        else if (id == R.id.nav_ip_address)
        {
            m_model.onLocation(500);
        }
        else if (id == R.id.nav_promotion)
        {
            onPromotionOpen(500);
        }
        else if (id == R.id.nav_about)
        {
            m_view_controller.onAlertDismiss();
            onPopupDismiss();
            m_model.onAboutUS(500,m_fragment_container);
        }
        else if (id == R.id.server)
        {
            m_proxy_controller.clearExceptionCounter();
            m_model.onServer(700,m_proxy_controller.isUserRegistered(), m_fragment_container);
        }
        else if (id == R.id.setting)
        {
            onSettingManagerOpen(500);
        }
        else if (id == R.id.nav_share)
        {
            m_view_controller.onAlertDismiss();
            onPopupDismiss();
            m_model.onShare();
        }
        else if (id == R.id.nav_help)
        {
            m_view_controller.onAlertDismiss();
            onPopupDismiss();
            m_model.onContactUS();
        }
        else if (id == R.id.nav_rate)
        {
            m_view_controller.onAlertDismiss();
            onPopupDismiss();
            m_model.onRateUs();
        }
        else if (id == R.id.ic_menu_privacy)
        {
            m_view_controller.onAlertDismiss();
            onPopupDismiss();
            m_model.onPrivacyPolicy();
        }
        else if (id == R.id.nav_quit)
        {
            m_proxy_controller.onCloseSmooth();
        }
        onCloseDrawer(null);
    }

    /*EVENT HANDLERS OVERRIDE*/

    public void onPromotionOpen(int p_delay){
        m_view_controller.onAlertDismiss();
        onPopupDismiss();
        m_model.onPromotion(p_delay, m_fragment_container);
    }

    public void onStartBeatAnimation(){
        m_view_controller.onStartBeatAnimation();
    }

    public void onSettingManagerClick(View view){
        m_view_controller.onAlertDismiss();
        onPopupDismiss();
        m_model.onSettings(500, m_fragment_container);
    }

    public void onStart(View view)
    {
        m_connect_base.setClickable(false);
        new Handler().postDelayed(() -> m_connect_base.setClickable(true), 250);

        m_proxy_controller.onTriggered(TRIGGER.TOOGLE);
    }

    public void onSpeedClick(View view)
    {
        m_view_controller.onSpeedClick();
    }

    public void onServer(MenuItem item){
        m_view_controller.onAlertDismiss();
        onPopupDismiss();
        m_proxy_controller.clearExceptionCounter();
        m_model.onServer(500,m_proxy_controller.isUserRegistered(), m_fragment_container);
    }

    public void onChooseServer(String p_server){
        m_proxy_controller.onChooseServer(p_server);
    }

    public void onServer(View view) {
        m_view_controller.onAlertDismiss();
        m_proxy_controller.clearExceptionCounter();
        m_model.onServer(500,m_proxy_controller.isUserRegistered(), m_fragment_container);
        onPopupDismiss();
    }

    public void onSettingManagerOpen(int delay) {
        onPopupDismiss();
        m_model.onSettings(delay, m_fragment_container);
    }

    public void onSettingManager(View view) {
        onSettingManagerOpen(0);
    }


    public void onAppManager(int delay) {
        onPopupDismiss();
        m_view_controller.onAlertDismiss();
        if(constants.SYSTEM_APPS.size()>0){
            m_model.onAppManager(delay, m_fragment_container);
        }
    }

    public void onAppManager(View view) {
        onAppManager(0);
    }

    /*EVENT VIEW CALLBACK HANDLER*/

    public class homeModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type == enums.ETYPE.HOME_ALERT){
                String m_message = p_data.get(0).toString();
                boolean isStopButtonActive = true;
                if(m_message.equals(strings.SE_SERVER_MESSAGE_DESC)){
                    isStopButtonActive = false;
                    m_message = "" + p_data.get(0);
                }else {
                    m_message = strings.HO_ERROR_OCCURED + m_message;
                }

                boolean finalIsStopButtonActive = isStopButtonActive;
                String finalM_message = m_message;
                new Handler().postDelayed(() -> m_view_controller.onShowAlert(finalM_message,(String) p_data.get(1), true, finalIsStopButtonActive),(long) p_data.get(2));
            }
            else if(p_event_type == enums.ETYPE.OPEN_FRAGMENT){
                boolean m_Response = (boolean)p_data.get(0);
                if(m_Response)
                    m_view_controller.onOpenFragment(500);
                else
                    m_view_controller.onOpenFragment(0);

            }
        }
    }

    public class homeViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type == enums.ETYPE.SHOW_ADVERT){
                pluginManager.getInstance().onAdvertTrigger(Arrays.asList(this, m_banner_ads), enums.AD_ETYPE.SHOW_ADVERT);
            }
        }
    }


    /*EVENT VIEW REDIRECTIONS*/

    public void onShowAlert(String p_error, boolean p_is_forced){
        m_view_controller.onShowAlert(strings.HO_ERROR_OCCURED + p_error, "Request Failure", p_is_forced, true);
    }

    public void onLoadAdvert(){
        m_view_controller.onStopBeatAnimation();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pluginManager.getInstance().onAdvertTrigger(Arrays.asList(this, m_banner_ads), enums.AD_ETYPE.SHOW_ADVERT);
            }
        }, 500);
    }

    public void onConnected()
    {
        m_view_controller.onConnected();
        onLoadAdvert();
    }

    public void onIdle()
    {
        m_view_controller.onIdle();
    }

    public void onConnecting()
    {
        m_view_controller.onConnecting();
    }

    public void onAutoConnect()
    {
        m_view_controller.onAutoConnect();
    }

    public void onDisconnecting()
    {
        m_view_controller.onDisconnecting();
    }

    public void onSetFlag(String location)
    {
        m_view_controller.onSetFlag(location);
    }

    public void onSetFlagInstant(String location)
    {
        m_view_controller.onClearFlagInstant();
    }

    public void onUpdateDownloadSpeed(float val){
        m_view_controller.onUpdateDownloadSpeed(val);
    }

    public void onUpdateUploadSpeed(float val){
        m_view_controller.onUpdateUploadSpeed(val);
    }

}

