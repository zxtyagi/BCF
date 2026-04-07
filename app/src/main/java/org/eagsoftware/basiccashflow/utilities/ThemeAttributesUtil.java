package org.eagsoftware.basiccashflow.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.google.android.material.color.MaterialColors;

public class ThemeAttributesUtil {
    public static Drawable getDefaultEditTextDrawable(Context context) {
        try (TypedArray typArr = context.obtainStyledAttributes(new int[]{android.R.attr.editTextBackground})) {
            return typArr.getDrawable(0);
        }
    }

    @SuppressWarnings("unused")
    public static int getPrimaryColor(Context context) {
        return MaterialColors.getColor(context, androidx.appcompat.R.attr.colorPrimary,
                Color.BLACK);
    }

    public static int getOnPrimaryColor(Context context) {
        return MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnPrimary,
                Color.GREEN);
    }

    public static int getOnSurfaceColor(Context context) {
        return MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnSurface,
                Color.WHITE);
    }
}
