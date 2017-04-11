package com.bignerdranch.android;

import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by lfs-ios on 2017/4/10.
 */

public class CrimeListActivity extends SignleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("CrimeListActivity", "销毁");
    }
}
