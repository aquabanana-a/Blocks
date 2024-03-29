package com.fromfinalform.blocks.presentation.model.graphics.opengl;

import android.annotation.TargetApi;
import android.opengl.EGL14;
import android.os.Build;

/**
 * EGL 14 Config Factory class
 *
 * @author Perraco Labs (August-2015)
 * @repository https://github.com/perracolabs/GLSurfaceViewEGL14
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class EGL14Config {
    /**
     * Extension for surface recording
     */
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;

    /**
     * Chooses a valid EGL Config for EGL14
     *
     * @param eglDisplay EGL14 Display
     * @param recordable True to set the recordable flag
     * @return Resolved config
     */
    public static android.opengl.EGLConfig chooseConfig(final android.opengl.EGLDisplay eglDisplay, final boolean recordable) {
        // The actual surface is generally RGBA or RGBX, so situationally omitting alpha
        // doesn't really help. It can also lead to a huge performance hit on glReadPixels()
        // when reading into a GL_RGBA buffer.

        final int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_DEPTH_SIZE, 16,
                // Requires that setEGLContextClientVersion(2) is called on the view.
                EGL14.EGL_RENDERABLE_TYPE, 4,
                EGL14.EGL_SAMPLE_BUFFERS, 1,
                EGL14.EGL_SAMPLES, 4,
                EGL14.EGL_NONE};

        android.opengl.EGLConfig[] configList = new android.opengl.EGLConfig[1];
        final int[] numConfigs = new int[1];

        if (!EGL14.eglChooseConfig(eglDisplay, attribList, 0, configList, 0, configList.length, numConfigs, 0)) {
            //throw new RuntimeException("failed to find valid RGB8888 EGL14 EGLConfig");
            final int[] attribList2 = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_DEPTH_SIZE, 16,

                    // Requires that setEGLContextClientVersion(2) is called on the view.
//                    EGL14.EGL_RENDERABLE_TYPE, 4, // EGL_OPENGL_ES2_BIT
//                    EGL14.EGL_SAMPLE_BUFFERS, 1,
//                    EGL14.EGL_SAMPLES, 4, // This is for 4x MSAA

                    EGL14.EGL_NONE};

            android.opengl.EGLConfig[] configList2 = new android.opengl.EGLConfig[1];
            final int[] numConfigs2 = new int[1];
            if (!EGL14.eglChooseConfig(eglDisplay, attribList2, 0, configList2, 0, configList2.length, numConfigs, 0)) {
                throw new RuntimeException("failed to find valid RGB8888 EGL14 EGLConfig");
            } else return configList2[0];
        }
        return configList[0];
    }
}
