package com.darkweb.genesisvpn.application.homeManager;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.messages;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.jwang123.flagkit.FlagKit;

import java.util.Locale;

class homeViewController {

    /*LOCAL VIEW DECLARATION*/
    private Button m_connect_base;
    private Button m_connect_animator;
    private Button m_connection_toggle;
    private Button m_stop_alert_btn;
    private Button m_connect_animator_initial;
    private Button m_dismiss_alert_btn;
    private FrameLayout m_fragment_container;

    private ImageView m_ui_smoothner;
    private ImageView m_connect_loading;
    private ImageView m_speed_base;
    private ImageView m_blocker;
    private ImageButton m_flag;

    private FragmentActivity m_context;
    private TextView m_connect_label;
    private String m_connect_text_local = strings.HO_IDLE;
    private ConstraintLayout m_alert_dialog;
    private DrawerLayout m_drawer;

    private TextView m_alert_title;
    private TextView m_alert_description;
    private TextView m_download_speed;
    private TextView m_upload_speed;


    /*LOCAL VARIABLE DECLARATION*/

    private connectAnimation m_connect_animations;
    private connectAnimation m_connect_beat;
    private Handler m_updateUIHandler = null;
    private String m_flag_status = strings.EMPTY_STR;
    private float m_text_size = 0;
    private boolean isFlagRemoved = true;
    private eventObserver.eventListener m_event;
    private boolean isForcedAlert = false;
    private String m_current_flag = strings.EMPTY_STR;

    /*INITIALIZATIONS*/

    homeViewController(eventObserver.eventListener p_event, Button p_connect_base, Button p_connect_animator, ImageView p_connect_loading, ImageButton p_flag, TextView p_connect_label, ConstraintLayout p_alert_dialog, TextView p_alert_title, TextView p_alert_description, FragmentActivity p_context, TextView p_download_speed, TextView p_upload_speed, Button p_connection_toggle, Button p_stop_alert_btn, DrawerLayout p_drawer, ImageView p_speed_base, Button p_connect_animator_initial, Button p_dismiss_alert_btn, FrameLayout p_fragment_container, ImageView p_blocker, ImageView p_ui_smoothner)
    {
        this.m_event = p_event;
        this.m_connect_base = p_connect_base;
        this.m_connect_animator = p_connect_animator;
        this.m_connect_loading = p_connect_loading;
        this.m_flag = p_flag;
        this.m_connect_label = p_connect_label;
        this.m_context = p_context;
        this.m_alert_dialog = p_alert_dialog;
        this.m_alert_title = p_alert_title;
        this.m_alert_description = p_alert_description;
        this.m_download_speed = p_download_speed;
        this.m_upload_speed = p_upload_speed;
        this.m_connection_toggle = p_connection_toggle;
        this.m_stop_alert_btn = p_stop_alert_btn;
        this.m_drawer = p_drawer;
        this.m_speed_base = p_speed_base;
        this.m_connect_animator_initial = p_connect_animator_initial;
        this.m_dismiss_alert_btn = p_dismiss_alert_btn;
        this.m_fragment_container = p_fragment_container;
        this.m_blocker = p_blocker;
        this.m_ui_smoothner = p_ui_smoothner;

        m_speed_base.setTranslationZ(35);
        m_connect_animations = new connectAnimation();
        m_connect_beat = new connectAnimation();
        m_speed_base.setTranslationZ(35);

        initializeConnectStyles();
    }

    /*HELPER METHODS*/

