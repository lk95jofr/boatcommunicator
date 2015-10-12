package com.toonsnet.tools.boatcommunicator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DbAdapter {
    private static final String TAG = "DbAdapter";

    public static final String ROWID           = "_id";
    public static final String TYPE_OF_BOAT    = "typeOfBoat";     // sailing boat
    public static final String BOAT_MODELL     = "boatModell";     // Maxi 77
    public static final String SAIL_NUMBER     = "sailNumber";     // 1003
    public static final String BOAT_NAME       = "boatName";       // bettan
    public static final String HARBOR          = "harbor";         // björksalavarv
    public static final String KRYSSAR_KLUBBEN = "kryssarKlubben"; // 182734
    public static final String SEA_RESCUE      = "seaRescue";      // 858w63
    public static final String OWNER           = "owner";          // Fredrik
    public static final String EMAIL           = "email";          // freddan@email
    public static final String PHONE           = "phone";          // 070-1234567
    public static final String COUNTRY         = "country";        // Sverige
    public static final String FAVORITE        = "favorite";       // true / false
//    public static final String IMAGE           = "image";
    public static final String MODIFIED        = "modified";
    public static final String CREATED         = "created";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "boatcommunicator";
    private static final String BOAT_DATA = "boatdata";
    private static final String PLACES_DATA = "placesdata"; // TODO: 2015-09-30 add a db for favorite places
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static final String DATABASE_CREATE_DATA =
            "CREATE TABLE IF NOT EXISTS " +
                    BOAT_DATA       + " (" +
                    ROWID           + " integer PRIMARY KEY autoincrement," +
                    TYPE_OF_BOAT    + " text not null," +
                    BOAT_MODELL     + " text not null," +
                    SAIL_NUMBER     + " text," +
                    BOAT_NAME       + " text ," +
                    HARBOR          + " text not null," +
                    KRYSSAR_KLUBBEN + " text," +
                    SEA_RESCUE      + " text," +
                    OWNER           + " text not null," +
                    EMAIL           + " text," +
                    PHONE           + " text," +
                    COUNTRY         + " text not null," +
                    FAVORITE        + " text," +
//                    IMAGE           + " blob," +
                    MODIFIED        + " text not null," +
                    CREATED         + " text not null);";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, DATABASE_CREATE_DATA);
            db.execSQL(DATABASE_CREATE_DATA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO: 2015-09-30 don't destroy data, make backup
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + BOAT_DATA);
            onCreate(db);
        }
    }

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createBoat(String typeOfBoat,
                           String boatModell,
                           String sailNumber,
                           String boatName,
                           String harbor,
                           String kryssarKlubben,
                           String seaRescue,
                           String owner,
                           String email,
                           String phone,
                           String country,
                           String favorite) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(TYPE_OF_BOAT, typeOfBoat);
        initialValues.put(BOAT_MODELL, boatModell);
        initialValues.put(SAIL_NUMBER, sailNumber);
        initialValues.put(BOAT_NAME, boatName);
        initialValues.put(HARBOR, harbor);
        initialValues.put(KRYSSAR_KLUBBEN, kryssarKlubben);
        initialValues.put(SEA_RESCUE, seaRescue);
        initialValues.put(OWNER, owner);
        initialValues.put(EMAIL, email);
        initialValues.put(PHONE, phone);
        initialValues.put(COUNTRY, country);
        initialValues.put(FAVORITE, favorite);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = Calendar.getInstance().getTime();

        initialValues.put(MODIFIED, df.format(today));
        initialValues.put(CREATED, df.format(today));

