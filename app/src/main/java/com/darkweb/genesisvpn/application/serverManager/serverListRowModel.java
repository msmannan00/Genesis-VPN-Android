package com.darkweb.genesisvpn.application.serverManager;

import com.anchorfree.partner.api.data.Country;

public class serverListRowModel implements Comparable<serverListRowModel>
{
    /*Private Variables*/

    private String m_flag;
    private String m_header;
    private String m_description;
    private Country m_server;

    /*Initializations*/

    serverListRowModel(String p_header, String p_description, String p_flag, Country p_server) {
        this.m_flag = p_flag;
        this.m_header = p_header;
        this.m_description = p_description;
        this.m_server = p_server;
    }

    /*Variable Getters*/

    public String getHeader() {
        return m_header;
    }
    public String getDescription() {
        return m_description;
    }
    public String getFlag() {
        return m_flag;
    }
    public Country getCountryModel() {
        return m_server;
    }
    public void setCountryModel(Country p_model) {
        m_server = p_model;
    }

    @Override
    public int compareTo(serverListRowModel listRowModel) {
        if (listRowModel.getHeader() == null || listRowModel.getHeader() == null) {
            return 0;
        }else {
            return m_header.compareTo(listRowModel.getHeader());
        }
    }
}
