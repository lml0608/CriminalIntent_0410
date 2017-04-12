package com.bignerdranch.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
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

    private static final String TAG = "CrimeFragment";

    private static final String ARG_CRIME_ID = "crime_id";

    private static final int REQUEST_CONTACT = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;

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

        setHasOptionsMenu(true);

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
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        //控件
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspect);
        mReportButton = (Button)v.findViewById(R.id.crime_report);
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

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //发送report
//                Intent intent = new Intent(Intent.ACTION_SEND);
//
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                intent = Intent.createChooser(intent, getString(R.string.send_report));
//                startActivity(intent);
                shareReport();
            }
        });



        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }


        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        return v;
    }

    private void shareReport() {
        String mimeTtype = "text/plain";

        ShareCompat.IntentBuilder
                .from(getActivity())
                .setType(mimeTtype)
                .setSubject(getString(R.string.crime_report_subject))
                .setChooserTitle(getString(R.string.send_report))
                .setText(getCrimeReport())
                .startChooser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CONTACT && data != null) {

            Uri contactUri = data.getData();

            Log.i("CrimeFragment", String.valueOf(contactUri));
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null,null,null);

            try {

                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }finally {
                c.close();
            }
        }
    }

    private String getCrimeReport() {

        String solvedString = null;

        if (mCrime.isSolved()) {

            solvedString = getString(R.string.crime_report_solved);

        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";

        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
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

                CrimeLab.get(getActivity()).deleteCirmeById(mCrime);

                Log.i(TAG, String.valueOf(ContactsContract.CommonDataKinds.Phone.CONTENT_URI));
                //ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME   display_name
                Log.i(TAG, String.valueOf(ContactsContract.Contacts.CONTENT_URI));

                getActivity().finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
