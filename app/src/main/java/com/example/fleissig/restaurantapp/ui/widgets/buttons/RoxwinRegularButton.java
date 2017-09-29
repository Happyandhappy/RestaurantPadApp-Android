package com.example.fleissig.restaurantapp.ui.widgets.buttons;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.example.fleissig.restaurantapp.utils.TypefaceUtils;


public class RoxwinRegularButton
        extends Button {
    public RoxwinRegularButton(Context paramContext) {
        super(paramContext);
        setTypeface(getTypeface(paramContext));
    }

    public RoxwinRegularButton(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        setTypeface(getTypeface(paramContext));
    }

    public RoxwinRegularButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        setTypeface(getTypeface(paramContext));
    }

    private Typeface getTypeface(Context paramContext) {
        return TypefaceUtils.getRegular(paramContext, isInEditMode());
    }
}
