package com.darkweb.genesisvpn.application.homeManager;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.messages;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.jwang123.flagkit.FlagKit;
import java.util.Locale;

class homeViewController {

    /*LOCAL VIEW DECLARATION*/

    private Button m_connect_base;
    private Button m_connect_animator;
    private ImageView m_connect_loading;
    private TextView m_location_info;
    private ImageButton m_flag;
    private AppCompatActivity m_context;
    private TextView m_connect_label;
    private String m_text = strings.HO_IDLE;
    private ConstraintLayout m_alert_dialog;
    private TextView m_alert_title;
    private TextView m_alert_description;

    /*LOCAL VARIABLE DECLARATION*/

    private connectAnimation m_connect_animations;
    private Handler m_updateUIHandler = null;
    private String m_flag_status = strings.EMPTY_STR;
    private float m_text_size = 0;

    /*INITIALIZATIONS*/

    homeViewController(Button p_connect_base, Button p_connect_animator, ImageView p_connect_loading, ImageButton p_flag, TextView p_location_info, TextView p_connect_label, ConstraintLayout p_alert_dialog, TextView p_alert_title, TextView p_alert_description, AppCompatActivity p_context)
    {
        this.m_connect_base = p_connect_base;
        this.m_connect_animator = p_connect_animator;
        this.m_connect_loading = p_connect_loading;
        this.m_location_info = p_location_info;
        this.m_flag = p_flag;
        this.m_connect_label = p_connect_label;
        this.m_context = p_context;
        this.m_alert_dialog = p_alert_dialog;
        this.m_alert_title = p_alert_title;
        this.m_alert_description = p_alert_description;
        m_connect_animations = new connectAnimation();

        initializeConnectStyles();
    }

    /*HELPER METHODS*/

    private void initializeConnectStyles(){

        /*POST UI HANDLER TASKS*/
        createUpdateUiHandler();

        /*FONTS*/
        int size = helperMethods.screenWidth()*60/100;

        Typeface custom_font = Typeface.createFromAsset(m_context.getAssets(),  strings.HO_FONT_GOTHAM);
        m_connect_label.setTypeface(custom_font);

        /*WIDTH WITH SCREEN*/

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) m_connect_base.getLayoutParams();
        lp.width = size;
        lp.height = size;

        m_connect_base.setLayoutParams(lp);
        m_connect_animator.setLayoutParams(lp);
        m_connect_loading.setLayoutParams(lp);

        Handler handler = new Handler();
        handler.post(() -> {
            m_connect_animator.setTransitionName(strings.HO_TRANSITION_NAME_START);
            m_connect_animations.beatAnimation(m_connect_animator);
            m_connect_animations.rotateAnimation(m_connect_loading);
        });

