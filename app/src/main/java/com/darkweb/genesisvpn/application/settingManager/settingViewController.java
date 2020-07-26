package com.darkweb.genesisvpn.application.settingManager;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.messages;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.jwang123.flagkit.FlagKit;

import java.util.Locale;

class settingViewController {

    /* PRIVATE VIEWS */

    Switch m_auto_connect;
    Switch m_auto_start;
    Switch m_auto_optimal_location;
    RadioButton m_udp_connection;
    RadioButton m_tcp_connection;
    private ImageView m_default_server = null;

    /*LOCAL VARIABLE DECLARATION*/

    private AppCompatActivity m_context;
    private eventObserver.eventListener m_event;
    private boolean isDefaultServerSet = false;

    /*INITIALIZATIONS*/

    public settingViewController(AppCompatActivity p_context, eventObserver.eventListener p_event, Switch p_auto_connect, Switch p_auto_start, RadioButton p_udp_connection, RadioButton p_tcp_connection, ImageView p_default_server, Switch p_auto_optimal_location)
    {
        this.m_context = p_context;
        this.m_event = p_event;
        this.m_auto_connect = p_auto_connect;
        this.m_auto_start = p_auto_start;
        this.m_udp_connection = p_udp_connection;
        this.m_tcp_connection = p_tcp_connection;
        this.m_default_server = p_default_server;
        this.m_auto_optimal_location = p_auto_optimal_location;

        onInitializeView();
    }

    private void onInitializeView(){
        Window window = this.m_context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(m_context.getResources().getColor(R.color.colorPrimary));
        m_context. setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        onUpdateFlag();

        m_auto_connect.setChecked(status.AUTO_CONNECT);
        m_auto_start.setChecked(status.AUTO_START);
        m_auto_optimal_location.setChecked(status.AUTO_OPTIMAL_LOCATION);

        if(status.CONNECTION_TYPE == 0){
            m_udp_connection.setChecked(true);
            m_tcp_connection.setChecked(false);
        }
        else {
            m_udp_connection.setChecked(false);
            m_tcp_connection.setChecked(true);
        }
    }

    public void onUpdateFlag(){
        if(!status.AUTO_OPTIMAL_LOCATION){
            if(m_default_server!=null && !status.DEFAULT_SERVER.equals(strings.EMPTY_STR)){
                animateFlag(FlagKit.drawableWithFlag(m_context, status.DEFAULT_SERVER));
                m_auto_optimal_location.setChecked(false);
                isDefaultServerSet = true;
            }
        }else if(isDefaultServerSet){
            animateFlag(m_context.getDrawable(R.drawable.no_flag_default));
            isDefaultServerSet = false;
        }
    }

    public void animateFlag(Drawable p_flag){
        try{
            if(m_default_server.getAlpha()>=0.7f){
                m_default_server.animate().cancel();
                m_default_server.animate().alpha(0).setDuration(150).withEndAction(() -> {
                    m_default_server.animate().setDuration(350).alpha(1);
                    m_default_server.setImageDrawable(p_flag);
                }).start();
            }else {
                m_default_server.animate().cancel();
                m_default_server.setAlpha(0);
                m_default_server.animate().setDuration(350).alpha(1).start();
                m_default_server.setImageDrawable(p_flag);
            }
        }catch (Exception ex){
        }
    }

    /*HELPER METHODS*/

}
