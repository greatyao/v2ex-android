package com.yaoyumeng.v2ex2.ui.widget;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.yaoyumeng.v2ex2.utils.Html;
import com.yaoyumeng.v2ex2.Application;
import com.yaoyumeng.v2ex2.ui.PhotoViewActivity;
import com.yaoyumeng.v2ex2.utils.AsyncImageGetter;
import com.yaoyumeng.v2ex2.utils.NetWorkHelper;

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

        setTextIsSelectable(true);

        //移动网络情况下如果设置了不显示图片,则遵命
        if (NetWorkHelper.isMobile(getContext()) && !Application.getInstance().isLoadImageInMobileNetworkFromCache()) {
            super.setText(Html.fromHtml(text));
            setMovementMethod(LinkMovementMethod.getInstance());
            return;
        }

        Spanned spanned = Html.fromHtml(text, new AsyncImageGetter(getContext(), this));
        SpannableStringBuilder htmlSpannable;
        if (spanned instanceof SpannableStringBuilder) {
            htmlSpannable = (SpannableStringBuilder) spanned;
        } else {
            htmlSpannable = new SpannableStringBuilder(spanned);
        }

        ImageSpan[] spans = htmlSpannable.getSpans(0, htmlSpannable.length(), ImageSpan.class);
        final ArrayList<String> imageUrls = new ArrayList<String>();
        final ArrayList<String> imagePositions = new ArrayList<String>();
        for (ImageSpan span : spans) {
            final String imageUrl = span.getSource();
            final int start = htmlSpannable.getSpanStart(span);
            final int end = htmlSpannable.getSpanEnd(span);
            imagePositions.add(start + "/" + end);
            imageUrls.add(imageUrl);
        }

        for(ImageSpan span : spans){
            final int start = htmlSpannable.getSpanStart(span);
            final int end   = htmlSpannable.getSpanEnd(span);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    PhotoViewActivity.launch(getContext(), imagePositions.indexOf(start + "/" + end), imageUrls);
                }
            };

            ClickableSpan[] clickSpans = htmlSpannable.getSpans(start, end, ClickableSpan.class);
            if(clickSpans != null && clickSpans.length != 0) {

                for(ClickableSpan c_span : clickSpans) {
                    htmlSpannable.removeSpan(c_span);
                }
            }

            htmlSpannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        super.setText(htmlSpannable);
        setMovementMethod(LinkMovementMethod.getInstance());
    }
}
