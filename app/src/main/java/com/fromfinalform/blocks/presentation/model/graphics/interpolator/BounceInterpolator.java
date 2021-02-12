package com.fromfinalform.blocks.presentation.model.graphics.interpolator;

import android.view.animation.Interpolator;

public class BounceInterpolator implements Interpolator {

    private float factor = 0.3f; // default

    public BounceInterpolator() {
    }

    public BounceInterpolator(float factor) {
        this.factor = factor;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.pow(2, (-10 * input)) * Math.sin(((2 * Math.PI) * (input - (factor / 4))) / factor) + 1);
    }
}