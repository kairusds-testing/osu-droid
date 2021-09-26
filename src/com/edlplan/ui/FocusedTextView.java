package com.edlplan.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class FocusedTextView extends TextView {

    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FocusedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(isFocused(), direction, previouslyFocusedRect);
    }
    
    @Override
    public void onWindowFocusChanged(boolean focused) {
        super.onWindowFocusChanged(isFocused());
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}