//        initialValues.put(MyBaseColumn.MyTable.ImageField, EntityUtils.toByteArray(entity));
//        mDb.insert(SQLITE_TABLE_IMAGE, values);

        return mDb.insert(BOAT_DATA, null, initialValues);
    }

    public boolean deleteAllBoats() {

        int doneDelete = 0;
        doneDelete = mDb.delete(BOAT_DATA, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public Cursor fetchBoatsByFilter(String inputText) throws SQLException {
        Log.i(TAG, inputText);
        Cursor mCursor = null;

        if (inputText == null || inputText.trim().length() == 0)  {
            mCursor = mDb.query(BOAT_DATA, new String[] {
                            ROWID, TYPE_OF_BOAT, BOAT_MODELL, SAIL_NUMBER, BOAT_NAME, HARBOR, KRYSSAR_KLUBBEN, SEA_RESCUE,
                            OWNER, EMAIL, PHONE, COUNTRY, FAVORITE, MODIFIED, CREATED},
                    null, null, null, null, null);

        } else {
            String[] columns = new String[] {
                    ROWID, TYPE_OF_BOAT, BOAT_MODELL, SAIL_NUMBER, BOAT_NAME, HARBOR, KRYSSAR_KLUBBEN, SEA_RESCUE,
                    OWNER, EMAIL, PHONE, COUNTRY, FAVORITE, MODIFIED, CREATED};
/*
            String selection =
                    "(" + TYPE_OF_BOAT + " like '%" + inputText + "%') or " +
                            "(" + BOAT_MODELL + " like '%" + inputText + "%') or " +
                            "(" + SAIL_NUMBER + " like '%" + inputText + "%') or " +
                            "(" + BOAT_NAME + " like '%" + inputText + "%') or " +
                            "(" + HARBOR + " like '%" + inputText + "%') or " +
                            "(" + KRYSSAR_KLUBBEN + " like '%" + inputText + "%') or " +
                            "(" + SEA_RESCUE + " like '%" + inputText + "%') or " +
                            "(" + OWNER + " like '%" + inputText + "%') or " +
                            "(" + EMAIL + " like '%" + inputText + "%') or " +
                            "(" + PHONE + " like '%" + inputText + "%') or " +
                            "(" + COUNTRY + " like '%" + inputText + "%') or " +
                            "(" + FAVORITE + " like '%" + inputText + "%') or " +
                            "(modified like '%" + inputText + "%') or " +
                            "(created like '%" + inputText + "%')";
*/
            String selection = BOAT_MODELL + " like '%" + inputText + "%'";

            mCursor = mDb.query(true, BOAT_DATA, columns, selection, null, null, null, null, null);
        }

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public Cursor fetchAllBoats() {
        Log.i(TAG, "fetchAllBoats");
        return fetchBoatsByFilter("");
    }

    public void insertSomeBoats() {
        createBoat("Segelbåt", "Maxi 77", "1003", "Bettan", "Björksalavarv", "", "", "Fredrik0", "freddan@email", "070-123456", "Sverige", "true");
        createBoat("Segelbåt", "Maxi 77", "1004", "Bettan", "Björksalavarv", "1234", "", "Fredrik1", "freddan@email", "070-123456", "Sverige", "true");
        createBoat("Segelbåt", "Maxi 77", "1005", "Bettan", "Björksalavarv", "", "", "Fredrik2", "freddan@email", "070-123456", "Sverige", "true");
        createBoat("Segelbåt", "Maxi 77", "1006", "Bettan", "Björksalavarv", "", "", "Fredrik3", "freddan@email", "070-123456", "Sverige", "true");
        createBoat("Segelbåt", "Maxi 77", "1107", "Bettan", "Björksalavarv", "", "", "Fredrik4", "freddan@email", "070-123456", "Sverige", "true");
        createBoat("Segelbåt", "Maxi 77", "1203", "Bettan", "Björksalavarv", "", "a2ef45", "Fredrik5", "freddan@email", "070-123456", "Sverige", "true");
        createBoat("Segelbåt", "Maxi 77", "1303", "Bettan", "Björksalavarv", "", "", "Fredrik6", "freddan@email", "070-123456", "Sverige", "true");
        createBoat("Segelbåt", "Maxi 77", "1403", "Bettan", "Björksalavarv", "", "", "Fredrik7", "freddan@email", "070-123456", "Sverige", "true");
        createBoat("Segelbåt", "Maxi 77", "1503", "Bettan", "Björksalavarv", "", "", "Fredrik8", "freddan@email", "070-123456", "Sverige", "true");
        createBoat("Segelbåt", "Maxi 77", "1603", "Bettan", "Björksalavarv", "", "", "Fredrik9", "freddan@email", "070-123456", "Sverige", "true");
    }
}