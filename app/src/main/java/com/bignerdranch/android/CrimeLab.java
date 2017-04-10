package com.bignerdranch.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.database.CrimeBaseHelper;
import com.bignerdranch.android.database.CrimeCursorWrapper;
import com.bignerdranch.android.database.CrimeDbSchema;
import com.bignerdranch.android.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lfs-ios on 2017/4/10.
 */

public class CrimeLab {

    private Context mContext;

    private SQLiteDatabase mDatabase;

    private static CrimeLab sCrimeLab;


    private CrimeLab(Context context) {

        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {

        if (sCrimeLab == null) {

            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }


    /**
     * 添加crime
     * @param crime
     */
    public void addCrime(Crime crime) {

        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }



    //返回查询出来的所有crime对象
    private CrimeCursorWrapper queryCrime(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new CrimeCursorWrapper(cursor);
    }




    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();

        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        return values;
    }


    /**
     * 返回Crime对象类型集合
     * @return
     */
    public List<Crime> getCrimes() {

        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrime(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            crimes.add(cursor.getCrime());
            cursor.moveToNext();
        }
        cursor.close();
        return crimes;
    }

    /**
     * 返回指定id的Crime对象
     * @param id
     * @return
     */
    public Crime getCrime(UUID id) {

        CrimeCursorWrapper cursor = queryCrime(CrimeTable
                .Cols.UUID + "= ?", new String[]{id.toString()});

        try{
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }

    }


    /**
     * 更新
     * @param crime
     */
    public void updateCrime(Crime crime) {

        String uuidString = crime.getId().toString();

        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }



}
