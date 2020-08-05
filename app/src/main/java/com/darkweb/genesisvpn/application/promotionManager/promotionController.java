package com.darkweb.genesisvpn.application.promotionManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class promotionController extends Fragment {

    /* PRIVATE VARIABLES */

    private promotionViewController m_view_controller;
    private promotionModel m_model;
    private View.OnClickListener m_on_click_listener;
    private View.OnFocusChangeListener m_on_focus_listener;

    private EditText m_promotion_edit_text;
    private ImageButton m_back_navigation;
    private Button m_submit;

    /* ALERT VARIABLES */

    private ConstraintLayout m_alert_dialog;
    private TextView m_alert_title;
    private TextView m_alert_description;
    private Button m_alert_dismiss;

    /* INITIALIZATION */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.promotion_view, null);
        initializeModel(root);
        initializeViews();
        initializeClickListeners();
        initializeListeners();
        return root;
    }

    public void initializeViews(){
        m_view_controller = new promotionViewController(getActivity(), new promotionViewCallback(), m_promotion_edit_text, m_alert_dialog, m_alert_title, m_alert_description, m_back_navigation, m_submit, m_alert_dismiss);
    }

    public boolean onBackPressed(){
        if(m_view_controller.isAlertViewShwoing()){
            m_view_controller.onAlertDismiss();
            return false;
        }
        else {
            return true;
        }
    }

    public void initializeModel(View root){
        m_model = new promotionModel(getActivity(), new promotionModelCallback());
        sharedControllerManager.getInstance().setPromotionController(this);
        m_promotion_edit_text = root.findViewById(R.id.m_promotion_edit_text);
        m_alert_dialog = root.findViewById(R.id.m_alert_dialog);
        m_alert_title = root.findViewById(R.id.m_alert_title);
        m_alert_description = root.findViewById(R.id.m_alert_description);
        m_back_navigation = root.findViewById(R.id.m_back_navigation);
        m_submit = root.findViewById(R.id.m_submit);
        m_alert_dismiss = root.findViewById(R.id.m_alert_dismiss);
    }

    public void initializeClickListeners(){
        m_on_click_listener = (View v) -> {
            if(v.getId()==R.id.m_back_navigation){
                m_model.quit();
            }
            else if(v.getId()==R.id.m_submit){
                m_model.adsDisabler();
            }
            else if(v.getId()==R.id.m_alert_dismiss || v.getId()==R.id.m_alert_dialog){
                onAlertDismiss();
            }
        };
        m_on_focus_listener = (view, b) -> {
            if(!b){
                helperMethods.hideKeyboard(promotionController.this.getActivity());
            }
        };
    }

    public void initializeListeners(){
        m_back_navigation.setOnClickListener(m_on_click_listener);
        m_submit.setOnClickListener(m_on_click_listener);
        m_alert_dismiss.setOnClickListener(m_on_click_listener);
        m_alert_dialog.setOnClickListener(m_on_click_listener);
        m_promotion_edit_text.setOnFocusChangeListener(m_on_focus_listener);
    }

    /*LAYOUT EVENT HANDLER*/

    public void onClearPromotiont(){
        m_view_controller.onClearPromotionEditText();
    }

    public void onShowAlert(String p_title,String p_description){
        m_view_controller.onShowAlert(p_description, p_title, true);
    }

    public void onAlertDismiss() {
        onClearPromotiont();
        m_view_controller.onAlertDismiss();
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class promotionModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type.equals(enums.ETYPE.ABOUT_ADS_REMOVE_CLICK))
            {
                onClearPromotiont();
                if((boolean)pluginManager.getInstance().onAdvertTrigger(null,enums.AD_ETYPE.AD_STATUS)){
                    onShowAlert(strings.AD_ADS_ALREADY_DISABLED_TITLE, strings.AD_ADS_ALREADY_DISABLED);
                }
                else if(m_model.isPromoCodeValid(m_promotion_edit_text.getText().toString())){
                    pluginManager.getInstance().onAdvertTrigger(Collections.singletonList(promotionController.this),enums.AD_ETYPE.DISABLE_ADS);
                }else{
                    onShowAlert(strings.AD_ADS_FAILED_TITLE, strings.AD_ADS_FAILED);
                }
            }
            else if(p_event_type.equals(enums.ETYPE.GENERIC_QUIT))
            {
                if(m_view_controller.isAlertViewShwoing()){
                    m_view_controller.onAlertDismiss();
                }
                else {
                    getActivity().onBackPressed();
                }
            }
        }
    }

    public class promotionViewCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
        }
    }
}
