package ysn.com.view.refresh_layout.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;

/**
 * @Author yangsanning
 * @ClassName AnimUtils
 * @Description 动画工具
 * @Date 2020/2/12
 * @History 2020/2/12 author: description:
 */
public class AnimUtils {

    public static void ofInt(long duration, final OnAnimActionListener onAnimActionListener, int... values) {
        ValueAnimator animator = ValueAnimator.ofInt(values);
        animator.setDuration(duration).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (onAnimActionListener != null) {
                    onAnimActionListener.onAnimUpdate(animation);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onAnimActionListener != null) {
                    onAnimActionListener.onAnimEnd(animation);
                }
            }
        });
    }

    public interface OnAnimActionListener {

        void onAnimUpdate(ValueAnimator animation);

        void onAnimEnd(Animator animation);
    }
}
