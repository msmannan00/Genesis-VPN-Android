package com.darkweb.genesisvpn.application.pluginManager;

import android.content.Context;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.strings;
import com.darkweb.genesisvpn.application.homeManager.homeModel;
import com.darkweb.genesisvpn.application.proxyManager.proxyController;

public class messageManager
{
    /*Initializations*/
    private boolean isPopupOn = false;

    private static final messageManager ourInstance = new messageManager();

    public static messageManager getInstance()
    {
        return ourInstance;
    }

    private messageManager()
    {
    }

    /*Helper Methods*/
    public void serverLoading()
    {
        Context application_context = homeModel.getInstance().getHomeInstance();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.server_message_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.server_message_desc)
                .addButton(strings.server_message_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

    /*Helper Methods*/
    public void permissionError()
    {
        Context application_context = homeModel.getInstance().getHomeInstance();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(application_context)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(strings.permission_title)
                .setBackgroundColor(application_context.getResources().getColor(R.color.blue_dark_v2))
                .setTextColor(application_context.getResources().getColor(R.color.black))
                .setMessage(strings.permission_desc)
                .addButton(strings.permission_bt1, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    proxyController.getInstance().autoStart();
                })
                .addButton(strings.permission_bt2, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }
}