        m_connect_label.setText(strings.HO_CONNECTING);
        ViewCompat.setTranslationZ(m_connect_loading, 15);
        m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.075f);
        m_flag.setAlpha(1f);

        ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams) m_flag.getLayoutParams();
        lp1.width = helperMethods.screenWidth()*19/100;
        lp1.height = helperMethods.screenWidth()*14/100;
        m_flag.setLayoutParams(lp1);
        m_location_info.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.035f);
        m_flag.setBackground(ContextCompat.getDrawable(m_context, R.drawable.noneflag));
        setStatusBarColor();
        m_alert_dialog.setAlpha(0);
        m_alert_dialog.setVisibility(View.GONE);
        m_alert_title.setTypeface(null, Typeface.BOLD);

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
        if(!m_text.equals(strings.HO_DISCONNECTING) && !status.HAS_APPLICATION_STOPPED) {
            m_text = strings.HO_DISCONNECTING;
            m_text_size = 0.075f;
            m_context.runOnUiThread(() -> {
                m_connect_loading.animate().cancel();
                m_connect_loading.animate().alpha(1);
                animateConnectText();
                onSetFlag(strings.EMPTY_STR);
            });
        }
    }


    void onConnected()
    {
        if(!m_text.equals(strings.HO_CONNECTED) && !status.HAS_APPLICATION_STOPPED) {
            m_text = strings.HO_CONNECTED;
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
        if(!m_text.equals(strings.HO_IDLE) && !status.HAS_APPLICATION_STOPPED) {
            m_text = strings.HO_IDLE;
            m_text_size = 0.2f;
            m_context.runOnUiThread(() -> {
                m_connect_loading.animate().cancel();
                m_connect_loading.animate().alpha(0);
                animateConnectText();
            });
        }
    }

    void onConnecting()
    {
        if(!m_text.equals(strings.HO_CONNECTING) && !status.HAS_APPLICATION_STOPPED){
            m_text = strings.HO_CONNECTING;
            m_text_size = 0.075f;
            m_context.runOnUiThread(() -> {
                m_connect_loading.animate().cancel();
                m_connect_loading.animate().alpha(1);
                onSetFlag(strings.EMPTY_STR);
                animateConnectText();
            });
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

    public void onShowAlert(String p_error_message,String p_error_title){
        String m_error = strings.HO_ERROR_OCCURED + p_error_message;
        if(!m_alert_description.getText().equals(m_error)){
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
    }

    public void onAlertDismiss() {
        m_context.runOnUiThread(() -> {
            m_alert_dialog.animate().cancel();
            m_alert_dialog.setClickable(false);
            ((ConstraintLayout)m_alert_dialog.getChildAt(0)).getChildAt(1).setClickable(false);
            ((ConstraintLayout)m_alert_dialog.getChildAt(0)).getChildAt(2).setClickable(false);
            m_alert_dialog.animate().setDuration(200).alpha(0).withEndAction(() -> m_alert_dialog.setVisibility(View.GONE));
        });
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
                    if(!m_flag_status.equals(strings.EMPTY_STR))
                    {
                        if(m_flag.getAlpha()==0f)
                        {
                            m_flag.animate().alpha(1);
                        }

                        Locale obj = new Locale(strings.EMPTY_STR, m_flag_status);
                        String location = strings.HO_CONNECTED_TO + obj.getDisplayCountry();

                        if(!location.equals(m_location_info.getText().toString())){
                            animateFlag(FlagKit.drawableWithFlag(m_context, m_flag_status));
                            m_location_info.setText(strings.HO_CONNECTED_TO + obj.getDisplayCountry());
                        }
                    }
                }
                else if(msg.what == messages.REMOVE_FLAG)
                {
                    if(m_flag.getAlpha()==0f)
                    {
                        m_flag.animate().alpha(1);
                    }

                    if(!strings.HO_UNCONNECTED.equals(m_location_info.getText().toString())){
                        animateFlag(m_context.getResources().getDrawable(R.drawable.noneflag));
                        m_location_info.setText(strings.HO_UNCONNECTED);
                    }
                }
            }
        };
    }

    public void animateFlag(Drawable p_flag){
        try{
            if(m_flag.getAlpha()>=0.7f){
                m_flag.animate().cancel();
                m_flag.animate().alpha(0).setDuration(150).withEndAction(() -> {
                    m_flag.animate().setDuration(350).alpha(1);
                    m_flag.setBackground(p_flag);
                }).start();
            }else {
                m_flag.animate().cancel();
                m_flag.setAlpha(0);
                m_flag.animate().setDuration(350).alpha(1).start();
                m_flag.setBackground(p_flag);
            }
        }catch (Exception ex){
        }
    }

    public void animateButtonClicked(MotionEvent pEvent){
        switch(pEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                m_connect_base.animate().alpha(0.5f).setInterpolator(new LinearInterpolator()).setDuration(100);
                m_connect_animator.setTransitionName(strings.HO_TRANSITION_NAME_PAUSE);
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                m_connect_base.animate().alpha(1f).setInterpolator(new LinearInterpolator()).setDuration(100);
                m_connect_animator.setTransitionName(strings.HO_TRANSITION_NAME_START);
                break;
        }
    }

    void animateConnectText(){
        m_context.runOnUiThread(() -> {
            try{
                if(m_connect_label.getAlpha()>=0.7f){
                    m_connect_label.animate().cancel();
                    m_connect_label.animate().alpha(0).setDuration(200).withEndAction(() -> {
                        m_connect_label.setText(m_text);
                        m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * m_text_size);
                        m_connect_label.animate().setDuration(200).alpha(1);
                    }).start();
                }else {
                    m_connect_label.animate().cancel();
                    m_connect_label.setAlpha(0);
                    m_connect_label.setText(m_text);
                    m_connect_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * m_text_size);
                    m_connect_label.animate().setDuration(200).alpha(1).start();
                }
            }catch (Exception ex){
            }
        });
    }


}
