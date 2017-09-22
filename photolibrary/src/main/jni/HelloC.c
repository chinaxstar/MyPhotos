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
#include <android/log.h>
#include <stdio.h>

#define TAG "JNI_LOG"
#define LOGE(text) __android_log_print(ANDROID_LOG_ERROR,TAG,"#text")

const jint END = 255;
const jint WHITE = 0xffffffff;
const jint BLACK = 0xff000000;
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


JNIEXPORT jint JNICALL
Java_top_xstar_photolibrary_HelloC_grayAlogrithm(JNIEnv *jniEnv, jclass jc, jint src) {
    jint A = (src >> 24) & END;
    jint R = (src >> 16) & END;
    jint G = (src >> 8) & END;
    jint B = src & END;
    return (B * 114 + G * 587 + R * 299) / 1000;
}

JNIEXPORT jboolean JNICALL
Java_top_xstar_photolibrary_HelloC_checkDiff(JNIEnv *jniEnv, jclass jc, jintArray _rgb, jint src,
                                             jint threshold) {
    jint *rgb = (*jniEnv)->GetIntArrayElements(jniEnv, _rgb, NULL);
    jsize len = (*jniEnv)->GetArrayLength(jniEnv, _rgb);
    jint R = ((src >> 16) & END) * len;
    jint G = ((src >> 8) & END) * len;
    jint B = (src & END) * len;
    threshold *= len;
    for (int i = 0; i < len; ++i) {
        R -= ((rgb[i] >> 16) & END);
        G -= ((rgb[i] >> 8) & END);
        B -= (rgb[i] & END);
    }
#ifdef LOGE
    char str[30];
    sprintf(str, "R%dG%dB%d", R, G, B);
    LOGE(str);
#endif
    (*jniEnv)->ReleaseIntArrayElements(jniEnv, _rgb, rgb, JNI_ABORT);
    if ((R >= threshold) & (G >= threshold) & (B >= threshold))return 1;
    return 0;
}

JNIEXPORT jint JNICALL Java_top_xstar_photolibrary_HelloC_abs(JNIEnv *jniEnv, jclass jc, jint n) {
    return n > 0 ? n : -n;
}

JNIEXPORT jintArray JNICALL Java_top_xstar_photolibrary_HelloC_pencil
        (JNIEnv *jniEnv, jclass jc, jintArray pixels, jint w, jint h, jint threshold) {
    jintArray newPixels = (*jniEnv)->NewIntArray(jniEnv, w * h);
    jint *_p = (*jniEnv)->GetIntArrayElements(jniEnv, newPixels, NULL);

    jintArray _temp = (*jniEnv)->NewIntArray(jniEnv, 8);
    jint *temp = (*jniEnv)->GetIntArrayElements(jniEnv, _temp, NULL);

    jint src;
    jint *p = (*jniEnv)->GetIntArrayElements(jniEnv, pixels, NULL);
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            if (j == (w - 1) | i == (h - 1))
                continue;
            src = p[i * w + j];
            temp[0] = p[(i - 1) * w + j - 1];
            temp[1] = p[(i - 1) * w + j];
            temp[2] = p[(i - 1) * w + j + 1];
            temp[3] = p[i * w + j + -1];
            temp[4] = p[i * w + j + 1];
            temp[5] = p[(i + 1) * w + j - 1];
            temp[6] = p[(i + 1) * w + j];
            temp[7] = p[(i + 1) * w + j + 1];
            if (Java_top_xstar_photolibrary_HelloC_checkDiff(jniEnv, jc, temp, src, threshold)) {
                _p[i * w + j] = BLACK;
            } else
                _p[i * w + j] = WHITE;
        }
    }
    (*jniEnv)->ReleaseIntArrayElements(jniEnv, pixels, p, JNI_ABORT);
    (*jniEnv)->ReleaseIntArrayElements(jniEnv, newPixels, _p, JNI_ABORT);
//    (*jniEnv)->ReleaseIntArrayElements(jniEnv, _temp, temp, JNI_ABORT);
    return newPixels;
}

JNIEXPORT jintArray JNICALL Java_top_xstar_photolibrary_HelloC_grey
        (JNIEnv *jniEnv, jclass jc, jintArray pixels, jint w, jint h) {
    jint gray;
    jint temp;
    jint *p = (*jniEnv)->GetIntArrayElements(jniEnv, pixels, NULL);
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            if (j == (w - 1) | i == (h - 1))
                continue;
            gray = Java_top_xstar_photolibrary_HelloC_grayAlogrithm(jniEnv, jc, p[i * w + j]);
            temp = (gray << 16) | (gray << 8) | gray;
            p[i * w + j] = (p[i * w + j] >> 24 << 24) | temp;
        }
    }
    (*jniEnv)->ReleaseIntArrayElements(jniEnv, pixels, p, JNI_ABORT);
    return pixels;
}

JNIEXPORT jintArray JNICALL Java_top_xstar_photolibrary_HelloC_sketch
        (JNIEnv *jniEnv, jclass jc, jintArray pixels, jint w, jint h, jint threshold) {
    jint src;
    jint dst;
    jint *p = (*jniEnv)->GetIntArrayElements(jniEnv, pixels, NULL);
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            if (j == (w - 1) | i == (h - 1))
                continue;
            src = Java_top_xstar_photolibrary_HelloC_grayAlogrithm(jniEnv, jc, p[i * w + j]);
            dst = Java_top_xstar_photolibrary_HelloC_grayAlogrithm(jniEnv, jc,
                                                                   p[w * (i + 1) + j + 1]);
            if (Java_top_xstar_photolibrary_HelloC_abs(jniEnv, jc, src - dst) >= threshold) {
                p[i * w + j] = BLACK;
            } else {
                p[i * w + j] = WHITE;
            }
        }
    }
    (*jniEnv)->ReleaseIntArrayElements(jniEnv, pixels, p, JNI_ABORT);
    return pixels;
}