    private void initializeConnectStyles(){

        /*POST UI HANDLER TASKS*/
        createUpdateUiHandler();

        /*FONTS*/
        int size = helperMethods.screenWidth()*70/100;

        Typeface custom_font = Typeface.createFromAsset(m_context.getAssets(),  strings.HO_FONT_GOTHAM);
        m_connect_label.setTypeface(custom_font);

        /*WIDTH WITH SCREEN*/
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) m_speed_base.getLayoutParams();

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) m_connect_base.getLayoutParams();
        lp.width = size;
        lp.height = size;
        params.leftMargin = (int)(size/1.6); params.topMargin = (int)(size/1.6);
        m_speed_base.setLayoutParams(params);

        m_connect_base.setLayoutParams(lp);
        m_connect_animator.setLayoutParams(lp);
        m_connect_animator_initial.setLayoutParams(lp);
        m_connect_loading.setLayoutParams(lp);
        m_connect_animator.setAlpha(1);
        m_connect_animator_initial.setAlpha(1);

        m_connect_animator.setTransitionName(strings.HO_TRANSITION_NAME_START);
        m_connect_animator_initial.setTransitionName(strings.HO_TRANSITION_NAME_START);
        m_connect_animations.rotateAnimation(m_connect_loading, m_speed_base);

        if(status.AUTO_CONNECT){
            m_connect_label.setText(strings.HO_CONNECTING);
            m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.075f);
            m_connect_text_local = strings.HO_CONNECTING;
            m_connect_loading.animate().cancel();
            m_connect_loading.animate().setDuration(500).withStartAction(() -> {
                m_connect_loading.animate().setStartDelay(0);
                ViewCompat.setTranslationZ(m_speed_base, 35);
                m_speed_base.setTranslationZ(35);
            }).withEndAction(() -> m_speed_base.setTranslationZ(35)).alpha(1);
        }
        else {
            m_connect_label.setText(strings.HO_IDLE);
            m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.2f);
            m_connect_text_local = strings.HO_IDLE;
            m_connect_loading.animate().cancel();
            m_connect_loading.animate().setDuration(0).alpha(0);
        }
        m_flag.setAlpha(1f);

        ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams) m_flag.getLayoutParams();
        lp1.width = helperMethods.screenWidth()*19/100;
        lp1.height = helperMethods.screenWidth()*14/100;
        m_flag.setLayoutParams(lp1);
        // m_location_info.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.035f);
        m_flag.setImageDrawable(ContextCompat.getDrawable(m_context, R.drawable.no_flag_default));
        setStatusBarColor();
        m_alert_dialog.setAlpha(0);
        m_alert_dialog.setVisibility(View.GONE);
        m_alert_title.setTypeface(null, Typeface.BOLD);
        if(status.CONNECTION_TYPE == 1){
            m_connection_toggle.setText("TCP");
        }else if(status.CONNECTION_TYPE == 0){
            m_connection_toggle.setText("UDP");
        }else {
            m_connection_toggle.setText("AUTO");
        }

        new Handler().postDelayed(() -> m_connect_base.setClickable(true), 500);

    }

    void setStatusBarColor(){
        Window window = m_context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(m_context.getResources().getColor(R.color.colorPrimary));
    }

    /*EVENT TO VIEW HANDLERS*/

    void onDisconnecting()
    {
        m_speed_base.setTranslationZ(35);
        if(!m_connect_text_local.equals(strings.HO_DISCONNECTING) && !status.HAS_APPLICATION_STOPPED) {
            m_connect_text_local = strings.HO_DISCONNECTING;
            m_text_size = 0.075f;
            m_context.runOnUiThread(() -> {
                m_connect_loading.animate().cancel();
                m_connect_loading.animate().alpha(1);
                animateConnectText();
                onSetFlag(strings.EMPTY_STR);
            });
        }
    }

    public void onStopBeatAnimation(){
    }

    public void onSpeedClick(){
        m_connect_base.performClick();
        m_connect_base.setPressed(true);
        m_connect_base.setPressed(false);
    }

    public void onStartBeatAnimation(){
        m_connect_beat.beatAnimation(m_connect_animator_initial);
        m_speed_base.setTranslationZ(1140);
    }

    void onConnected()
    {
        m_speed_base.setTranslationZ(35);
        if(!m_connect_text_local.equals(strings.HO_CONNECTED) && !status.HAS_APPLICATION_STOPPED) {
            m_connect_text_local = strings.HO_CONNECTED;
            m_text_size = 0.080f;
            m_context.runOnUiThread(() -> {
                m_connect_loading.animate().cancel();
                m_connect_loading.animate().alpha(0);
                animateConnectText();
            });
        }
    }

    void onIdle()
    {
        m_speed_base.setTranslationZ(35);

        if(!m_connect_text_local.equals(strings.HO_IDLE) && !status.HAS_APPLICATION_STOPPED) {
            m_connect_text_local = strings.HO_IDLE;
            m_text_size = 0.2f;
            m_context.runOnUiThread(() -> {
                m_connect_loading.animate().cancel();
                m_connect_loading.animate().alpha(0);
                animateConnectText();
            });
        }
    }

    public void onAutoConnect()
    {
        m_speed_base.setTranslationZ(35);
        //m_connect_base.performClick();
        m_speed_base.setTranslationZ(125);
    }

    void onConnecting()
    {
        m_speed_base.setTranslationZ(35);
        if(!m_connect_text_local.equals(strings.HO_CONNECTING) && !status.HAS_APPLICATION_STOPPED){
            m_connect_text_local = strings.HO_CONNECTING;
            m_text_size = 0.075f;
            m_context.runOnUiThread(() -> {
                m_connect_loading.animate().cancel();
                m_connect_loading.animate().alpha(1);
                onSetFlag(strings.EMPTY_STR);
                animateConnectText();
            });
        }
    }

    public void onOpenDrawer(){
        m_drawer.setEnabled(true);
        m_drawer.setClickable(true);
        m_drawer.openDrawer(Gravity.LEFT); //Edit Gravity.START need API 14
    }

    public void onCloseDrawer(){
        if(m_drawer!=null){
            m_drawer.closeDrawer(Gravity.LEFT); //Edit Gravity.START need API 14
        }
    }

    public void onConnectionToggle(int p_connection){
        if(p_connection == 1){
            m_connection_toggle.setText("TCP");
        }else if(p_connection == 0){
            m_connection_toggle.setText("UDP");
        }else {
            m_connection_toggle.setText("AUTO");
        }
    }

    public void onConnectionUpdate(){
        if(m_connection_toggle!=null){
            if(status.CONNECTION_TYPE == 1){
                m_connection_toggle.setText("TCP");
            }else if(status.CONNECTION_TYPE == 0){
                m_connection_toggle.setText("UDP");
            }else {
                m_connection_toggle.setText("AUTO");
            }
        }
    }

    void onSetFlag(String location)
    {
        if(!location.equals(strings.EMPTY_STR))
        {
            m_flag_status = location.toLowerCase();
            startPostTask(messages.UPDATE_FLAG);
        }else {
            startPostTask(messages.REMOVE_FLAG);
        }
    }

    public void onClearFlagInstant()
    {
        startPostTask(messages.REMOVE_FLAG_INSTANT);
    }

    public void onShowAlert(String p_error_message,String p_error_title, boolean p_is_forced, boolean isStopButtonActive){

        m_context.runOnUiThread(() -> {
            String m_error = p_error_message;
            if(p_is_forced || !m_alert_description.getText().equals(m_error) && !m_alert_title.getText().equals(strings.AF_NO_APPLICATION_HEADER)){
                Drawable m_background;
                if(isStopButtonActive){
                    m_background = helperMethods.XMLTODrawable(m_context, R.xml.sc_rounded_corner_positive);
                    m_stop_alert_btn.setVisibility(View.VISIBLE);
                }else {
                    m_background = helperMethods.XMLTODrawable(m_context, R.xml.sc_rounded_corner);
                    m_stop_alert_btn.setVisibility(View.INVISIBLE);
                }
                if(m_background!=null){
                    m_dismiss_alert_btn.setBackground(m_background);
                }
                isForcedAlert = p_is_forced;
                m_context.runOnUiThread(() -> {
                    if(m_alert_dialog.getAlpha()<1){
                        m_alert_dialog.animate().cancel();
                        m_alert_dialog.setAlpha(0);
                    }else {
                        m_alert_description.animate().cancel();
                        m_alert_title.animate().cancel();
                        m_alert_description.setAlpha(0.0f);
                        m_alert_title.setAlpha(0.0f);
                        m_alert_description.animate().alpha(1);
                        m_alert_title.animate().alpha(1);

                        m_alert_description.setText(m_error);
                        m_alert_title.setText(p_error_title);
                        return;
                    }

                    m_alert_dialog.animate().setDuration(200).alpha(1).withEndAction(() -> {
                        m_alert_dialog.setClickable(true);
                        ((ConstraintLayout)m_alert_dialog.getChildAt(0)).getChildAt(1).setClickable(true);
                        ((ConstraintLayout)m_alert_dialog.getChildAt(0)).getChildAt(2).setClickable(true);
                    });
                    m_alert_description.setText(m_error);
                    m_alert_title.setText(p_error_title);
                    m_alert_dialog.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    public void onAlertDismissProxy() {
        if(!isForcedAlert){
            onAlertDismiss();
        }
    }

    public void onAlertDismiss() {
        m_context.runOnUiThread(() -> {
            m_alert_dialog.animate().cancel();
            m_alert_dialog.setClickable(false);
            ((ConstraintLayout)m_alert_dialog.getChildAt(0)).getChildAt(1).setClickable(false);
            ((ConstraintLayout)m_alert_dialog.getChildAt(0)).getChildAt(2).setClickable(false);
            m_alert_dialog.animate().setDuration(200).alpha(0).withEndAction(() -> {
                m_alert_dialog.setVisibility(View.GONE);
                m_alert_description.setText("");
                m_alert_title.setText("");
            });
        });
    }

    public boolean isAlertViewShwoing(){
        return m_alert_dialog.getAlpha() > 0;
    }

    void onUpdateDownloadSpeed(float val){
        if(val<0){
            val = 0;
        }
        val = val / 1000;
        if(val<1000){
            m_download_speed.setText(String.format("%.2f", val) + " / ↓ kbps");
        }else {
            val = val/1000;
            m_download_speed.setText(String.format("%.2f", val) + " / ↓ mbps");
        }
    }

    void onUpdateUploadSpeed(float val){
        if(val<0){
            val = 0;
        }
        val = val / 1000;
        if(val<1000){
            m_upload_speed.setText(String.format("%.2f", val) + " / ↑ kbps");
        }else {
            val = val/1000;
            m_upload_speed.setText(String.format("%.2f", val) + " / ↑ mbps");
        }
    }

    /*POST UI TASKS*/

    public void startPostTask(int m_id)
    {
        Message message = new Message();
        message.what = m_id;
        m_updateUIHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler()
    {
        m_updateUIHandler = new Handler()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == messages.UPDATE_FLAG)
                {

                    Log.i("sppp","sppp:1:" + m_flag_status);
                    isFlagRemoved = false;
                    if(!m_flag_status.equals(strings.EMPTY_STR))
                    {
                        if(m_flag.getAlpha()==0f)
                        {
                            m_flag.animate().alpha(1);
                        }
                        if(!m_current_flag.equals(m_flag_status)){
                            m_current_flag = m_flag_status;
                            animateFlag(FlagKit.drawableWithFlag(m_context, m_flag_status));
                        }
                    }
                }
                else if(msg.what == messages.REMOVE_FLAG)
                {

                    Log.i("sppp","sppp:2:" + m_flag_status);
                    if(m_flag.getAlpha()==0f)
                    {
                        m_flag.animate().alpha(1);
                    }
                    if(!isFlagRemoved){
                        isFlagRemoved = true;

                        String m_prev_flag = R.drawable.no_flag_default+"";
                        if(!m_current_flag.equals(m_prev_flag)){
                            m_current_flag = m_prev_flag;
                            animateFlag(m_context.getResources().getDrawable(R.drawable.no_flag_default));
                        }
                    }
                }
                else if(msg.what == messages.REMOVE_FLAG_INSTANT)
                {

                    Log.i("sppp","sppp:3:" + m_flag_status);
                    if(m_flag.getAlpha()==0f)
                    {
                        m_flag.animate().setDuration(0).cancel();
                        m_flag.animate().setDuration(0).alpha(1);
                    }

                    String m_prev_flag = R.drawable.no_flag_default+"";
                    if(!m_current_flag.equals(m_prev_flag)){
                        m_current_flag = m_prev_flag;
                        isFlagRemoved = true;
                        m_flag.animate().cancel();
                        m_connect_label.animate().cancel();
                        m_connect_label.setAlpha(1);
                        m_flag.animate().cancel();
                        m_flag.animate().setDuration(0).alpha(1);
                        m_flag.setImageDrawable(m_context.getResources().getDrawable(R.drawable.no_flag_default));

                        if(m_connect_text_local.equals(strings.HO_DISCONNECTING)) {
                            m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * 0.075f);
                        }
                        else if(m_connect_text_local.equals(strings.HO_CONNECTED)) {
                            m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * 0.080f);
                        }
                        else if(m_connect_text_local.equals(strings.HO_IDLE)) {
                            m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * 0.2f);
                        }
                        else if(m_connect_text_local.equals(strings.HO_CONNECTING)){
                            m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * 0.075f);
                        }
                        m_connect_label.setText(m_connect_text_local);
                    }
                }
            }
        };
    }

    public void onStartFragmentAnimation(){
        m_ui_smoothner.animate().cancel();
        m_ui_smoothner.setAlpha(0);
        m_ui_smoothner.setVisibility(View.VISIBLE);
        m_ui_smoothner.animate().alpha(1);
    }

    public void onResetFragmentAnimation(){
        m_ui_smoothner.animate().cancel();
        m_ui_smoothner.animate().alpha(0).setDuration(200).withEndAction(() -> {
            m_ui_smoothner.setVisibility(View.INVISIBLE);
        }).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onOpenFragment(int p_delay){
        m_context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        m_blocker.setClickable(true);
        m_fragment_container.setAlpha(0);
        m_fragment_container.setVisibility(View.VISIBLE);
        m_fragment_container.animate().cancel();
        m_fragment_container.animate()
                .setDuration(150)
                .scaleY(1f)
                .scaleX(1f)
                .alpha(1)
                .setStartDelay(p_delay)
                .start();

        new Handler().postDelayed(() -> m_blocker.setClickable(false), 200);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onCloseFragment(){
        onResetFragmentAnimation();
        m_context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        m_context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        m_blocker.setClickable(true);
        m_fragment_container.setAlpha(1);
        m_fragment_container.setVisibility(View.VISIBLE);
        m_fragment_container.animate().cancel();
        m_fragment_container.animate()
                .setDuration(150)
                .scaleY(0.93f)
                .scaleX(0.93f)
                .alpha(0)
                .setStartDelay(0).withEndAction(() -> {
                    m_fragment_container.setVisibility(View.INVISIBLE);
                }).start();

        new Handler().postDelayed(() -> m_blocker.setClickable(false), 200);

    }

    public void animateFlag(Drawable p_flag){
        try{
            if(m_flag.getAlpha()>=0.7f){
                Log.i("FUZZ","FUZZ1");
                m_flag.animate().cancel();
                m_flag.animate().alpha(0).setDuration(150).withEndAction(() -> {
                    m_flag.animate().setDuration(350).alpha(1);
                    m_flag.setImageDrawable(p_flag);
                }).start();
            }else {
                Log.i("FUZZ","FUZZ2");
                m_flag.animate().cancel();
                m_flag.animate().alpha(0).setDuration(0).withEndAction(() -> {
                    m_flag.animate().setDuration(350).alpha(1).start();
                    m_flag.setImageDrawable(p_flag);
                }).start();
            }
        }catch (Exception ex){
        }
    }

    public void animateButtonClicked(MotionEvent pEvent){
        switch(pEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                m_connect_base.animate().alpha(0.5f).setInterpolator(new LinearInterpolator()).setDuration(100);
                m_connect_animator.setTransitionName(strings.HO_TRANSITION_NAME_PAUSE);
                //m_speed_base.setTranslationZ(140);
                break;

            case MotionEvent.ACTION_MOVE:
                //m_speed_base.setTranslationZ(140);
                break;

            case MotionEvent.ACTION_UP:
                m_connect_base.animate().alpha(1f).setInterpolator(new LinearInterpolator()).setDuration(100);
                m_connect_animator.setTransitionName(strings.HO_TRANSITION_NAME_START);
                //m_speed_base.setTranslationZ(140);
                break;
        }
    }

    void animateConnectText(){
        m_context.runOnUiThread(() -> {
            try{
                if(m_connect_label.getAlpha()>=0.7f){
                    m_connect_label.animate().cancel();
                    m_connect_label.animate().alpha(0).setDuration(200).withEndAction(() -> {
                        m_connect_label.setText(m_connect_text_local);
                        m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * m_text_size);
                        m_connect_label.animate().setDuration(200).alpha(1);
                    }).start();
                }else {
                    m_connect_label.animate().cancel();
                    m_connect_label.setAlpha(0);
                    m_connect_label.setText(m_connect_text_local);
                    m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * m_text_size);
                    m_connect_label.animate().setDuration(200).alpha(1).start();
                }
            }catch (Exception ex){
            }
        });
    }


}
