//
// Created by Administrator on 2017-09-20.
//

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <jni.h>

const int END = 0xff;
const int32_t WHITE = 0xffffffff;
const int32_t BLACK = 0xff000000;

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */

JNIEXPORT jstring JNICALL
Java_top_xstar_photolibrary_HelloC_hello(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "Hello JNI !");
}

jint *ARGB(jint src) {
    jint color[3];
    color[0] = ((src >> 16) & END);
    color[1] = (jbyte) ((src >> 16) & END);
    color[2] = (src >> 8) & END;
    color[3] = src & END;
    return color;
}

JNIEXPORT jint JNICALL Java_top_xstar_photolibrary_HelloC_grayAlogrithm(jint src) {
    int r = (src >> 16) & END;
    int g = (src >> 8) & END;
    int b = src & END;
    return (jint) (r * 299 + g * 587 + b * 114) / 1000;
}

JNIEXPORT jint JNICALL Java_top_xstar_photolibrary_HelloC_abs(jint n) {
    return n > 0 ? n : -n;
}

JNIEXPORT jintArray JNICALL Java_top_xstar_photolibrary_HelloC_pencil
        (JNIEnv *jniEnv, jclass jc, jintArray pixels, jint w, jint h, jint threshold) {


}

JNIEXPORT jintArray JNICALL Java_top_xstar_photolibrary_HelloC_sketch
        (JNIEnv *jniEnv, jclass jc, jintArray pixels, jint w, jint h, jint threshold) {
    jint src;
    jint dst;
    jboolean b;
    jint *p = (*jniEnv)->GetIntArrayElements(jniEnv, pixels, &b);
    for (int i = 0; i < h; ++i) {
        for (int j = 0; j < w; ++j) {
            if (j == w - 1 || i == h - 1)
                continue;
            src = Java_top_xstar_photolibrary_HelloC_grayAlogrithm(p[i * w + j]);
            dst = Java_top_xstar_photolibrary_HelloC_grayAlogrithm(p[i * (w + 1) + j + 1]);
            if (Java_top_xstar_photolibrary_HelloC_abs(src - dst) >= threshold) {
                p[i * w + j] = BLACK;
            } else {
                p[i * w + j] = WHITE;
            }
        }
    }

    (*jniEnv)->ReleaseIntArrayElements(jniEnv, pixels, p, JNI_ABORT);
    return pixels;
}

