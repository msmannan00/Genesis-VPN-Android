package com.darkweb.genesisvpn.application.helperManager;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.darkweb.genesisvpn.application.constants.strings;
import static com.darkweb.genesisvpn.application.constants.strings.SE_UNKNOWN_EXCEPTION;
import static com.darkweb.genesisvpn.application.constants.strings.SE_VPN_PERMISSION;

public class helperMethods
{
    public static String createErrorMessage(VpnException p_exception){
        if(p_exception.getMessage().equals(SE_VPN_PERMISSION)){
            return SE_VPN_PERMISSION;
        }
        else if(p_exception == null || p_exception.getCause() == null || p_exception.getCause().getLocalizedMessage() == null || p_exception.getCause().getLocalizedMessage().equals(strings.EMPTY_STR)){
            return SE_UNKNOWN_EXCEPTION;
        }else {
            return p_exception.getCause().getLocalizedMessage();
        }
    }

    public static void shareApp(AppCompatActivity p_context) {
        ShareCompat.IntentBuilder.from(p_context)
                .setType(strings.SH_TYPE)
                .setChooserTitle(strings.SH_TITLE)
                .setSubject(strings.SH_SUBJECT)
                .setText(strings.SH_DESC + p_context.getPackageName())
                .startChooser();
    }

       public static void sendEmail(AppCompatActivity p_context)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                strings.EM_MAIL_TO,strings.EM_EMAIL, null));
        intent.setData(Uri.parse(strings.CO_TYPE));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{strings.EM_EMAIL});
        intent.putExtra(Intent.EXTRA_SUBJECT, strings.EM_SUBJECT);
        if (intent.resolveActivity(p_context.getPackageManager()) != null) {
            p_context.startActivity(intent);
        }
    }

    public static void quit(AppCompatActivity activity) {
        activity.finish();
    }

    public static void quitApplication(AppCompatActivity activity) {

    }

    public static void openActivity( Class<?> p_cls, AppCompatActivity p_context){
        Intent myIntent = new Intent(p_context, p_cls);
        p_context.startActivity(myIntent);
    }

    public static int screenWidth()
    {
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        if(width>height)
        {
            return height;
        }
        {
            return width;
        }
    }
}
