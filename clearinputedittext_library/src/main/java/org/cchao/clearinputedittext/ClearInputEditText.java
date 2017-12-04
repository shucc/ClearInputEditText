package org.cchao.clearinputedittext;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by shucc on 17/11/30.
 * cc@cchao.org
 */
public class ClearInputEditText extends AppCompatEditText {

    private Drawable drawableEnd;

    private int iconSize;

    private int height;

    private int width;

    private boolean leftToRight = true;

    @DrawableRes
    private int clearInputDrawableInt = R.drawable.ic_clear_input;

    public ClearInputEditText(Context context) {
        super(context);
        init(context, null);
    }

    public ClearInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearInputEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearInputEditText);
        iconSize = typedArray.getDimensionPixelSize(R.styleable.ClearInputEditText_clear_input_size, 0);
        clearInputDrawableInt = typedArray.getResourceId(R.styleable.ClearInputEditText_clear_input_icon, R.drawable.ic_clear_input);
        typedArray.recycle();

        leftToRight = isLeftToRight();
        setMaxLines(1);
        setSingleLine(true);
        setSaveEnabled(true);
        if (!TextUtils.isEmpty(getText())) {
            showPasswordVisibilityIndicator(true);
        }
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    showPasswordVisibilityIndicator(true);
                } else {
                    showPasswordVisibilityIndicator(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            if (!TextUtils.isEmpty(getText().toString())) {
                showPasswordVisibilityIndicator(true);
            }
        } else {
            showPasswordVisibilityIndicator(false);
        }
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (leftToRight && right != null) {
            drawableEnd = right;
        } else if (!leftToRight && left != null) {
            drawableEnd = left;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && drawableEnd != null) {
            int x = (int) event.getX();
            //触摸区域设置为边长为输入框高度的正方形
            if ((leftToRight && (x >= (width - height))) || (!leftToRight && (x <= height))) {
                showPasswordVisibilityIndicator(false);
                setText("");
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 布局是否从左往右
     *
     * @return
     */
    private boolean isLeftToRight() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return true;
        }
        Configuration config = getResources().getConfiguration();
        return !(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL);
    }

    private void showPasswordVisibilityIndicator(boolean show) {
        Drawable[] existingDrawables = getCompoundDrawables();
        Drawable left = existingDrawables[0];
        Drawable top = existingDrawables[1];
        Drawable right = existingDrawables[2];
        Drawable bottom = existingDrawables[3];
        if (show) {
            Drawable original = AppCompatResources.getDrawable(getContext(), clearInputDrawableInt);
            if (iconSize > 0) {
                original.setBounds(0, 0, iconSize, iconSize);
            } else {
                int height = getHeight();
                original.setBounds(0, 0, height / 2, height / 2);
            }
            original.mutate();
            setCompoundDrawables(leftToRight ? left : original, top, leftToRight ? original : right, bottom);
        } else {
            setCompoundDrawables(leftToRight ? left : null, top, leftToRight ? null : right, bottom);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        drawableEnd = null;
        super.finalize();
    }
}
