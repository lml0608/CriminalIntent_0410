package com.bignerdranch.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by lfs-ios on 2017/4/10.
 */

public class CrimeListActivity extends SignleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("CrimeListActivity", "你好");
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        //return R.layout.activity_twopane;//双版面
        return R.layout.activity_masterdetail;//使用别名，refs.xml 单版面
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("CrimeListActivity", "销毁");
    }
}
