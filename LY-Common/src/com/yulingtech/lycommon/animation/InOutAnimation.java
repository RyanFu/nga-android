package com.yulingtech.lycommon.animation;

import android.view.View;
import android.view.animation.AnimationSet;

public abstract class InOutAnimation extends AnimationSet {

	public Direction	direction;

	public enum Direction {
		IN, OUT;
	}

	public InOutAnimation(Direction direction, long l, View[] aview) {
		super(true);
		this.direction = direction;
		switch (this.direction) {
		case IN:
			addInAnimation(aview);
			break;
		case OUT:
			addOutAnimation(aview);

			break;
		}
		setDuration(l);
	}

	protected abstract void addInAnimation(View aview[]);

	protected abstract void addOutAnimation(View aview[]);

}
