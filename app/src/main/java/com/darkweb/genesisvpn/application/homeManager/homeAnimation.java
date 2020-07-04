package com.darkweb.genesisvpn.application.homeManager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.darkweb.genesisvpn.application.constants.messages;

class homeAnimation
{
    /*INITIALIZATIONS*/

    private static final homeAnimation ourInstance = new homeAnimation();
    static homeAnimation getInstance() {
        return ourInstance;
    }
    private Handler updateUIHandler;

    private homeAnimation()
    {
        createUpdateUiHandler();
    }

    private ObjectAnimator circularGrow;

    /*ANIMATIONS IMPLEMENTATION*/

    @SuppressLint("ObjectAnimatorBinding")
    void beatAnimation(Object target)
    {
        circularGrow = ObjectAnimator.ofPropertyValuesHolder(
                target,
                PropertyValuesHolder.ofFloat("scaleX", 1.3f),
                PropertyValuesHolder.ofFloat("scaleY", 1.3f),
                PropertyValuesHolder.ofFloat("alpha", 0f)
        );

        circularGrow.setStartDelay(1500);
        circularGrow.setDuration(1500);
        circularGrow.start();

        ((Button) target).setAlpha(1f);
        setListerner(circularGrow,messages.CIRCULAR_GROW_FINISH,messages.CIRCULAR_GROW_STARTED);
    }

    @SuppressLint("ObjectAnimatorBinding")
    void rotateAnimation(Object target)
    {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        ((ImageView) target).startAnimation(rotate);
    }

    /*LISTENERS IMPLEMENTATION*/

    private void setListerner(ObjectAnimator animation, final int onFinish,final int onStart)
    {
            animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                startPostTask(onStart);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startPostTask(onFinish);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /*LISTENERS POST IMPLEMENTATION*/

    private void startPostTask(int m_id){
        Message message = new Message();
        message.what = m_id;
        updateUIHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateUiHandler(){
        updateUIHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == messages.CIRCULAR_GROW_STARTED)
                {
                    //home_model.getInstance().getHomeInstance().connectLoadingStatus(1);
                }
                if(msg.what == messages.CIRCULAR_GROW_FINISH)
                {
                    circularGrow.start();
                }
            }
        };
    }
}
