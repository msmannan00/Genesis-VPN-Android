package com.darkweb.genesisvpn.application.appManager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;

import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.constants.strings;

import java.util.ArrayList;
import java.util.List;

public class appListModel
{
    /*Private Variables*/

    private ArrayList<String> m_blocked_packages = new ArrayList<>();

    /*Initializations*/

    public void setModel(ArrayList<String> p_blocked_packages)
    {
        m_blocked_packages.addAll(p_blocked_packages);
    }

    public ArrayList<String> getModel()
    {
        return m_blocked_packages;
    }

}