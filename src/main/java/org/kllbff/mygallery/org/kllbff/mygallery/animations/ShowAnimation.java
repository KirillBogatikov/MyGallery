package org.kllbff.mygallery.org.kllbff.mygallery.animations;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class ShowAnimation extends AlphaAnimation implements Animation.AnimationListener {
    private View target;

    public ShowAnimation(View view) {
        super(0.0F, 1.0F);
        this.target = view;
        setAnimationListener(this);
        this.setDuration(500L);
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
        target.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}
}
