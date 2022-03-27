package ir.noavar.outlet.customs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import ir.noavar.outlet.MyApplication;

@SuppressLint("AppCompatCustomView")
public class SolidTextView extends TextView {
    public SolidTextView(Context context) {
        super(context);
        if (!isInEditMode()) setTypeFace();
    }

    public SolidTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) setTypeFace();
    }

    public SolidTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) setTypeFace();
    }

    @SuppressLint("NewApi")
    public SolidTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) setTypeFace();
    }

    private void setTypeFace() {
        MyApplication myApplication = (MyApplication) getContext().getApplicationContext();
        setTypeface(myApplication.getSolid());
    }
}
