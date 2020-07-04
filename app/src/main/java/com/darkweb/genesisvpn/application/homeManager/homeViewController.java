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
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private Button connect_base;
    private Button connect_animator;
    private ImageView connect_loading;
    private TextView location_info;
    private ImageButton flag;

    /*LOCAL VARIABLE DECLARATION*/

    private enums.vpn_status buttonStatus = enums.vpn_status.IDLE;
    private Handler updateUIHandler = null;
    private String flag_status = "";

    /*INITIALIZATIONS*/

    homeViewController(Button connect_base, Button connect_animator, ImageView connect_loading, ImageButton flag, TextView location_info)
    {
        this.connect_base = connect_base;
        this.connect_animator = connect_animator;
        this.connect_loading = connect_loading;
        this.location_info = location_info;
        this.flag = flag;

        initializeConnectStyles();
    }

    /*HELPER METHODS*/

    private void initializeConnectStyles(){

        /*POST UI HANDLER TASKS*/
        createUpdateUiHandler();

        /*FONTS*/
        homeModel.getInstance().getHomeInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        int size = helperMethods.screenWidth()*60/100;

        Typeface custom_font = Typeface.createFromAsset(homeModel.getInstance().getHomeInstance().getAssets(),  "fonts/gotham_med.ttf");
        connect_base.setTypeface(custom_font);

        /*WIDTH WITH SCREEN*/

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams)connect_base.getLayoutParams();
        lp.width = size;
        lp.height = size;

        connect_base.setLayoutParams(lp);
        connect_animator.setLayoutParams(lp);
        connect_loading.setLayoutParams(lp);

        Handler handler = new Handler();
        handler.post(() -> {
            homeAnimation.getInstance().beatAnimation(connect_animator);
            homeAnimation.getInstance().rotateAnimation(connect_loading);
        });

        connect_base.setText(strings.goText);
        ViewCompat.setTranslationZ(connect_loading, 15);
        connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.2f);
        connect_loading.setAlpha(0f);
        flag.setAlpha(0f);

        ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams)flag.getLayoutParams();
        lp1.width = helperMethods.screenWidth()*19/100;
        lp1.height = helperMethods.screenWidth()*14/100;
        flag.setLayoutParams(lp1);
        location_info.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.035f);
        setStatusBarColor();

    }

    void setStatusBarColor(){
        Window window = homeModel.getInstance().getHomeInstance().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(homeModel.getInstance().getHomeInstance().getResources().getColor(R.color.colorPrimary));
    }

    /*EVENT TO VIEW HANDLERS*/

    void onStopping()
    {
        if(buttonStatus != enums.vpn_status.DISCONNECTING) {
            buttonStatus = enums.vpn_status.DISCONNECTING;
            connect_loading.animate().alpha(1);
            connect_base.setText(strings.stopingText);
            connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * 0.065f);
        }
    }


    void onConnected()
    {
        if(buttonStatus != enums.vpn_status.CONNECTED) {
            connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth() * 0.065f);
            connect_base.setText(strings.connectedText);
            connect_loading.animate().alpha(0);
            buttonStatus = enums.vpn_status.CONNECTED;
        }
    }

    void onStopped()
    {
        if(buttonStatus != enums.vpn_status.IDLE) {
            connect_base.setText(strings.goText);
            connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX, helperMethods.screenWidth()*0.2f);
            connect_loading.animate().alpha(0);
            buttonStatus = enums.vpn_status.IDLE;
        }
    }

    void onConnecting()
    {
        connect_base.setText(strings.connectingText);
        connect_base.setTextSize(TypedValue.COMPLEX_UNIT_PX,helperMethods.screenWidth()*0.065f);
        connect_loading.animate().alpha(1);
        buttonStatus = enums.vpn_status.CONNECTING_VPN;
    }

    void onSetFlag(String location)
    {
        if(!location.equals(strings.emptySTR))
        {
            flag_status = location.toLowerCase();
            startPostTask(messages.UPDATE_FLAG);
        }
    }

    void onHideFlag()
    {
        flag.animate().alpha(0);
        location_info.setText("Unconnected");
    }

    /*ANIMATION VIEW REDIRECTIONS*/


    /*POST UI TASKS*/

    public void startPostTask(int m_id)
    {
        Message message = new Message();
        message.what = m_id;
        updateUIHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler()
    {
        updateUIHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == messages.UPDATE_FLAG)
                {
                    if(!flag_status.equals(strings.emptySTR))
                    {
                        if(flag.getAlpha()==0f)
                        {
                            flag.animate().alpha(1);
                        }

                        Locale obj = new Locale("", flag_status);

                        flag.setBackground(FlagKit.drawableWithFlag(homeModel.getInstance().getHomeInstance(), flag_status));
                        location_info.setText("Connected To | " + obj.getDisplayCountry());
                    }
                }
            }
        };
    }

}
