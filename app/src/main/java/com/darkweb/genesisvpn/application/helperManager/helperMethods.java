package com.darkweb.genesisvpn.application.helperManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.telephony.TelephonyManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import com.darkweb.genesisvpn.application.constants.strings;

import java.util.Locale;

public class helperMethods
{
    public static void shareApp(AppCompatActivity p_context) {
        ShareCompat.IntentBuilder.from(p_context)
                .setType(strings.sh_type)
                .setChooserTitle(strings.sh_title)
                .setSubject(strings.sh_subject)
                .setText(strings.sh_desc + p_context.getPackageName())
                .startChooser();
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        try {
            String m_country = context.getResources().getConfiguration().locale.getCountry();
            return m_country;
        }
        catch (Exception e) { }

        return "us";
    }

    public static void sendEmail(AppCompatActivity m_context)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","gamesolstudios@gmail.com", null));
        intent.setData(Uri.parse(strings.co_type)); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"gamesolstudios@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Issue Ticket");
        if (intent.resolveActivity(m_context.getPackageManager()) != null) {
            m_context.startActivity(intent);
        }
    }

    public static void quit(AppCompatActivity activity) {
        activity.finish();
    }

    public static void openActivity( Class<?> cls, AppCompatActivity m_context){
        Intent myIntent = new Intent(m_context, cls);
        m_context.startActivity(myIntent);
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
