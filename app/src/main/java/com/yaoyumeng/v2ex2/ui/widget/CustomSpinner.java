package com.yaoyumeng.v2ex2.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.utils.InputUtils;

/**
 * Created by yw on 2015/7/19.
 */
public class CustomSpinner extends TextView {

    private PopupWindow popup = null;
    private CustomSpinner topButton;
    private SearchListView mListView;
    private OnItemSelectedListener mListener;
    private Context mContext;
    private Animation showAnimation;
    private Animation dismissAnimation;

    public boolean isShowPopup() {
        return popup != null ? popup.isShowing() : false;
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        topButton = this;
        initView(mContext);
    }

    private void initView(final Context c) {
        ArrowView arrow = new ArrowView(c, null, topButton);
        topButton.setCompoundDrawables(null, null, arrow.getDrawable(), null);

        // click button text on to popupWindow
        topButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initPopupWindow(c);
            }
        });

        mListView = new SearchListView(c);
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object obj = parent.getItemAtPosition(position);
                topButton.setText(obj.toString());
                dismiss();
                if(mListener != null)
                    mListener.onItemSelected(parent, view, position, id);
            }
        });
    }

    protected void initPopupWindow(Context context) {
        if (popup == null) {
            popup = new PopupWindow(mContext);
            popup.setWidth(topButton.getWidth());
            popup.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            popup.setFocusable(true);
            popup.setHeight(WindowManager.LayoutParams.FILL_PARENT);
            popup.setOutsideTouchable(false);
            popup.setContentView(mListView);
            //解决 popup 弹出输入法被遮挡问题
            popup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        }

        showPop();
    }

    public void showPop() {
        if (!popup.isShowing()) {
            if (showAnimation == null) {
                showAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, -1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f);
                showAnimation.setInterpolator(new AccelerateInterpolator());
                showAnimation.setDuration(100);

            }
            popup.getContentView().startAnimation(showAnimation);

            popup.showAsDropDown(topButton);

            InputUtils.popSoftkeyboard(getContext(), mListView.mSearchView, true);
        }
    }

    public void dismiss() {

        if (popup.isShowing()) {
            if (dismissAnimation == null) {
                dismissAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, -1.0f);
                dismissAnimation.setDuration(150);
                dismissAnimation.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                popup.dismiss();
                            }
                        });

                    }
                });
            }
            popup.getContentView().startAnimation(dismissAnimation);
        }
    }

    public void setAdapter(ArrayAdapter<?> adapter) {
        if (mListView != null) {
            mListView.setAdapter(adapter);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(AdapterView<?> parent, View view, int position, long id);

        void onNothingSelected(AdapterView<?> parent);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mListener = listener;
    }

    class ArrowView extends View {

        private int width;
        private int height;
        protected ShapeDrawable shape;

        public ArrowView(Context context, AttributeSet set, View v) {
            super(context, set);
            // this.mContext = context;
            width = 30;
            height = 20;
            Path p = new Path();
            p.moveTo(0, 0);
            p.lineTo(width, 0);
            p.lineTo(width / 2, height);
            p.lineTo(0, 0);
            shape = new ShapeDrawable(new PathShape(p, width, height));
            shape.getPaint().setColor(Color.BLACK);
            shape.setBounds(0, 0, width, height);
        }

        public void setColor(int color) {
            shape.getPaint().setColor(color);
        }

        protected Drawable getDrawable() {
            Canvas canvas = new Canvas();
            shape.draw(canvas);
            this.draw(canvas);
            return shape;
        }

    }

    class SearchListView extends LinearLayout implements SearchView.OnQueryTextListener {
        private SearchView mSearchView;
        private ListView mListView;
        private ArrayAdapter<?> mAdapter;

        public SearchListView(Context context) {
            super(context);
            init();
        }

        public SearchListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public SearchListView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            setOrientation(VERTICAL);

            inflate(getContext(), R.layout.layout_select_node, this);

            mSearchView = ((SearchView) findViewById(R.id.search));
            mListView = ((ListView) findViewById(R.id.select_node_listview));

            mSearchView.setSubmitButtonEnabled(false);
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setIconifiedByDefault(false);
            mListView.setVisibility(GONE);
        }

        public void setAdapter(ArrayAdapter<?> adapter) {
            if (mAdapter == null) {
                mListView.setVisibility(VISIBLE);
            }
            mAdapter = adapter;
            mListView.setAdapter(adapter);

            mAdapter.getFilter().filter(mSearchView.getQuery());
        }

        public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
            mListView.setOnItemClickListener(listener);
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (mAdapter == null) {
                return true;
            }
            mAdapter.getFilter().filter(newText);
            return true;
        }
    }
}