package erandoo.app.custom;

import android.animation.Animator;
import android.view.animation.Interpolator;

/**
 * Created by DouglasW on 6/8/2014.
 */
public interface PieGraphAnimate {

    final int ANIMATE_NORMAL = 0;
    final int ANIMATE_INSERT = 1;
    final int ANIMATE_DELETE = 2;
    int getDuration();
    void setDuration(int duration);

    Interpolator getInterpolator();
    void setInterpolator(Interpolator interpolator);

    boolean isAnimating();
    boolean cancelAnimating();
    void animateToGoalValues();
    void setAnimationListener(Animator.AnimatorListener animationListener);
}
