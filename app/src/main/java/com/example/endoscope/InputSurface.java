/*
2 * Copyright (C) 2013 The Android Open Source Project
3 *
4 * Licensed under the Apache License, Version 2.0 (the "License");
5 * you may not use this file except in compliance with the License.
6 * You may obtain a copy of the License at
7 *
8 *      http://www.apache.org/licenses/LICENSE-2.0
9 *
10 * Unless required by applicable law or agreed to in writing, software
11 * distributed under the License is distributed on an "AS IS" BASIS,
12 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
13 * See the License for the specific language governing permissions and
14 * limitations under the License.
15 */

package com.example.endoscope;

import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;
/**
 30 * Holds state associated with a Surface used for MediaCodec encoder input.
 31 * <p>
 32 * The constructor takes a Surface obtained from MediaCodec.createInputSurface(), and uses that
 33 * to create an EGL window surface.  Calls to eglSwapBuffers() cause a frame of data to be sent
 34 * to the video encoder.
 35 */
class InputSurface {
    private static final String TAG = "InputSurface";
    private static final boolean VERBOSE = false;

    private static final int EGL_RECORDABLE_ANDROID = 0x3142;

    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
     private Surface mSurface;
  /**
     49     * Creates an InputSurface from a Surface.
     50     */
  public InputSurface(Surface surface) {
      if (surface == null) {
          throw new NullPointerException();
      }
      mSurface = surface;
              eglSetup();
  }

                /**
     61     * Prepares EGL.  We want a GLES 2.0 context and a surface that supports recording.
     62     */
                private void eglSetup() {
                    mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
                       throw new RuntimeException("unable to get EGL14 display");}
                    int[] version = new int[2];
                if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
                       mEGLDisplay = null;
                        throw new RuntimeException("unable to initialize EGL14");
                   }

               // Configure EGL for recordable and OpenGL ES 2.0.  We want enough RGB bits
               // to minimize artifacts from possible YUV conversion.
              int[] attribList = {
                      EGL14.EGL_RED_SIZE, 8,
                      EGL14.EGL_GREEN_SIZE, 8,
                      EGL14.EGL_BLUE_SIZE, 8,
                      EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                      EGL_RECORDABLE_ANDROID, 1,
                      EGL14.EGL_NONE
              };
                    EGLConfig[] configs = new EGLConfig[1];
                    int[] numConfigs = new int[1];
                    if (!EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                            numConfigs, 0)) {throw new RuntimeException("unable to find RGB888+recordable ES2 EGL config");}
             // Configure context for OpenGL ES 2.0.
                    int[] attrib_list = {
                               EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                                EGL14.EGL_NONE
                        };
               mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT,
                               attrib_list, 0);
               checkEglError("eglCreateContext");
                if (mEGLContext == null) {
                        throw new RuntimeException("null context");
                   }

               // Create a window surface, and attach it to the Surface we received.
               int[] surfaceAttribs = {
                                EGL14.EGL_NONE
                       };
               mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, configs[0], mSurface,
                               surfaceAttribs, 0);
               checkEglError("eglCreateWindowSurface");
                if (mEGLSurface == null) {throw new RuntimeException("surface was null");
                   }
           }
    /**
     116     * Discard all resources held by this class, notably the EGL context.  Also releases the
     117     * Surface that was passed to our constructor.
     118     */
               public void release() {
               if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                       EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            EGL14.eglReleaseThread();
                        EGL14.eglTerminate(mEGLDisplay);
                    }

               mSurface.release();

                mEGLDisplay = EGL14.EGL_NO_DISPLAY;
                mEGLContext = EGL14.EGL_NO_CONTEXT;
                mEGLSurface = EGL14.EGL_NO_SURFACE;

                mSurface = null;
            }

                /**
     137     * Makes our EGL context and surface current.
     138     */
                public void makeCurrent() {
               if (!EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
                        throw new RuntimeException("eglMakeCurrent failed");
                   }
           }

                public void makeUnCurrent() {
                if (!EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                              EGL14.EGL_NO_CONTEXT)) {
                       throw new RuntimeException("eglMakeCurrent failed");
                  }
           }

                /**
     153     * Calls eglSwapBuffers.  Use this to "publish" the current frame.
     154     */
                public boolean swapBuffers() {
               return EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
           }

                /**
     160     * Returns the Surface that the MediaCodec receives buffers from.
     161     */
                public Surface getSurface() {
               return mSurface;
            }

       /**
     167     * Queries the surface's width.
     168     */
             public int getWidth() {
                int[] value = new int[1];
        EGL14.eglQuerySurface(mEGLDisplay, mEGLSurface, EGL14.EGL_WIDTH, value, 0);
                return value[0];
           }

              /**
     176     * Queries the surface's height.
     177     */
   public int getHeight() {
               int[] value = new int[1];
               EGL14.eglQuerySurface(mEGLDisplay, mEGLSurface, EGL14.EGL_HEIGHT, value, 0);
                return value[0];
           }

               /**
     185     * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
     186     */
                public void setPresentationTime(long nsecs) {
                EGLExt.eglPresentationTimeANDROID(mEGLDisplay, mEGLSurface, nsecs);
           }

               /**
     192     * Checks for EGL errors.
     193     */
             private void checkEglError(String msg) {
             int error;
              if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
                     throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
                  }
           }
    }
