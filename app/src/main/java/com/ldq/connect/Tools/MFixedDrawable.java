package com.ldq.connect.Tools;

import android.graphics.drawable.ColorDrawable;

public class MFixedDrawable extends ColorDrawable {
    public MFixedDrawable(int white) {
        super(white);
    }

    @Override
    public void setAlpha(int alpha) {
        super.setAlpha(0);
    }
}
