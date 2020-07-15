package com.darkweb.genesisvpn.application.serverManager;

import com.anchorfree.partner.api.data.Country;

public class listRowModel
{
    /*Private Variables*/

    private String m_flag;
    private String m_header;
    private String m_description;
    private Country m_server;

    /*Initializations*/

    listRowModel(String p_header, String p_description, String p_flag, Country p_server) {
        this.m_flag = p_flag;
        this.m_header = p_header;
        this.m_description = p_description;
        this.m_server = p_server;
    }

    /*Variable Getters*/

    String getHeader() {
        return m_header;
    }
    String getDescription() {
        return m_description;
    }
    public String getFlag() {
        return m_flag;
    }
    Country getCountryModel() {
        return m_server;
    }
}
