package com.darkweb.genesisvpn.application.serverManager;

import com.anchorfree.partner.api.data.Country;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.helperManager.eventObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class serverListModel
{
    /*Private Variables*/

    private ArrayList<serverListRowModel> m_country_model = new ArrayList<>();
    private ArrayList<String> m_country_model_unconnected = new ArrayList<>();
    private ArrayList<serverListRowModel> m_recent_model = new ArrayList<>();
    private serverListRowModel[] m_recent_model_array;
    private boolean m_server_populated = false;
    private eventObserver.eventListener m_event;

    /*Initializations*/

    private static final serverListModel ourInstance = new serverListModel();
    public static serverListModel getInstance()
    {
        return ourInstance;
    }

    public void onInitialize(eventObserver.eventListener p_event){
        m_event = p_event;
    }


    /*Helper Methods*/

    public void setCountryModel(List<Country> p_countries, ArrayList<String> p_recent){

        m_country_model.clear();

        if(!m_server_populated){
            m_server_populated = true;
            m_recent_model_array = new serverListRowModel[p_recent.size()];
            for(int counter=0;counter<p_countries.size();counter++)
            {
                Locale obj = new Locale(strings.EMPTY_STR, p_countries.get(counter).getCountry());
                serverListRowModel row_model = new serverListRowModel(obj.getDisplayCountry(),strings.CS_COUNTRY_CODE + p_countries.get(counter).getCountry() + strings.LINE_BREAK + strings.CS_RELAY_SERVERS + p_countries.get(counter).getServers(),p_countries.get(counter).getCountry(),p_countries.get(counter));
                if(p_recent.contains(row_model.getHeader())){
                    addRecentModel(row_model, p_recent.indexOf(row_model.getHeader()));
                }
                else if(m_country_model_unconnected.contains(row_model.getFlag())){
                    addRecentModel(row_model, p_recent.indexOf(row_model.getHeader()));
                }
                m_country_model.add(row_model);
            }
            for (com.darkweb.genesisvpn.application.serverManager.serverListRowModel serverListRowModel : m_recent_model_array) {
                if (serverListRowModel != null) {
                    m_recent_model.add(serverListRowModel);
                }
            }
            Collections.sort(m_country_model);
        }
        if(m_event!=null){
            m_event.invokeObserver(null, enums.ETYPE.ON_LOAD_LIST);
        }

    }
    public ArrayList<serverListRowModel> getCountryModel()
    {
        return m_country_model;
    }

    public void setRecentModel(ArrayList<serverListRowModel> p_recent_model){
        m_recent_model.clear();
        m_recent_model.addAll(p_recent_model);
    }

    public ArrayList<serverListRowModel> getRecentModel()
    {
        return m_recent_model;
    }

    public void addRecentModel(serverListRowModel p_model, int index)
    {
        try{
            m_recent_model_array[index] = p_model;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void addModelToServer(String p_server)
    {
        serverListRowModel p_model = null;
        for(int counter=0;counter<m_country_model.size() ;counter++){
            if(p_server.equals(m_country_model.get(counter).getFlag())){
                p_model = m_country_model.get(counter);
                break;
            }
        }

        if(p_model!=null){
            for(int counter=0;counter<m_recent_model.size();counter++){
                if(p_model.getHeader().equals(m_recent_model.get(counter).getHeader())){
                    m_recent_model.remove(counter);
                    m_recent_model.add(0,p_model);
                    status.RECENT_SERVERS.clear();
                    status.RECENT_SERVERS.addAll(m_recent_model);
                    return;
                }
            }
            m_recent_model.add(0,p_model);
            if(m_recent_model.size()>5){
                m_recent_model.remove(5);
            }
            status.RECENT_SERVERS.clear();
            status.RECENT_SERVERS.addAll(m_recent_model);
        }else {
            m_country_model_unconnected.add(p_server);
        }
    }
}