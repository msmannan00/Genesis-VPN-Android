package com.darkweb.genesisvpn.application.serverManager;

import com.anchorfree.partner.api.data.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class list_model
{
    /*Private Variables*/

    private ArrayList<list_row_model> list_model = new ArrayList<>();

    /*Initializations*/

    private static final list_model ourInstance = new list_model();
    public static list_model getInstance()
    {
        return ourInstance;
    }

    /*Helper Methods*/

    public void setModel(List<Country> countries){
        for(int counter=0;counter<countries.size();counter++)
        {
            Locale obj = new Locale("", countries.get(counter).getCountry());
            list_row_model row_model = new list_row_model(obj.getDisplayCountry(),"Country Code | "+countries.get(counter).getCountry() + "\nFast Relay Servers | " + countries.get(counter).getServers(),countries.get(counter).getCountry(),countries.get(counter));
            list_model.add(row_model);
        }
    }
    public ArrayList<list_row_model> getModel()
    {
        return list_model;
    }

}