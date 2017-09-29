package com.example.fleissig.restaurantapp.ui.widgets.textviews;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.utils.TypefaceUtils;


public class RoxwinBoldTextView
        extends TextView {
    public RoxwinBoldTextView(Context paramContext) {
        super(paramContext);
        setTypeface(getTypeface(paramContext));
    }

    public RoxwinBoldTextView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        setTypeface(getTypeface(paramContext));
    }

    public RoxwinBoldTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        setTypeface(getTypeface(paramContext));
    }

    private Typeface getTypeface(Context paramContext) {
        return TypefaceUtils.getBold(paramContext, isInEditMode());
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if(focused)
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if(focused)
            super.onWindowFocusChanged(focused);
    }


    @Override
    public boolean isFocused() {
        return true;
    }
}
