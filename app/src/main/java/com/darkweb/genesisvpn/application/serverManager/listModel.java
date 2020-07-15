package com.darkweb.genesisvpn.application.serverManager;

import com.anchorfree.partner.api.data.Country;
import com.darkweb.genesisvpn.application.constants.strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class listModel
{
    /*Private Variables*/

    private ArrayList<listRowModel> m_list_model = new ArrayList<>();

    /*Initializations*/

    private static final listModel ourInstance = new listModel();
    public static listModel getInstance()
    {
        return ourInstance;
    }

    /*Helper Methods*/

    public void setModel(List<Country> p_countries){
        for(int counter=0;counter<p_countries.size();counter++)
        {
            Locale obj = new Locale(strings.EMPTY_STR, p_countries.get(counter).getCountry());
            listRowModel row_model = new listRowModel(obj.getDisplayCountry(),strings.CS_COUNTRY_CODE + p_countries.get(counter).getCountry() + strings.LINE_BREAK + strings.CS_RELAY_SERVERS + p_countries.get(counter).getServers(),p_countries.get(counter).getCountry(),p_countries.get(counter));
            m_list_model.add(row_model);
        }
    }
    public ArrayList<listRowModel> getModel()
    {
        return m_list_model;
    }

}