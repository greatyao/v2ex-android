package com.yaoyumeng.v2ex2.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.utils.InputUtils;
import com.yaoyumeng.v2ex2.utils.SimpleTextWatcher;

public class EnterLayout {

    private Context mContext;
    private View mRootView;
    public ImageButton send;
    public EditText content;
    private String hint;

    public EnterLayout(Context context, View rootView, View.OnClickListener sendTextOnClick) {
        mRootView = rootView;
        mContext = context;

        send = (ImageButton) mRootView.findViewById(R.id.send);
        send.setOnClickListener(sendTextOnClick);
        send.setVisibility(View.VISIBLE);

        content = (EditText) mRootView.findViewById(R.id.comment);
        content.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateSendButtonStyleAndHint();
            }
        });
        content.setText("");
    }

    public void updateSendButtonStyleAndHint() {
        if (sendButtonEnable()) {
            send.setEnabled(true);
        } else {
            send.setEnabled(false);
            content.setHint(hint);
        }
    }

    protected boolean sendButtonEnable() {
        return content.getText().length() > 0;
    }

    public void hideKeyboard() {
        InputUtils.popSoftkeyboard(mContext, content, false);
    }

    public void popKeyboard() {
        content.requestFocus();
        InputUtils.popSoftkeyboard(mContext, content, true);
    }

    public void insertText(String s) {
        content.requestFocus();
        int insertPos = content.getSelectionStart();

        String insertString = s + " ";
        Editable editable = content.getText();
        editable.insert(insertPos, insertString);
    }

    public void setText(String s) {
        content.requestFocus();
        Editable editable = content.getText();
        editable.clear();
        editable.insert(0, s);
    }

    public void setDefaultHint(String hint) {
        this.hint = hint;
    }

    public void deleteOneChar() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        content.dispatchKeyEvent(event);
    }

    public void clearContent() {
        content.setText("");
        content.setHint(hint);
    }

    public String getContent() {
        return content.getText().toString();
    }

    public void hide() {
        View root = mRootView.findViewById(R.id.commonEnterRoot);
        root.setVisibility(View.GONE);
    }

    public void show() {
        View root = mRootView.findViewById(R.id.commonEnterRoot);
        root.setVisibility(View.VISIBLE);
    }

    public void restoreSaveStart() {
        content.addTextChangedListener(restoreWatcher);
    }

    public void restoreSaveStop() {
        content.removeTextChangedListener(restoreWatcher);
    }

    private TextWatcher restoreWatcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            Object tag = content.getTag();
            if (tag == null) {
                return;
            }
        }
    };

}
