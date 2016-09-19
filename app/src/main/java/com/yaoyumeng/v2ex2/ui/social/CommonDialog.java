package com.yaoyumeng.v2ex2.ui.social;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yaoyumeng.v2ex2.R;

public class CommonDialog extends Dialog {
    protected FrameLayout container;
    protected View content;
    protected int contentPadding;
    protected TextView headerVw;

    public CommonDialog(Context context) {
        super(context);
        init(context);
    }

    public CommonDialog(Context context, int defStyle) {
        super(context, defStyle);
        contentPadding = (int) getContext().getResources().getDimension(
                R.dimen.global_dialog_padding);
        init(context);
    }

    protected void init(final Context context) {
        setCancelable(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        content = LayoutInflater.from(context).inflate(
                R.layout.dialog_common, null);
        headerVw = (TextView) content.findViewById(R.id.dialog_header);
        container = (FrameLayout) content.findViewById(R.id.content_container);
        super.setContentView(content);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.dismiss();
    }

    public void setContent(View view) {
        setContent(view, contentPadding);
    }

    public void setContent(View view, int padding) {
        container.removeAllViews();
        container.setPadding(padding, padding, padding, padding);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        container.addView(view, lp);
    }

    @Override
    public void setContentView(int i) {
        setContent(null);
    }

    @Override
    public void setContentView(View view) {
        setContentView(null, null);
    }

    @Override
    public void setContentView(View view,
                               LayoutParams layoutparams) {
        throw new Error("Dialog: User setContent (View view) instead!");
    }

    @Override
    public void setTitle(int title) {
        setTitle((getContext().getResources().getString(title)));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title != null && title.length() > 0) {
            headerVw.setText(title);
            headerVw.setVisibility(View.VISIBLE);
        } else {
            headerVw.setVisibility(View.GONE);
        }
    }
}
