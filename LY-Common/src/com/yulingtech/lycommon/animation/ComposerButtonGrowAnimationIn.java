package com.yulingtech.lycommon.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;


public class ComposerButtonGrowAnimationIn extends InOutAnimation {

	public ComposerButtonGrowAnimationIn(int i) {
		super(InOutAnimation.Direction.IN, i, new View[0]);
	}

	@Override
	protected void addInAnimation(View[] aview) {
		addAnimation(new ScaleAnimation(0F, 1F, 0F, 1F, 1, 0.5F, 1, 0.5F));
		addAnimation(new AlphaAnimation(0F, 1F));

	}

	@Override
	protected void addOutAnimation(View[] aview) {}

}
