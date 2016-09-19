package com.yaoyumeng.v2ex2.ui;

import android.os.Bundle;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.ui.fragment.SettingsFragment;
import com.yaoyumeng.v2ex2.ui.swipeback.SwipeBackActivity;

public class SettingsActivity extends SwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        getFragmentManager().beginTransaction().add(R.id.container, new SettingsFragment()).commitAllowingStateLoss();
    }
}
