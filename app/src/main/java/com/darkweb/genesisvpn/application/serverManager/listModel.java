package com.darkweb.genesisvpn.application.serverManager;

import com.anchorfree.partner.api.data.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class listModel
{
    /*Private Variables*/

    private ArrayList<listRowModel> list_model = new ArrayList<>();

    /*Initializations*/

    private static final listModel ourInstance = new listModel();
    public static listModel getInstance()
    {
        return ourInstance;
    }

    /*Helper Methods*/

    public void setModel(List<Country> countries){
        for(int counter=0;counter<countries.size();counter++)
        {
            Locale obj = new Locale("", countries.get(counter).getCountry());
            listRowModel row_model = new listRowModel(obj.getDisplayCountry(),"Country Code | "+countries.get(counter).getCountry() + "\nFast Relay Servers | " + countries.get(counter).getServers(),countries.get(counter).getCountry(),countries.get(counter));
            list_model.add(row_model);
        }
    }
    public ArrayList<listRowModel> getModel()
    {
        return list_model;
    }

}