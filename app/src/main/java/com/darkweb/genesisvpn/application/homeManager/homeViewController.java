package com.darkweb.genesisvpn.application.homeManager;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.messages;
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

    /*LOCAL VARIABLE DECLARATION*/

    private enums.vpn_status m_buttonStatus = enums.vpn_status.IDLE;
    private Handler m_updateUIHandler = null;
    private String m_flag_status = "";

    /*INITIALIZATIONS*/

    homeViewController(Button p_connect_base, Button p_connect_animator, ImageView p_connect_loading, ImageButton p_flag, TextView p_location_info,AppCompatActivity p_context)
    {
        this.m_connect_base = p_connect_base;
        this.m_connect_animator = p_connect_animator;
        this.m_connect_loading = p_connect_loading;
        this.m_location_info = p_location_info;
        this.m_flag = p_flag;
        this.m_context = p_context;

        initializeConnectStyles();
    }

    /*HELPER METHODS*/

    private void initializeConnectStyles(){

        /*POST UI HANDLER TASKS*/
        createUpdateUiHandler();

        /*FONTS*/
        m_context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        int size = helperMethods.screenWidth()*60/100;

        Typeface custom_font = Typeface.createFromAsset(m_context.getAssets(),  "fonts/gotham_med.ttf");
        m_connect_base.setTypeface(custom_font);

        /*WIDTH WITH SCREEN*/

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) m_connect_base.getLayoutParams();
        lp.width = size;
        lp.height = size;

        m_connect_base.setLayoutParams(lp);
        m_connect_animator.setLayoutParams(lp);
        m_connect_loading.setLayoutParams(lp);

        Handler handler = new Handler();
        handler.post(() -> {
            homeAnimation.getInstance().beatAnimation(m_connect_animator);
            homeAnimation.getInstance().rotateAnimation(m_connect_loading);
        });

        m_connect_base.setText(strings.goText);
        ViewCompat.setTranslationZ(m_connect_loading, 15);
        m_connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.2f);
        m_connect_loading.setAlpha(0f);
        m_flag.setAlpha(0f);

        ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams) m_flag.getLayoutParams();
        lp1.width = helperMethods.screenWidth()*19/100;
        lp1.height = helperMethods.screenWidth()*14/100;
        m_flag.setLayoutParams(lp1);
        m_location_info.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.035f);
        setStatusBarColor();

    }

    void setStatusBarColor(){
        Window window = m_context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(m_context.getResources().getColor(R.color.colorPrimary));
    }

    /*EVENT TO VIEW HANDLERS*/

    void onStopping()
    {
        if(m_buttonStatus != enums.vpn_status.DISCONNECTING) {
            m_buttonStatus = enums.vpn_status.DISCONNECTING;
            m_connect_loading.animate().alpha(1);
            m_connect_base.setText(strings.stopingText);
            m_connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * 0.065f);
        }
    }


    void onConnected()
    {
        if(m_buttonStatus != enums.vpn_status.CONNECTED) {
            m_connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * 0.065f);
            m_connect_base.setText(strings.connectedText);
            m_connect_loading.animate().alpha(0);
            m_buttonStatus = enums.vpn_status.CONNECTED;
        }
    }

    void onStopped()
    {
        if(m_buttonStatus != enums.vpn_status.IDLE) {
            m_connect_base.setText(strings.goText);
            m_connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.2f);
            m_connect_loading.animate().alpha(0);
            m_buttonStatus = enums.vpn_status.IDLE;
        }
    }

    void onConnecting()
    {
        m_connect_base.setText(strings.connectingText);
        m_connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX,helperMethods.screenWidth()*0.065f);
        m_connect_loading.animate().alpha(1);
        m_buttonStatus = enums.vpn_status.CONNECTING_VPN;
    }

    void onSetFlag(String location)
    {
        if(!location.equals(strings.emptySTR))
        {
            m_flag_status = location.toLowerCase();
            startPostTask(messages.UPDATE_FLAG);
        }
    }

    void onHideFlag()
    {
        startPostTask(messages.UPDATE_NO_FLAG);
        m_location_info.setText(strings.unconnected);
    }

    /*ANIMATION VIEW REDIRECTIONS*/


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
                    if(!m_flag_status.equals(strings.emptySTR))
                    {
                        if(m_flag.getAlpha()==0f)
                        {
                            m_flag.animate().alpha(1);
                        }

                        Locale obj = new Locale("", m_flag_status);

                        m_flag.setBackground(FlagKit.drawableWithFlag(m_context, m_flag_status));
                        m_location_info.setText(strings.connectedTo + obj.getDisplayCountry());
                    }
                }
                else if(msg.what == messages.UPDATE_NO_FLAG)
                {
                    if(m_flag.getAlpha()==0f)
                    {
                        m_flag.animate().alpha(1);
                    }

                    Locale obj = new Locale("", m_flag_status);

                    m_flag.setBackground(ContextCompat.getDrawable(m_context, R.drawable.noneflag));
                }
            }
        };
    }

}
