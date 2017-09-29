package com.example.fleissig.restaurantapp.ui.widgets.textviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.utils.TypefaceUtils;


public class UniversBoldTextView
        extends TextView {
    public UniversBoldTextView(Context paramContext) {
        super(paramContext);
        setTypeface(getTypeface(paramContext));
    }

    public UniversBoldTextView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        setTypeface(getTypeface(paramContext));
    }

    public UniversBoldTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        setTypeface(getTypeface(paramContext));
    }

    private Typeface getTypeface(Context paramContext) {
        return TypefaceUtils.getUniversBold(paramContext, isInEditMode());
    }

}
