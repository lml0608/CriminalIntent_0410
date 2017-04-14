package com.bignerdranch.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

/**
 * Created by lfs-ios on 2017/4/10.
 */

public class CrimeFragment extends Fragment {

    private static final String TAG = "CrimeFragment";

    private static final String ARG_CRIME_ID = "crime_id";

    private static final int REQUEST_CONTACT = 1;

    private static final int REQUEST_PHOTO= 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private File mPhotoFile;
    private ImageButton mPhotoButton;
    private Bitmap mPhoto;
    private ImageView mPhotoView;

    private Callbacks mCallbacks;

    public interface Callbacks {

        void onCrimeUpdated(Crime crime);
    }



    /**
     * 携带id传递
     *
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        //根绝actvity返回的id 获取指定id的crime对象
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        //获取该Crime的图片文件
        mPhotoFile = CrimeLab.get(getActivity())
                .getPhotoFile(mCrime);
        ///storage/0403-0201/Android/data/com.bignerdranch.android/files/Pictures/IMG_47cf76b4-4ba9-41fc-8d18-cc68531fd0b2.jpg

        Log.i(TAG, "图片=" + mPhotoFile);
    }

    //用 可能会在CrimeFragment中修改Crime实例。修改完成后，我们 要 新CrimeLab中的
    //Crime  。这可  过在CrimeFragment.java中  CrimeFragment.onPause() 法完成
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        //控件
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mReportButton = (Button) v.findViewById(R.id.crime_report);
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
                updateCrime();
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
                updateCrime();
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


        mCallButton = (Button) v.findViewById(R.id.crime_call);

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拨打电话 action_dial

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 0);
                } else {
                    String phoneNumber = getphone(mCrime.getSuspect());

                    callCrime(phoneNumber);

                    Log.i(TAG, "phoneNumber = " + phoneNumber);
                }


            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_camera);

        final Intent captrueImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captrueImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captrueImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captrueImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();

        return v;
    }

    private void updatePhotoView() {

        if (mPhotoFile == null || !mPhotoFile.exists()) {

            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    //拨打电话
    private void callCrime( String number) {

        Intent i = new Intent(Intent.ACTION_DIAL);

        i.setData(Uri.parse("tel:" + number));

        startActivity(i);


    }

    //查询电话号码

    private String getphone(String name) {

        String phoneNumber = null;
        if (name != null) {


            //使用ContentResolver查找联系人数据
            Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            //遍历查询结果，找到所需号码
            while (cursor.moveToNext()) {
                //获取联系人ID
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //获取联系人的名字
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (name.equals(contactName)) {
                    //使用ContentResolver查找联系人的电话号码
                    Cursor phone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    if (phone.moveToNext()) {
                        phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.d(TAG, "电话：" + phoneNumber);
                        break;
                    }
                }
            }
            return phoneNumber;
        }

        return null;

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
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {

                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updateCrime();
            updatePhotoView();


        }
    }

    private void updateCrime() {

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);

        mCallbacks.onCrimeUpdated(mCrime);
    }


    private String getCrimeReport() {

        String solvedString = null;

        if (mCrime.isSolved()) {

            solvedString = getString(R.string.crime_report_solved);

        } else {
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

                getActivity().finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                String phoneNumber = getphone(mCrime.getSuspect());

                callCrime(phoneNumber);

                Log.i(TAG, "phoneNumber = " + phoneNumber);

            } else {

                Toast.makeText(getActivity(), "没有授权不能使用！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
