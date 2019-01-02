package pl.cyfrogen.budget.activities;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import pl.cyfrogen.budget.R;
import pl.cyfrogen.budget.base.BaseActivity;
import pl.cyfrogen.budget.libraries.GUIUtils;
import pl.cyfrogen.budget.libraries.OnRevealAnimationListener;

public abstract class CircularRevealActivity extends BaseActivity {
    private final int layoutID;
    private final int fabID;
    private final int container1ID;
    private final int container2ID;
    private View fab;
    private View container1;
    private View container2;

    public CircularRevealActivity(int layoutID, int fabID, int container1ID, int container2ID) {
        this.layoutID = layoutID;
        this.fabID = fabID;
        this.container1ID = container1ID;
        this.container2ID = container2ID;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutID);

        fab = findViewById(fabID);
        container1 = findViewById(container1ID);
        container2 = findViewById(container2ID);
        container2.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimation();
            setupExitAnimation();
        }
        onInitialized(savedInstanceState);
    }

    public abstract void onInitialized(Bundle savedInstanceState);


    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow(container1);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }


    private void animateRevealShow(final View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        GUIUtils.animateRevealShow(this, container1, fab.getWidth() / 2, R.color.colorPrimary,
                cx, cy, new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {

                    }

                    @Override
                    public void onRevealShow() {
                        initViews();
                    }
                });
    }

    private void initViews() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            animation.setDuration(600);
            container2.startAnimation(animation);
            container2.setVisibility(View.VISIBLE);
        });
    }


    @Override
    public void onBackPressed() {
        finishWithAnimation();
    }

    protected void finishWithAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animation.setDuration(200);
        container2.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                container2.setVisibility(View.INVISIBLE);
                GUIUtils.animateRevealHide(getApplicationContext(), container1, R.color.colorPrimary, fab.getWidth() / 2,
                        new OnRevealAnimationListener() {
                            @Override
                            public void onRevealHide() {
                                backPressed();

                            }

                            @Override
                            public void onRevealShow() {

                            }
                        });
            }
        });

    }


    private void backPressed() {
        super.onBackPressed();
    }

    private void setupExitAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.changebounds_with_arcmotion);
        getWindow().setSharedElementReturnTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

    }
}
