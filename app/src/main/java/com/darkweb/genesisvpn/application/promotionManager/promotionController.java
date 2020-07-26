package com.darkweb.genesisvpn.application.promotionManager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;

import java.util.Collections;
import java.util.List;

public class promotionController extends AppCompatActivity {

    /* PRIVATE VARIABLES */

    private promotionViewController m_view_controller;
    private promotionModel m_model;
    private EditText m_promotion_edit_text;

    /* INITIALIZATION */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_view);
        initializeModel();
        initializeViews();
    }

    public void initializeViews(){
        m_view_controller = new promotionViewController(this, new promotionViewCallback(), m_promotion_edit_text);
    }

    public void initializeModel(){
        m_model = new promotionModel(this, new promotionModelCallback());
        sharedControllerManager.getInstance().setPromotionController(this);
        m_promotion_edit_text = findViewById(R.id.m_promotion_edit_text);
    }

    /*LAYOUT EVENT HANDLER*/

    public void onBackPressed(View view)
    {
        m_model.quit();
    }

    public void adsDisabler(View view)
    {
        m_model.adsDisabler();
        m_view_controller.onClearPromotionEditText();
    }

    public void onClearPromotionEditText(View view){
        m_view_controller.onClearPromotionEditText();
    }

    /*EVENT LISTNER CALLBACKS HANDLERS*/

    public class promotionModelCallback implements eventObserver.eventListener{

        @Override
        public void invokeObserver(List<Object> p_data, enums.ETYPE p_event_type)
        {
            if(p_event_type.equals(enums.ETYPE.ABOUT_ADS_REMOVE_CLICK))
            {
                if((boolean)pluginManager.getInstance().onAdvertTrigger(null,enums.AD_ETYPE.AD_STATUS)){
                    Toast.makeText(promotionController.this, strings.AD_ADS_ALREADY_DISABLED, Toast.LENGTH_SHORT).show();
                }
                else if(m_model.isPromoCodeValid(m_promotion_edit_text.getText().toString())){
                    pluginManager.getInstance().onAdvertTrigger(Collections.singletonList(promotionController.this),enums.AD_ETYPE.DISABLE_ADS);
                }else{
                    Toast.makeText(promotionController.this, strings.AD_ADS_FAILED, Toast.LENGTH_SHORT).show();
                }
            }
            else if(p_event_type.equals(enums.ETYPE.GENERIC_QUIT))
            {
                helperMethods.quit(promotionController.this);
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
