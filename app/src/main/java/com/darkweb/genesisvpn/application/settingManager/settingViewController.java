package com.darkweb.genesisvpn.application.settingManager;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.anchorfree.sdk.UnifiedSDK;
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

    private Switch m_auto_connect;
    private Switch m_auto_start;
    private Switch m_auto_optimal_location;
    private RadioButton m_udp_connection;
    private RadioButton m_tcp_connection;
    private RadioButton m_def_connection;
    private ImageView m_default_server;
    private LinearLayout m_default_server_layout;
    private ConstraintLayout m_alert_dialog;
    private TextView m_alert_title;
    private TextView m_alert_description;

    /*LOCAL VARIABLE DECLARATION*/

    private FragmentActivity m_context;
    private eventObserver.eventListener m_event;
    private boolean isDefaultServerSet = false;

    /*INITIALIZATIONS*/

    public settingViewController(FragmentActivity p_context, eventObserver.eventListener p_event, Switch p_auto_connect, Switch p_auto_start, RadioButton p_udp_connection, RadioButton p_tcp_connection, ImageView p_default_server, Switch p_auto_optimal_location, LinearLayout p_default_server_layout, RadioButton p_def_connection, ConstraintLayout p_alert_dialog, TextView p_alert_title, TextView p_alert_description)
    {
        this.m_context = p_context;
        this.m_event = p_event;
        this.m_auto_connect = p_auto_connect;
        this.m_auto_start = p_auto_start;
        this.m_udp_connection = p_udp_connection;
        this.m_tcp_connection = p_tcp_connection;
        this.m_def_connection = p_def_connection;
        this.m_default_server = p_default_server;
        this.m_auto_optimal_location = p_auto_optimal_location;
        this.m_default_server_layout = p_default_server_layout;
        this.m_alert_dialog = p_alert_dialog;
        this.m_alert_title = p_alert_title;
        this.m_alert_description = p_alert_description;

        onInitializeView();
    }

    private void onInitializeView(){
        Window window = this.m_context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(m_context.getResources().getColor(R.color.colorPrimary));
        onUpdateFlag();

        m_auto_connect.setChecked(status.AUTO_CONNECT);
        m_auto_start.setChecked(status.AUTO_START);
        m_auto_optimal_location.setChecked(status.AUTO_OPTIMAL_LOCATION);

        if(status.CONNECTION_TYPE == 1){
            onUpdateConnectionType(R.id.m_connect_type_TCP);
        }
        else if(status.CONNECTION_TYPE == 2){
            onUpdateConnectionType(R.id.m_connect_type_AUTO);
        }
        else {
            onUpdateConnectionType(R.id.m_connect_type_UDP);
        }

        if(m_auto_optimal_location.isChecked()){
            m_default_server_layout.setAlpha(0);
            m_default_server_layout.setVisibility(View.INVISIBLE);
        }else {
            m_default_server_layout.setAlpha(1);
            m_default_server_layout.setVisibility(View.VISIBLE);
        }

        m_alert_dialog.setAlpha(0);
        m_alert_dialog.setVisibility(View.GONE);
        m_alert_title.setTypeface(null, Typeface.BOLD);
    }

    public void onUpdateConnectionType(int p_id){
        if(p_id == R.id.m_connect_type_TCP){
            m_udp_connection.setChecked(false);
            m_tcp_connection.setChecked(true);
            m_def_connection.setChecked(false);
        }
        else if(p_id == R.id.m_connect_type_AUTO){
            m_udp_connection.setChecked(false);
            m_tcp_connection.setChecked(false);
            m_def_connection.setChecked(true);
        }
        else {
            m_udp_connection.setChecked(true);
            m_tcp_connection.setChecked(false);
            m_def_connection.setChecked(false);
        }
    }

    public void onDefaultServerToggle(boolean p_is_checked){
        if(p_is_checked){
            m_default_server_layout.animate().setDuration(250).alpha(0).withEndAction(() -> {
                m_default_server_layout.setVisibility(View.INVISIBLE);
                onUpdateFlag();
            });
        }else {
            onUpdateFlag();
            m_default_server_layout.setVisibility(View.VISIBLE);
            m_default_server_layout.animate().setDuration(250).alpha(1).withEndAction(() -> {
            });;
        }
    }

    public void onUpdateFlag(){
        if(!m_auto_optimal_location.isChecked()){
            if(m_default_server!=null && !status.DEFAULT_SERVER.equals(strings.EMPTY_STR)){
                m_default_server.setImageDrawable(FlagKit.drawableWithFlag(m_context, status.DEFAULT_SERVER));
                isDefaultServerSet = true;
            }
        }else if(isDefaultServerSet){
            m_default_server.setImageDrawable(m_context.getDrawable(R.drawable.no_flag_default));
            isDefaultServerSet = false;
        }
    }

    /*ALERT VIEW*/

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

    public void onShowAlert(String p_error_message,String p_error_title, boolean p_is_forced){
        String m_error = p_error_message;
        if(p_is_forced || !m_alert_description.getText().equals(m_error) && !m_alert_title.getText().equals(strings.AF_NO_APPLICATION_HEADER)){
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

    public boolean isAlertViewShwoing(){
        return m_alert_dialog.getVisibility() == View.VISIBLE;
    }
}
