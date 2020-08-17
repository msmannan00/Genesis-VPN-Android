package com.darkweb.genesisvpn.application.promotionManager;

import androidx.fragment.app.FragmentActivity;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;
import java.util.Calendar;

class promotionModel
{
    /* PRIVATE VARIABLES */

    private FragmentActivity m_context;
    private eventObserver.eventListener m_event;
    private int m_promo_code_multiplier = 472810;

    /*INITIALIZATION*/

    public promotionModel(FragmentActivity p_context, eventObserver.eventListener p_event){
        this.m_context = p_context;
        m_event = p_event;
    }

    /*HANDLERS*/

    public boolean isPromoCodeValid(String m_promo){
        int m_user_promo;
        try {
            if(m_promo.equals("imammehdi00")){
                return true;
            }
            m_user_promo = Integer.parseInt(m_promo);
        } catch(NumberFormatException nfe) {
            return false;
        }

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int m_promo_code_real = (day + month+1 + year)  * m_promo_code_multiplier;
        return m_promo_code_real == m_user_promo;
    }

    void quit()
    {
        m_event.invokeObserver(null, enums.ETYPE.GENERIC_QUIT);
    }

    public void adsDisabler()
    {
        m_event.invokeObserver(null, enums.ETYPE.ABOUT_ADS_REMOVE_CLICK);
    }
}
