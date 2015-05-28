package com.yaoyumeng.v2ex.ui;

import android.os.Bundle;
import android.view.MenuItem;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.R;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.hannesdorfmann.swipeback.transformer.SlideSwipeBackTransformer;

/**
 * Created by yw on 2015/5/28.
 */
public class SwipeBackActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle saved){
        super.onCreate(saved);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        overridePendingTransition(R.anim.swipeback_stack_right_in,
                R.anim.swipeback_stack_to_back);
    }

    public void setSwipeContentView(int layoutResID) {
        SwipeBack.attach(this, Position.LEFT)
                .setDrawOverlay(true)
                .setDividerEnabled(true) // Must be called to enable, setDivider() is not enough
                .setSwipeBackTransformer(new SlideSwipeBackTransformer())
                .setContentView(layoutResID)
                .setSwipeBackView(com.yaoyumeng.v2ex.R.layout.layout_swipeback_default);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

