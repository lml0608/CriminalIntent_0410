package com.bignerdranch.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by lfs-ios on 2017/4/10.
 */

//implements CrimeListFragment.Callbacks 并实现接口的方法
public class CrimeListActivity extends SignleFragmentActivity
    implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{

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

    //实现接口中的方法

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {

            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {

            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }

    }

    @Override
    public void onCrimeUpdated(Crime crime) {

        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
