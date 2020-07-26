package com.darkweb.genesisvpn.application.promotionManager;

import android.content.pm.ActivityInfo;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;

class promotionViewController {

    /*LOCAL VARIABLE DECLARATION*/
    private AppCompatActivity m_context;
    private eventObserver.eventListener m_event;
    private EditText m_promotion_edit_text;

    /*INITIALIZATIONS*/

    public promotionViewController(AppCompatActivity p_context, eventObserver.eventListener p_event, EditText p_promotion_edit_text)
    {
        this.m_context = p_context;
        m_event = p_event;
        m_promotion_edit_text = p_promotion_edit_text;
        onInitializeView();
    }

    private void onInitializeView(){
        Window window = this.m_context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(m_context.getResources().getColor(R.color.colorPrimary));
        m_context. setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    public void onClearPromotionEditText(){
        m_promotion_edit_text.clearFocus();
        helperMethods.hideKeyboard(m_context);
    }


    /*HELPER METHODS*/

}
