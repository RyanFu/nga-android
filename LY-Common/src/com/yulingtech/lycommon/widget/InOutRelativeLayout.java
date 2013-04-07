package com.yulingtech.lycommon.widget;

import com.yulingtech.lycommon.animation.InOutAnimation;
import com.yulingtech.lycommon.animation.InOutAnimation.Direction;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

public class InOutRelativeLayout extends RelativeLayout {

	private Animation	animation;

	public InOutRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public InOutRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InOutRelativeLayout(Context context) {
		super(context);
	}

	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
		if (this.animation instanceof InOutAnimation) {
			setVisibility(((InOutAnimation) animation).direction != InOutAnimation.Direction.OUT ? View.VISIBLE
					: View.GONE);
		}
	}

	@Override
	protected void onAnimationStart() {
		super.onAnimationStart();
		if (this.animation instanceof InOutAnimation) {
			setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void startAnimation(Animation animation) {
		super.startAnimation(animation);
		this.animation = animation;
		getRootView().postInvalidate();
	}
}