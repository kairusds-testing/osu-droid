package com.edlplan.ui;

import android.widget.TextView;

public class ActiveTextView extends TextView {

    @Override
    public boolean isFocused() {
        return true;
    }
}