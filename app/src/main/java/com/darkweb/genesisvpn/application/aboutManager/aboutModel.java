package com.darkweb.genesisvpn.application.aboutManager;
import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesisvpn.application.helperManager.helperMethods;

class aboutModel
{
    /*INITIALIZATION*/

    private AppCompatActivity m_context;

    public aboutModel(AppCompatActivity p_context){
        this.m_context = p_context;
    }

    /*HANDLERS*/

    void quit()
    {
        helperMethods.quit(m_context);
    }
}
