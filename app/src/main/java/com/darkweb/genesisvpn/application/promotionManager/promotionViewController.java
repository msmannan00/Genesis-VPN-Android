package com.darkweb.genesisvpn.application.promotionManager;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;

class promotionViewController {

    /*LOCAL VARIABLE DECLARATION*/
    private FragmentActivity m_context;
    private eventObserver.eventListener m_event;
    private EditText m_promotion_edit_text;
    private ConstraintLayout m_alert_dialog;
    private TextView m_alert_title;
    private TextView m_alert_description;
    private ImageButton m_back_navigation;
    private Button m_submit;
    private Button m_alert_dismiss;

    /*INITIALIZATIONS*/

    public promotionViewController(FragmentActivity p_context, eventObserver.eventListener p_event, EditText p_promotion_edit_text, ConstraintLayout p_alert_dialog, TextView p_alert_title, TextView p_alert_description, ImageButton p_back_navigation, Button p_submit, Button p_alert_dismiss)
    {
        this.m_context = p_context;
        this.m_event = p_event;
        this.m_promotion_edit_text = p_promotion_edit_text;
        this.m_alert_dialog = p_alert_dialog;
        this.m_alert_title = p_alert_title;
        this.m_alert_description = p_alert_description;
        this.m_back_navigation = p_back_navigation;
        this.m_submit = p_submit;
        this.m_alert_dismiss = p_alert_dismiss;

        onInitializeView();
    }

    private void onInitializeView(){
        Window window = this.m_context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(m_context.getResources().getColor(R.color.colorPrimary));
    }

    public void onClearPromotionEditText(){
        m_promotion_edit_text.clearFocus();
        helperMethods.hideKeyboard(m_context);
    }

    public void onAlertDismiss() {
        m_promotion_edit_text.setText(strings.EMPTY_STR);
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
        m_alert_dialog.setVisibility(View.VISIBLE);
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
        return m_alert_dialog.getAlpha() > 0;
    }

    /*HELPER METHODS*/

}
