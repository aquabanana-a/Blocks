package com.fromfinalform.blocks.presentation.model.graphics.interpolator;

import android.view.animation.Interpolator;

//is same? PathInterpolatorCompat.create (is part of v4)
//похоже на то, но та реализация очень уж монструозна, если глянуть в исходники. тут попроще и работает 1в1 как на йос (сравнивал по раскадровке).

//from https://opensource.apple.com/source/WebCore/WebCore-955.66/platform/graphics/UnitBezier.h
//https://developer.apple.com/documentation/quartzcore/camediatimingfunctionname/1522173-easeineaseout
public class TimeFuncInterpolator implements Interpolator {

    private final static float EPSILON = (float) 1E-6;
    private final float ax;
    private final float bx;
    private final float cx;
    private final float ay;
    private final float by;
    private final float cy;

    public TimeFuncInterpolator(double p1x, double p1y, double p2x, double p2y) {
       this((float)p1x, (float)p1y, (float) p2x, (float) p2y);
    }
    public TimeFuncInterpolator(float p1x, float p1y, float p2x, float p2y) {
        // Calculate the polynomial coefficients, implicit first and last control points are (0,0) and (1,1).
        cx = 3.0f * p1x;
        bx = 3.0f * (p2x - p1x) - cx;
        ax = 1.0f - cx - bx;

        cy = 3.0f * p1y;
        by = 3.0f * (p2y - p1y) - cy;
        ay = 1.0f - cy - by;
    }

    @Override
    public float getInterpolation(float input) {
        return solve(input, EPSILON) + EPSILON;
    }

    float sampleCurveX(float t) {
        // `ax t^3 + bx t^2 + cx t' expanded using Horner's rule.
        return ((ax * t + bx) * t + cx) * t;
    }

    float sampleCurveY(float t) {
        return ((ay * t + by) * t + cy) * t;
    }

    float sampleCurveDerivativeX(float t) {
        return (3.0f * ax * t + 2.0f * bx) * t + cx;
    }

    // Given an x value, find a parametric value it came from.
    float solveCurveX(float x, float epsilon) {
        float t0;
        float t1;
        float t2;
        float x2;
        float d2;
        int i;

        // First try a few iterations of Newton's method -- normally very fast.
        for (t2 = x, i = 0; i < 8; i++) {
            x2 = sampleCurveX(t2) - x;
            if (Math.abs(x2) < epsilon) return t2;
            d2 = sampleCurveDerivativeX(t2);
            if (Math.abs(d2) < 1e-6) break;
            t2 = t2 - x2 / d2;
        }

        // Fall back to the bisection method for reliability.
        t0 = 0.0f;
        t1 = 1.0f;
        t2 = x;

        if (t2 < t0) return t0;
        if (t2 > t1) return t1;

        while (t0 < t1) {
            x2 = sampleCurveX(t2);
            if (Math.abs(x2 - x) < epsilon) return t2;
            if (x > x2) t0 = t2;
            else t1 = t2;
            t2 = (t1 - t0) * .5f + t0;
        }

        // Failure.
        return t2;
    }

    float solve(float x, float epsilon) {
        return sampleCurveY(solveCurveX(x, epsilon));
    }
}
