package org.kllbff.mygallery.animations;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * <h3>Простейшая анимация появления элемента графического интерфейса</h3>
 * <p>Представляет плавное изменение прозрачности элемента с последующим закреплением средствами метода {@link View#setVisibility(int)}.
 *    Класс является наследником {@link AlphaAnimation} и реализует интерфейc {@link android.view.animation.Animation.AnimationListener AnimationListener}
 *    и тем самым является и анимацией и слушателем событий. Это необходимо для отлавливания события окончания анимации для закрепления элемента</p>
 */
public class ShowAnimation extends AlphaAnimation implements Animation.AnimationListener {
    private View target;

    /**
     * Инициализирует анимацию, &quot;привязывая&quot; её к определённому элементу управления
     * <p>При использовании с любым другим View, появляться будет в любом случае указанный в конструкторе</p>
     *
     * @param view показываемый элемент управления
     */
    public ShowAnimation(View view) {
        super(0.0F, 1.0F);
        this.target = view;
        setAnimationListener(this);
        this.setDuration(500L);
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    /**
     * По окончании анимации элемент должен быть закреплен, используя метод {@link View#setVisibility(int)}
     * с аргументом {@link View#VISIBLE}
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        target.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}
}
