package org.kllbff.mygallery.animations;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class HideAnimation extends AlphaAnimation implements Animation.AnimationListener {
    private View target;

    public HideAnimation(View view) {
        super(1.0F, 0.0F);
        this.target = view;
        this.setAnimationListener(this);
        this.setDuration(500L);
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
        target.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
