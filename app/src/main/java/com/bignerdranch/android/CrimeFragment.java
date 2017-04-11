package com.bignerdranch.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.UUID;

/**
 * Created by lfs-ios on 2017/4/10.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    /**
     * 携带id传递
     * @param crimeId
     * @return
     */
    public static CrimeFragment newInstance(UUID crimeId) {

        Bundle args = new Bundle();

        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        //根绝actvity返回的id 获取指定id的crime对象
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    //用 可能会在CrimeFragment中修改Crime实例。修改完成后，我们 要 新CrimeLab中的
    //Crime  。这可  过在CrimeFragment.java中  CrimeFragment.onPause() 法完成
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        //控件
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        //设置控件的值
        mTitleField.setText(mCrime.getTitle());
        //输入框
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //mTitleField内容改变前
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //mTitleField内容发生改变时
                //设置crime对象的title值为mTitleField控件内容
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //mTitleField内容改变后
            }
        });
        //时间按钮
        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setEnabled(true);

        //设置获取的状态
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        //checkbox监听事件
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_crime_setting, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_item_delete_crime:
                //删除当前viewpage显示的crime



                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
