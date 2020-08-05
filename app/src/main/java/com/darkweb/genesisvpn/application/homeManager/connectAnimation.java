package com.darkweb.genesisvpn.application.homeManager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.darkweb.genesisvpn.application.constants.messages;
import com.darkweb.genesisvpn.application.constants.strings;

class connectAnimation
{
    /*INITIALIZATIONS*/

    private int m_default_size;
    private Handler updateUIHandler;
    private ObjectAnimator circularGrow;
    private Object m_target;

    public connectAnimation()
    {
        createUpdateUiHandler();
    }

    /*ANIMATIONS IMPLEMENTATION*/

    @SuppressLint("ObjectAnimatorBinding")
    void beatAnimation(Object p_target)
    {
        m_default_size = ((Button) p_target).getWidth();
        this.m_target = p_target;
        ((Button)this.m_target).setLayerType(View.LAYER_TYPE_HARDWARE, null);
        circularGrow = ObjectAnimator.ofPropertyValuesHolder(
                p_target,
                PropertyValuesHolder.ofFloat(strings.HA_SCALE_X, 1.3f),
                PropertyValuesHolder.ofFloat(strings.HA_SCALE_Y, 1.3f),
                PropertyValuesHolder.ofFloat(strings.HA_ALPHA, 0f)
        );
        circularGrow.setStartDelay(1500);
        circularGrow.setDuration(1500);
        circularGrow.start();

        ((Button) p_target).setAlpha(0.6f);
        setListerner(circularGrow,messages.CIRCULAR_GROW_FINISH,messages.CIRCULAR_GROW_STARTED);
    }

    ImageView m_support;
    @SuppressLint("ObjectAnimatorBinding")
    void rotateAnimation(Object p_target, ImageView p_support)
    {
        m_support = p_support;
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2700);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        m_target = p_target;
        (this.m_support).setLayerType(View.LAYER_TYPE_HARDWARE, null);
        ((ImageView) p_target).startAnimation(rotate);
    }

    /*LISTENERS IMPLEMENTATION*/

    private void setListerner(ObjectAnimator p_animation, final int p_on_finish,final int p_on_start)
    {
            p_animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(m_support !=null){
                    m_support.setTranslationZ(35);
                }
                Button btn = (Button) m_target;
                if(btn!=null && btn.getTransitionName().equals(strings.HA_PAUSE)){
                    btn.setVisibility(View.INVISIBLE);
                }else {
                    btn.setVisibility(View.VISIBLE);
                }
                startPostTask(p_on_start);
                ((Button) m_target).setWidth(m_default_size);
                ((Button) m_target).setHeight(m_default_size);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startPostTask(p_on_finish);
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

    private void startPostTask(int p_id){
        Message message = new Message();
        message.what = p_id;
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
