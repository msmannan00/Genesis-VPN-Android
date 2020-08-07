package com.darkweb.genesisvpn.application.constants;

import android.view.View;

import com.darkweb.genesisvpn.application.serverManager.serverListRowModel;
import java.util.ArrayList;

public class status {
    public static boolean DEVELOPER_MODE = false;
    public static boolean HAS_APPLICATION_STOPPED = false;
    public static ArrayList<String> DISABLED_APPS = new ArrayList<>();
    public static ArrayList<serverListRowModel> RECENT_SERVERS = new ArrayList<>();
    public static boolean AUTO_CONNECT = false;
    public static boolean AUTO_START = false;
    public static boolean AUTO_OPTIMAL_LOCATION = true;
    public static int CONNECTION_TYPE = 2;
    public static String DEFAULT_SERVER = "";
}
