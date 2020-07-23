package com.darkweb.genesisvpn.application.appManager;

import android.graphics.drawable.Drawable;

public class appListRowModel implements Comparable<appListRowModel>
{
    /*Private Variables*/

    private Drawable m_icon;
    private String m_header;
    private String m_description;

    /*Initializations*/

    public appListRowModel(String p_header, String p_description, Drawable p_icon) {
        this.m_icon = p_icon;
        this.m_header = p_header;
        this.m_description = p_description;
    }

    /*Variable Getters*/

    public String getHeader() {
        return m_header;
    }
    public String getDescription() {
        return m_description;
    }
    public Drawable getIcon() {
        return m_icon;
    }

    @Override
    public int compareTo(appListRowModel listRowModel) {
        if (m_header == null || listRowModel.m_header == null) {
            return 0;
        }else {
            return m_header.compareTo(listRowModel.m_header);
        }
    }
}
