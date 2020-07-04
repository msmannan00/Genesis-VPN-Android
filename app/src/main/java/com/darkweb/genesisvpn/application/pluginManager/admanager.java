package com.darkweb.genesisvpn.application.pluginManager;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.strings;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class admanager
{

    /*Private Variables*/
    private static final admanager ourInstance = new admanager();
    private InterstitialAd mInterstitialHidden_base;
    private int adDisableCount = 0;
    private boolean adsDisabled = false;
    private AdView bannerAds = null;

    /*Initializations*/

    public static admanager getInstance() {
        return ourInstance;
    }

    private admanager() {
    }

    public void initialize(AppCompatActivity m_context)
    {
        if(!preferenceManager.getInstance().getBool(keys.ads_disabled,false))
        {
            MobileAds.initialize(m_context, "ca-app-pub-5074525529134731~1412991199");
            mInterstitialHidden_base = initAd("ca-app-pub-5074525529134731/7636560716", m_context);

            initBannerAds(m_context);
        }
        else
        {
            adsDisabled = true;
        }
    }

    private void initBannerAds(AppCompatActivity m_context)
    {
        bannerAds = m_context.findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .build();
        bannerAds.loadAd(request);
    }

    private InterstitialAd initAd(String id, AppCompatActivity m_context)
    {
        InterstitialAd adInstance = new InterstitialAd(m_context);
        adInstance.setAdUnitId(id);
        adInstance.loadAd(new AdRequest.Builder().build());

        return adInstance;
    }

    /*Helper Methods*/

    public void showAd()
    {
        if(!adsDisabled)
        {
            if(mInterstitialHidden_base.isLoaded())
            {
                mInterstitialHidden_base.show();
                mInterstitialHidden_base.loadAd(new AdRequest.Builder().build());
            }
            else if(!mInterstitialHidden_base.isLoading())
            {
                mInterstitialHidden_base.loadAd(new AdRequest.Builder().build());
            }
        }
    }

    public void adsDisabler(AppCompatActivity m_context)
    {
        adsDisabled = true;
        adDisableCount+=1;
        if(adDisableCount==10)
        {
            if(bannerAds!=null)
            {
                bannerAds.setVisibility(View.GONE);
                preferenceManager.getInstance().setBool(keys.ads_disabled,true);
                Toast.makeText(m_context, strings.ads_disabled, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(m_context, strings.ads_already_disabled, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
