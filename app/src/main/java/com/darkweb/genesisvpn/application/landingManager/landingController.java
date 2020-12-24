package com.darkweb.genesisvpn.application.landingManager;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.darkweb.genesisvpn.R;
import com.darkweb.genesisvpn.application.constants.enums;
import com.darkweb.genesisvpn.application.constants.keys;
import com.darkweb.genesisvpn.application.constants.status;
import com.darkweb.genesisvpn.application.pluginManager.pluginManager;
import com.darkweb.genesisvpn.application.stateManager.sharedControllerManager;
import com.github.paolorotolo.appintro.AppIntro;

import java.util.Arrays;

public class landingController extends AppIntro {

    private landingViewController mLauncherViewController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        CustomSlideBigText welcome = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text);
        welcome.setTitle(getString(R.string.LANDING_HELLO));
        welcome.setSubTitle(getString(R.string.LANDING_WELCOME));
        addSlide(welcome);

        CustomSlideBigText intro2 = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text);
        intro2.setTitle(getString(R.string.LANDING_BROWSE_INFO));
        intro2.setSubTitle(getString(R.string.LANDING_NO_TRACKING));
        addSlide(intro2);

        CustomSlideBigText cs2 = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text);
        cs2.setTitle(getString(R.string.LANDING_BRIDGES_INFO));
        cs2.showButton(getString(R.string.LANDING_MORE), v -> {
            status.LANDING_NAVIGATION = 1;
            finish();
            final Handler handler = new Handler();
            handler.postDelayed(() ->
            {
                sharedControllerManager.getInstance().getHomeController().onSettingManager(null);
            }, 100);
        });
        addSlide(cs2);

        CustomSlideBigText cs3 = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text);
        cs3.setTitle(getString(R.string.LANDING_FILTER_INFO));
        cs3.showButton(getString(R.string.LANDING_MORE), v -> {
            status.LANDING_NAVIGATION = 2;
            finish();
            final Handler handler = new Handler();
            handler.postDelayed(() ->
            {
                sharedControllerManager.getInstance().getHomeController().onAppManager(null);
            }, 100);
        });
        addSlide(cs3);

        setBarColor(getResources().getColor(R.color.alert_default));
        setSeparatorColor(getResources().getColor(R.color.white));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);
        initConnections();
    }

    private void initConnections(){
        mLauncherViewController = new landingViewController(this,null);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(status.LANDING_NAVIGATION==1){
            this.getPager().setCurrentItem(2);
        }
        else if(status.LANDING_NAVIGATION==2){
            this.getPager().setCurrentItem(3);
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        status.LANDING_PAGE_SHOWN = true;
        status.LANDING_NAVIGATION = 0;
        pluginManager.getInstance().onPreferenceTrigger(Arrays.asList(keys.LANDING_PAGE_CONNECT, status.LANDING_PAGE_SHOWN), enums.PREFERENCES_ETYPE.SET_BOOL);
        finish();
    }

}