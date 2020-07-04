package com.darkweb.genesisvpn.application.aboutManager;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;

class aboutModel
{
    /* PRIVATE VARIABLES */

    private AppCompatActivity m_context;

    /*INITIALIZATION*/

    public aboutModel(AppCompatActivity p_context){
        this.m_context = p_context;
    }

    /*HANDLERS*/

    void quit()
    {
        helperMethods.quit(m_context);
    }
}
