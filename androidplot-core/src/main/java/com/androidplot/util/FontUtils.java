/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.util;

import android.graphics.*;

public class FontUtils {

    private static final int ZERO = 0;

    /**
     * Determines the height of the tallest character that can be drawn by paint.
     * @param paint
     * @return
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        return (-metrics.ascent) + metrics.descent;
        //return (-metrics.top) + metrics.bottom;
    }

    /**
     * Get the smallest rect that ecompasses the text to be drawn using paint.
     * @param text
     * @param paint
     * @return
     */
    public static Rect getPackedStringDimensions(String text, Paint paint) {
        Rect size = new Rect();
        paint.getTextBounds(text, ZERO, text.length(), size);
        return size;
    }

    /**
     * Like getPackedStringDimensions except adds extra space to accommodate all
     * characters that can be drawn regardless of whether or not they exist in text.
     * This ensures a more uniform appearance for things that have dynamic text.
     * @param text
     * @param paint
     * @return
     */
    public static Rect getStringDimensions(String text, Paint paint) {
        Rect size = new Rect();
        if(text == null || text.length() == ZERO) {
            return null;
        }
        paint.getTextBounds(text, ZERO, text.length(), size);
        size.bottom = size.top + (int) getFontHeight(paint);
        return size;
    }

    /**
     * Draws text vertically centered on the specified coordinates
     * @param canvas
     * @param paint
     * @param text
     * @param cx
     * @param cy
     */
    public static void drawTextVerticallyCentered(Canvas canvas, String text, float cx, float cy, Paint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint);
    }

}
