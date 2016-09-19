/*
 * Copyright (C) 2011 Google Inc.
 * Licensed to The Android Open Source Project.
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
 */

package com.yaoyumeng.v2ex2.ui.photo;

import android.os.Build;

import com.yaoyumeng.v2ex2.Application;

import java.util.regex.Pattern;


/**
 * Image utilities
 */
public class ImageUtils {
    // Logging
    private static final String TAG = "ImageUtils";

    /**
     * Minimum class memory class to use full-res photos
     */
    private final static long MIN_NORMAL_CLASS = 32;
    /**
     * Minimum class memory class to use small photos
     */
    private final static long MIN_SMALL_CLASS = 24;

    private static final String BASE64_URI_PREFIX = "base64,";
    private static final Pattern BASE64_IMAGE_URI_PATTERN = Pattern.compile("^(?:.*;)?base64,.*");

    public static enum ImageSize {
        EXTRA_SMALL,
        SMALL,
        NORMAL,
    }

    public static final ImageSize sUseImageSize;

    static {
        // On HC and beyond, assume devices are more capable
        if (Build.VERSION.SDK_INT >= 11) {
            sUseImageSize = ImageSize.NORMAL;
        } else {
            if (Application.getInstance().getMemorySize() >= MIN_NORMAL_CLASS) {
                // We have plenty of memory; use full sized photos
                sUseImageSize = ImageSize.NORMAL;
            } else if (Application.getInstance().getMemorySize() >= MIN_SMALL_CLASS) {
                // We have slight less memory; use smaller sized photos
                sUseImageSize = ImageSize.SMALL;
            } else {
                // We have little memory; use very small sized photos
                sUseImageSize = ImageSize.EXTRA_SMALL;
            }
        }
    }

    /**
     * @return true if the MimeType type is image
     */
    public static boolean isImageMimeType(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }
}
