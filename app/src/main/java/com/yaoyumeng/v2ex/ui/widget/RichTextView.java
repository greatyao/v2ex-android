package com.yaoyumeng.v2ex.ui.widget;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yaoyumeng.v2ex.Application;
import com.yaoyumeng.v2ex.utils.AsyncImageGetter;
import com.yaoyumeng.v2ex.utils.NetWorkHelper;

import java.util.ArrayList;

/**
 * Created by yw on 2015/5/10.
 */
public class RichTextView extends TextView {

    public RichTextView(Context context) {
        super(context);
    }

    public RichTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setRichText(String text) {

        Spanned spanned = Html.fromHtml(text, new AsyncImageGetter(getContext(), this), null);
        SpannableStringBuilder htmlSpannable;
        if (spanned instanceof SpannableStringBuilder) {
            htmlSpannable = (SpannableStringBuilder) spanned;
        } else {
            htmlSpannable = new SpannableStringBuilder(spanned);
        }

        if (NetWorkHelper.isMobile(getContext()) && !Application.getInstance().isLoadImageInMobileNetwork()) {
            //移动网络情况下如果设置了不显示图片,则遵命
        } else {
            ImageSpan[] spans = htmlSpannable.getSpans(0, htmlSpannable.length(), ImageSpan.class);
            final ArrayList<String> imageUrls = new ArrayList<String>();
            final ArrayList<String> imagePositions = new ArrayList<String>();
            for (ImageSpan currentSpan : spans) {
                final String imageUrl = currentSpan.getSource();
                final int start = htmlSpannable.getSpanStart(currentSpan);
                final int end = htmlSpannable.getSpanEnd(currentSpan);
                imagePositions.add(start + "," + end);
                imageUrls.add(imageUrl);
            }
        }

        super.setText(spanned);
        setMovementMethod(LinkMovementMethod.getInstance());
    }
}
