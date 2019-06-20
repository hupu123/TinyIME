package com.bluebank.tinyime;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TinyIME extends InputMethodService {

    private LinearLayout[] keyBars;

    private int crtKeyBar = 0;              // 第几行
    private int selectPosition = 0;         // 当前行的第几位
    private boolean isCapsOn = false;       // 大写开关，true=打开，false=关闭

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("hugh-tag", "TinyIME onKeyDown keyCode=" + keyCode + " isInputViewShown=" + isInputViewShown());
        if (isInputViewShown()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    down();
                    return true;
                case KeyEvent.KEYCODE_DPAD_UP:
                    up();
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    left();
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    right();
                    return true;
                case KeyEvent.KEYCODE_ENTER:
                    ok();
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    requestHideSelf(0);
                    return true;
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        Log.i("hugh-tag", "TinyIME onStartInputView info=" + info + " restarting=" + restarting);
        setKeyBarVisible();
        super.onStartInputView(info, restarting);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        Log.i("hugh-tag", "TinyIME onCreateInputView");
        View view = getLayoutInflater().inflate(R.layout.keyboard, null);
        initView(view);
        return view;
    }

    /**
     * 初始化控件
     *
     * @param view 根布局
     */
    private void initView(View view) {
        LinearLayout keyboard = view.findViewById(R.id.ll_keyboard);
        String[][] keyboardValues = Keyboard.getKeyboardValues(isCapsOn);
        keyBars = new LinearLayout[keyboardValues.length];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < keyboardValues.length; i++) {
            keyBars[i] = new LinearLayout(this);
            for (int j = 0; j < keyboardValues[i].length; j++) {
                LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvParams.setMargins(1, 1, 1, 1);
                TextView tv = new TextView(this);
                tv.setText(keyboardValues[i][j]);
                tv.setPadding(2, 0, 2, 0);
                tv.setLayoutParams(params);
                tv.setBackgroundColor(getResources().getColor(R.color.black));
                tv.setTextColor(getResources().getColor(R.color.white));
                tv.setFocusable(true);
                keyBars[i].addView(tv);
            }
            keyboard.setLayoutParams(params);
            keyboard.addView(keyBars[i]);
        }
    }

    /**
     * 更新键盘大小写
     */
    private void setKeyboardCaps() {
        for (int i = 0; i < keyBars.length; i++) {
            for (int j = 0; j < keyBars[i].getChildCount(); j++) {
                TextView tv = (TextView) keyBars[i].getChildAt(j);
                tv.setText(Keyboard.getKeyValue(i, j, isCapsOn));
            }
        }
    }

    /**
     * 设置键盘显示行
     */
    private void setKeyBarVisible() {
        for (int i = 0; i < keyBars.length; i++) {
            if (crtKeyBar == i) {
                keyBars[i].setVisibility(View.VISIBLE);
            } else {
                keyBars[i].setVisibility(View.GONE);
            }
        }
        setKeyFocuse();
    }

    /**
     * 设置键盘焦点
     */
    private void setKeyFocuse() {
        final TextView key;
        if (selectPosition < keyBars[crtKeyBar].getChildCount()) {
            key = (TextView) keyBars[crtKeyBar].getChildAt(selectPosition);
        } else {
            key = (TextView) keyBars[crtKeyBar].getChildAt(keyBars[crtKeyBar].getChildCount() - 1);
        }
        key.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    key.setBackgroundColor(getResources().getColor(R.color.white));
                    key.setTextColor(getResources().getColor(R.color.black));
                } else {
                    key.setBackgroundColor(getResources().getColor(R.color.black));
                    key.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        key.requestFocus();
    }

    /**
     * 方向下事件
     */
    private void down() {
        if (crtKeyBar == 3) {
            crtKeyBar = 0;
        } else {
            crtKeyBar++;
        }
        setKeyBarVisible();
    }

    /**
     * 方向上事件
     */
    private void up() {
        if (crtKeyBar == 0) {
            crtKeyBar = 3;
        } else {
            crtKeyBar--;
        }
        setKeyBarVisible();
    }

    /**
     * 方向左事件
     */
    private void left() {
        if (selectPosition == 0) {
            selectPosition = keyBars[crtKeyBar].getChildCount() - 1;
        } else {
            selectPosition--;
        }
        setKeyFocuse();
    }

    /**
     * 方向右事件
     */
    private void right() {
        if (selectPosition == keyBars[crtKeyBar].getChildCount() - 1) {
            selectPosition = 0;
        } else {
            selectPosition++;
        }
        setKeyFocuse();
    }

    /**
     * ok事件
     */
    private void ok() {
        String keyValue = Keyboard.getKeyValue(crtKeyBar, selectPosition, isCapsOn);
        InputConnection ic = getCurrentInputConnection();
        if (keyValue.equalsIgnoreCase("CAP")) {
            isCapsOn = !isCapsOn;
            setKeyboardCaps();
        } else if (keyValue.equalsIgnoreCase("DEL")) {
            ic.deleteSurroundingText(1, 0);
        } else {
            ic.commitText(keyValue, 1);
        }
    }
}
