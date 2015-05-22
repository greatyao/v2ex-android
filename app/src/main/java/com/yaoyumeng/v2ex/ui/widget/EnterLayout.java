package com.yaoyumeng.v2ex.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.utils.InputUtils;
import com.yaoyumeng.v2ex.utils.SimpleTextWatcher;

public class EnterLayout {

    private Context mContext;
    private View mRootView;
    public TextView sendText;
    public ImageButton send;
    public EditText content;

    public EnterLayout(Context context, View rootView, View.OnClickListener sendTextOnClick) {
        mRootView = rootView;
        mContext = context;
        sendText = (TextView) mRootView.findViewById(R.id.sendText);
        sendText.setOnClickListener(sendTextOnClick);
        sendText.setVisibility(View.VISIBLE);

        send = (ImageButton) mRootView.findViewById(R.id.send);
        send.setVisibility(View.GONE);

        content = (EditText) mRootView.findViewById(R.id.comment);
        content.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateSendButtonStyle();
            }
        });
        content.setText("");
    }

    public void updateSendButtonStyle() {
        if (sendButtonEnable()) {
            sendText.setBackgroundResource(R.drawable.edit_send_green);
            sendText.setTextColor(0xffffffff);
        } else {
            sendText.setBackgroundResource(R.drawable.edit_send);
            sendText.setTextColor(0xff999999);
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

    public void deleteOneChar() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        content.dispatchKeyEvent(event);
    }

    public void clearContent() {
        content.setText("");
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
