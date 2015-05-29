package com.yaoyumeng.v2ex.ui;

import android.os.Bundle;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.ui.fragment.SettingsFragment;

public class SettingsActivity extends SwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeContentView(R.layout.activity_container);

        getFragmentManager().beginTransaction().add(R.id.container, new SettingsFragment()).commit();
    }
}
