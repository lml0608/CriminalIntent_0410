package com.bignerdranch.android;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import java.util.UUID;

public class CrimeActivity extends SignleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.crime_id";

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }


    /**
     * 重写父类抽象方法。
     * @return fragment
     */
    @Override
    protected Fragment createFragment() {

        //该activity获取上个界面传递过来的i，发送给CrimeFragment
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        return CrimeFragment.newInstance(crimeId);
    }
}
