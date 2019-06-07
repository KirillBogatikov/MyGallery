package org.kllbff.mygallery.animations;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * <h3>Простейшая анимация исчезания элемента графического интерфейса</h3>
 * <p>Представляет плавное изменение прозрачности элемента с последующим исчезновением средствами метода {@link View#setVisibility(int)}.
 *    Класс является наследником {@link AlphaAnimation} и реализует интерфейc {@link android.view.animation.Animation.AnimationListener AnimationListener}
 *    и тем самым является и анимацией и слушателем событий. Это необходимо для отлавливания события окончания анимации для скрытия элемента</p>
 */
public class HideAnimation extends AlphaAnimation implements Animation.AnimationListener {
    private View target;

    /**
     * Инициализирует анимацию, &quot;привязывая&quot; её к определённому элементу управления
     * <p>При использовании с любым другим View, исчезать будет в любом случае указанный в конструкторе</p>
     *
     * @param view скрываемый элемент управления
     */
    public HideAnimation(View view) {
        super(1.0F, 0.0F);
        this.target = view;
        this.setAnimationListener(this);
        this.setDuration(500L);
    }


    @Override
    public void onAnimationStart(Animation animation) {}

    /**
     * По окончании анимации элемент должен быть скрыт, используя метод {@link View#setVisibility(int)}
     * с аргументом {@link View#GONE}
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        target.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
