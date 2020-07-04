package com.darkweb.genesisvpn.application.serverManager;

import com.anchorfree.partner.api.data.Country;

public class listRowModel
{
    /*Private Variables*/

    private String flag;
    private String header;
    private String description;
    private Country server;

    /*Initializations*/

    listRowModel(String header, String description, String flag, Country server) {
        this.flag = flag;
        this.header = header;
        this.description = description;
        this.server = server;
    }

    /*Variable Getters*/

    String getHeader() {
        return header;
    }
    String getDescription() {
        return description;
    }
    public String getFlag() {
        return flag;
    }
    Country getCountryModel() {
        return server;
    }
}
