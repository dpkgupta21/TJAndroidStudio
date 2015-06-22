package com.traveljar.memories.SQLitedatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {


    // Table Contact fields
    public static final String TABLE_CONTACT = "Contact";
    public static final String CONTACT_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String CONTACT_COLUMN_NAME = "name";
    public static final String CONTACT_COLUMN_EMAIL = "emailAddress";
    public static final String CONTACT_COLUMN_PIC_SERVER_URL = "picServerURL";
    public static final String CONTACT_COLUMN_PIC_LOCAL_URL = "picLocalURL";
    public static final String CONTACT_COLUMN_PHONE = "phone_no";
    public static final String CONTACT_COLUMN_ALL_JIDS = "allJourneyIds";
    public static final String CONTACT_COLUMN_INTERESTS = "interests";
    public static final String CONTACT_COLUMN_ISONBOARD = "isOnBoard";
    public static final String CONTACT_COLUMN_STATUS = "status";
    // Table JOURNEY fields
    public static final String TABLE_JOURNEY = "JOURNEY";
    public static final String JOURNEY_COLUMN_ID = "_id";
    public static final String JOURNEY_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String JOURNEY_COLUMN_NAME = "name";
    public static final String JOURNEY_COLUMN_TAGLINE = "tagLine";
    public static final String JOURNEY_COLUMN_CREATEDBY = "createdBy";
    public static final String JOURNEY_COLUMN_GROUPTYPE = "groupType";
    public static final String JOURNEY_COLUMN_BUDDY_IDS = "buddyIds";
    public static final String JOURNEY_COLUMN_JOURNEY_LAPS = "journeyLapIds";
    public static final String JOURNEY_COLUMN_STATUS = "journeyStatus";
    public static final String JOURNEY_COLUMN_CREATED_AT = "createdAt";
    public static final String JOURNEY_COLUMN_UPDATED_AT = "updatedAt";
    public static final String JOURNEY_COLUMN_COMPELTED_AT = "completedAt";

    // Table TIMELINE fields
    public static final String TABLE_TIMELINE = "TIMELINE";
    public static final String TIMELINE_COLUMN_ID = "_id";
    public static final String TIMELINE_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String TIMELINE_COLUMN_JID = "journeyId";
    public static final String TIMELINE_COLUMN_MEM_ID = "memorableId";
    public static final String TIMELINE_COLUMN_MEM_TYPE = "memorableType";
    public static final String TIMELINE_COLUMN_CREATED_AT = "createdAt";
    public static final String TIMELINE_COLUMN_UPDATED_AT = "updatedAt";
    // Table PICTURE fields
    public static final String TABLE_PICTURE = "PICTURE";
    public static final String PICTURE_COLUMN_ID = "_id";
    public static final String PICTURE_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String PICTURE_COLUMN_JID = "jId";
    public static final String PICTURE_COLUMN_MEM_TYPE = "memoryType";
    public static final String PICTURE_COLUMN_CAPTION = "caption";
    public static final String PICTURE_COLUMN_EXT = "extension";
    public static final String PICTURE_COLUMN_SIZE = "size";
    public static final String PICTURE_COLUMN_DATASERVERURL = "dataServerURL";
    public static final String PICTURE_COLUMN_DATALOCALURL = "dataLocalURL";
    public static final String PICTURE_COLUMN_CREATEDBY = "createdBy";
    public static final String PICTURE_COLUMN_CREATEDAT = "createdAt";
    public static final String PICTURE_COLUMN_UPDATEDAT = "updatedAt";
    public static final String PICTURE_COLUMN_LIKEDBY = "likedBy";
    public static final String PICTURE_CLOUMN_LOCALTHUMBNAILPATH = "thumbnailPath";
    public static final String PICTURE_COLUMN_LATITUDE = "latitude";
    public static final String PICTURE_CLOUMN_LONGITUDE = "longitude";
    //	Table Audio fields
    public static final String TABLE_AUDIO = "AUDIO";
    public static final String VOICE_COLUMN_ID = "_id";
    public static final String VOICE_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String VOICE_COLUMN_JID = "jId";
    public static final String VOICE_COLUMN_MEM_TYPE = "memoryType";
    public static final String VOICE_COLUMN_EXT = "extension";
    public static final String VOICE_COLUMN_SIZE = "size";
    public static final String VOICE_COLUMN_DATASERVERURL = "dataServerURL";
    public static final String VOICE_COLUMN_DATALOCALURL = "dataLocalURL";
    public static final String VOICE_COLUMN_CREATEDBY = "createdBy";
    public static final String VOICE_COLUMN_CREATEDAT = "createdAt";
    public static final String VOICE_COLUMN_UPDATEDAT = "updatedAt";
    public static final String VOICE_COLUMN_LIKEDBY = "likedBy";
    public static final String VOICE_COLUMN_LATITUDE = "latitude";
    public static final String VOICE_CLOUMN_LONGITUDE = "longitude";
    //	Table Videos fields
    public static final String TABLE_VIDEO = "VIDEO";
    public static final String VIDEO_COLUMN_ID = "_id";
    public static final String VIDEO_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String VIDEO_COLUMN_JID = "journeyId";
    public static final String VIDEO_COLUMN_MEM_TYPE = "memoryType";
    public static final String VIDEO_COLUMN_CAPTION = "caption";
    public static final String VIDEO_COLUMN_EXT = "extension";
    public static final String VIDEO_COLUMN_SIZE = "size";
    public static final String VIDEO_COLUMN_DATASERVERURL = "dataServerURL";
    public static final String VIDEO_COLUMN_DATALOCALURL = "dataLocalURL";
    public static final String VIDEO_COLUMN_CREATED_BY = "createdBy";
    public static final String VIDEO_COLUMN_CREATED_AT = "createdAt";
    public static final String VIDEO_COLUMN_UPDATED_AT = "updatedAt";
    public static final String VIDEO_COLUMN_LIKED_BY = "likedBy";
    public static final String VIDEO_CLOUMN_LOCALTHUMBNAILPATH = "thumbnailPath";
    public static final String VIDEO_COLUMN_LATITUDE = "latitude";
    public static final String VIDEO_CLOUMN_LONGITUDE = "longitude";
    //	Table Moods fields
    public static final String TABLE_MOOD = "MOOD";
    public static final String MOOD_COLUMN_ID = "_id";
    public static final String MOOD_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String MOOD_COLUMN_JID = "journeyId";
    public static final String MOOD_COLUMN_MEM_TYPE = "memoryType";
    public static final String MOOD_COLUMN_CREATED_BY = "createdBy";
    public static final String MOOD_COLUMN_CREATED_AT = "createdAt";
    public static final String MOOD_COLUMN_UPDATED_AT = "updatedAt";
    public static final String MOOD_COLUMN_FRIENDS_ID = "friendsId";
    public static final String MOOD_COLUMN_LIKED_BY = "likedBy";
    public static final String MOOD_COLUMN_MOOD = "mood";
    public static final String MOOD_COLUMN_REASON = "reason";
    public static final String MOOD_COLUMN_LATITUDE = "latitude";
    public static final String MOOD_CLOUMN_LONGITUDE = "longitude";
    // Checkin Table fields
    public static final String TABLE_CHECKIN = "CHECKIN";
    public static final String CHECKIN_COLUMN_ID = "_id";
    public static final String CHECKIN_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String CHECKIN_COLUMN_JID = "jId";
    public static final String CHECKIN_COLUMN_MEM_TYPE = "memoryType";
    public static final String CHECKIN_COLUMN_CAPTION = "caption";
    public static final String CHECKIN_COLUMN_LATITUDE = "latitude";
    public static final String CHECKIN_COLUMN_LONGITUDE = "longitude";
    public static final String CHECKIN_COLUMN_PLACE_NAME = "checkInPlaceName";
    public static final String CHECKIN_COLUMN_PIC_URL = "checkInPicURL";
    public static final String CHECKIN_COLUMN_WITH = "checkInWith";
    public static final String CHECKIN_COLUMN_CREATED_BY = "createdBy";
    public static final String CHECKIN_COLUMN_CREATED_AT = "createdAt";
    public static final String CHECKIN_COLUMN_UPDATED_AT = "updatedAt";
    public static final String CHECKIN_COLUMN_LIKED_BY = "likedBy";
    //NOTE TABLE
    public static final String TABLE_NOTES = "NOTE";
    public static final String NOTES_COLUMN_ID = "_id";
    public static final String NOTES_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String NOTES_COLUMN_JID = "journeyId";
    public static final String NOTES_COLUMN_MEM_TYPE = "memoryType";
    public static final String NOTES_COLUMN_CAPTION = "caption";
    public static final String NOTES_COLUMN_CONTENT = "content";
    public static final String NOTES_COLUMN_CREATED_BY = "createdBy";
    public static final String NOTES_COLUMN_CREATED_AT = "createdAt";
    public static final String NOTES_COLUMN_UPDATED_AT = "updatedAt";
    public static final String NOTES_COLUMN_LIKED_BY = "likedBy";
    public static final String NOTES_COLUMN_LATITUDE = "latitude";
    public static final String NOTES_CLOUMN_LONGITUDE = "longitude";

    //table place
    public static final String TABLE_PLACE = "PLACE";
    public static final String PLACE_COLUMN_ID = "_id";
    public static final String PLACE_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String PLACE_COLUMN_ID_ON_GOOGLE = "idOnGoogle";
    public static final String PLACE_COLUMN_COUNTRY = "country";
    public static final String PLACE_COLUMN_STATE = "state";
    public static final String PLACE_COLUMN_CITY = "city";
    public static final String PLACE_COLUMN_CREATED_BY = "createdBy";
    public static final String PLACE_COLUMN_CREATED_AT = "createdAt";

    //Table Likes
    public static final String TABLE_LIKE = "LIKE";
    public static final String LIKE_COLUMN_ID = "_id";
    public static final String LIKE_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String LIKE_COLUMN_JOURNEY_ID = "journeyId";
    public static final String LIKE_COLUMN_MEMORABLE_ID = "memorableId";
    public static final String LIKE_COLUMN_USER_ID = "userId";
    public static final String LIKE_COLUMN_MEM_TYPE = "memType";

    //Table requests
    public static final String TABLE_REQUEST = "REQUEST";
    public static final String REQUEST_COLUMN_ID = "_id";
    public static final String REQUEST_COLUMN_REQUEST_TYPE = "idOnServer";
    public static final String REQUEST_COLUMN_JOURNEY_ID = "journeyId";


    private static final String CREATE_TABLE_LIKE = "create table " + TABLE_LIKE + "("
            + LIKE_COLUMN_ID + " integer primary key autoincrement, "
            + LIKE_COLUMN_ID_ONSERVER + " text , "
            + LIKE_COLUMN_JOURNEY_ID + " text ,"
            + LIKE_COLUMN_MEMORABLE_ID + " text,"
            + LIKE_COLUMN_USER_ID + " text ,"
            + LIKE_COLUMN_MEM_TYPE + " text " + ");";

    private static final String CREATE_TABLE_CONTACT = "create table " + TABLE_CONTACT + "("
            + CONTACT_COLUMN_ID_ONSERVER + " text primary key, "
            + CONTACT_COLUMN_NAME + " text ,"
            + CONTACT_COLUMN_EMAIL + " text,"
            + CONTACT_COLUMN_PIC_SERVER_URL + " text ,"
            + CONTACT_COLUMN_PIC_LOCAL_URL + " text ,"
            + CONTACT_COLUMN_PHONE + " text ,"
            + CONTACT_COLUMN_ALL_JIDS + " text ,"
            + CONTACT_COLUMN_INTERESTS + " text ,"
            + CONTACT_COLUMN_STATUS + " text ,"
            + CONTACT_COLUMN_ISONBOARD + " integer " + ");";
    private static final String CREATE_TABLE_JOURNEY = "create table " + TABLE_JOURNEY + "("
            + JOURNEY_COLUMN_ID + " integer primary key autoincrement, "
            + JOURNEY_COLUMN_ID_ONSERVER + " text, "
            + JOURNEY_COLUMN_NAME + " text ,"
            + JOURNEY_COLUMN_TAGLINE + " text,"
            + JOURNEY_COLUMN_CREATEDBY + " text ,"
            + JOURNEY_COLUMN_GROUPTYPE + " text ,"
            + JOURNEY_COLUMN_BUDDY_IDS + " text ,"
            + JOURNEY_COLUMN_JOURNEY_LAPS + " text ,"
            + JOURNEY_COLUMN_CREATED_AT + " integer ,"
            + JOURNEY_COLUMN_UPDATED_AT + " integer ,"
            + JOURNEY_COLUMN_COMPELTED_AT + " integer ,"
            + JOURNEY_COLUMN_STATUS + " text " + ");";

    private static final String CREATE_TABLE_TIMELINE = "create table " + TABLE_TIMELINE + "("
            + TIMELINE_COLUMN_ID + " integer primary key autoincrement, "
            + TIMELINE_COLUMN_ID_ONSERVER + " text ,"
            + TIMELINE_COLUMN_JID + " text ,"
            + TIMELINE_COLUMN_MEM_ID + " text ,"
            + TIMELINE_COLUMN_MEM_TYPE + " text ,"
            + TIMELINE_COLUMN_CREATED_AT + " text ,"
            + TIMELINE_COLUMN_UPDATED_AT + " text" + " );";
    private static final String CREATE_TABLE_PICTURE = "create table " + TABLE_PICTURE + "("
            + PICTURE_COLUMN_ID + " integer primary key autoincrement, "
            + PICTURE_COLUMN_ID_ONSERVER + " text, "
            + PICTURE_COLUMN_JID + " text, "
            + PICTURE_COLUMN_MEM_TYPE + " text, "
            + PICTURE_COLUMN_CAPTION + " text ,"
            + PICTURE_COLUMN_EXT + " text,"
            + PICTURE_COLUMN_SIZE + " text,"
            + PICTURE_COLUMN_DATASERVERURL + " text ,"
            + PICTURE_COLUMN_DATALOCALURL + " text ,"
            + PICTURE_COLUMN_CREATEDBY + " text ,"
            + PICTURE_COLUMN_CREATEDAT + " text ,"
            + PICTURE_COLUMN_UPDATEDAT + " text ,"
            + PICTURE_CLOUMN_LOCALTHUMBNAILPATH + " text ,"
            + PICTURE_COLUMN_LATITUDE + " text ,"
            + PICTURE_CLOUMN_LONGITUDE + " real ,"
            + PICTURE_COLUMN_LIKEDBY + " real " + ");";

    private static final String CREATE_TABLE_AUDIO = "create table if not exists " + TABLE_AUDIO + "("
            + VOICE_COLUMN_ID + " integer primary key autoincrement, "
            + VOICE_COLUMN_ID_ONSERVER + " text, "
            + VOICE_COLUMN_JID + " text, "
            + VOICE_COLUMN_MEM_TYPE + " text,"
            + VOICE_COLUMN_EXT + " text,"
            + VOICE_COLUMN_SIZE + " integer,"
            + VOICE_COLUMN_DATASERVERURL + " text ,"
            + VOICE_COLUMN_DATALOCALURL + " text ,"
            + VOICE_COLUMN_CREATEDBY + " text ,"
            + VOICE_COLUMN_CREATEDAT + " integer ,"
            + VOICE_COLUMN_UPDATEDAT + " integer ,"
            + VOICE_COLUMN_LATITUDE + " real ,"
            + VOICE_CLOUMN_LONGITUDE + " real ,"
            + VOICE_COLUMN_LIKEDBY + " text " + ");";

    private static final String CREATE_TABLE_VIDEO = "create table if not exists " + TABLE_VIDEO + "("
            + VIDEO_COLUMN_ID + " integer primary key autoincrement, "
            + VIDEO_COLUMN_ID_ONSERVER + " text, "
            + VIDEO_COLUMN_JID + " text,"
            + VIDEO_COLUMN_MEM_TYPE + " text,"
            + VIDEO_COLUMN_CAPTION + " text,"
            + VIDEO_COLUMN_EXT + " text,"
            + VIDEO_COLUMN_SIZE + " integer,"
            + VIDEO_COLUMN_DATASERVERURL + " text ,"
            + VIDEO_COLUMN_DATALOCALURL + " text ,"
            + VIDEO_COLUMN_CREATED_BY + " text ,"
            + VIDEO_COLUMN_CREATED_AT + " integer ,"
            + VIDEO_COLUMN_UPDATED_AT + " integer ,"
            + VIDEO_CLOUMN_LOCALTHUMBNAILPATH + " text ,"
            + VIDEO_COLUMN_LATITUDE + " real ,"
            + VIDEO_CLOUMN_LONGITUDE + " real ,"
            + VIDEO_COLUMN_LIKED_BY + " text " + ");";

    private static final String CREATE_TABLE_MOOD = "create table if not exists " + TABLE_MOOD + "("
            + MOOD_COLUMN_ID + " integer primary key autoincrement, "
            + MOOD_COLUMN_ID_ONSERVER + " text, "
            + MOOD_COLUMN_JID + " text ,"
            + MOOD_COLUMN_MEM_TYPE + " text ,"
            + MOOD_COLUMN_CREATED_BY + " text ,"
            + MOOD_COLUMN_CREATED_AT + " integer ,"
            + MOOD_COLUMN_UPDATED_AT + " integer ,"
            + MOOD_COLUMN_FRIENDS_ID + " text ,"
            + MOOD_COLUMN_MOOD + " text ,"
            + MOOD_COLUMN_REASON + " text ,"
            + MOOD_COLUMN_LATITUDE + " real ,"
            + MOOD_CLOUMN_LONGITUDE + " real ,"
            + MOOD_COLUMN_LIKED_BY + " text " + ");";

    private static final String CREATE_TABLE_CHECKIN = "create table " + TABLE_CHECKIN + "("
            + CHECKIN_COLUMN_ID + " integer primary key autoincrement, "
            + CHECKIN_COLUMN_ID_ONSERVER + " text, "
            + CHECKIN_COLUMN_JID + " text, "
            + CHECKIN_COLUMN_MEM_TYPE + " text, "
            + CHECKIN_COLUMN_CAPTION + " text ,"
            + CHECKIN_COLUMN_LATITUDE + " REAL,"
            + CHECKIN_COLUMN_LONGITUDE + " REAL,"
            + CHECKIN_COLUMN_PLACE_NAME + " text,"
            + CHECKIN_COLUMN_PIC_URL + " text ,"
            + CHECKIN_COLUMN_WITH + " text ,"
            + CHECKIN_COLUMN_CREATED_BY + " text ,"
            + CHECKIN_COLUMN_CREATED_AT + " text ,"
            + CHECKIN_COLUMN_UPDATED_AT + " text ,"
            + CHECKIN_COLUMN_LIKED_BY + " text " + ");";

    private static final String CREATE_TABLE_NOTES = "create table if not exists " + TABLE_NOTES + "("
            + NOTES_COLUMN_ID + " integer primary key autoincrement, "
            + NOTES_COLUMN_ID_ONSERVER + " text, "
            + NOTES_COLUMN_JID + " text ,"
            + NOTES_COLUMN_MEM_TYPE + " text ,"
            + NOTES_COLUMN_CAPTION + " text ,"
            + NOTES_COLUMN_CONTENT + " text ,"
            + NOTES_COLUMN_CREATED_BY + " text ,"
            + NOTES_COLUMN_CREATED_AT + " integer ,"
            + NOTES_COLUMN_UPDATED_AT + " integer ,"
            + NOTES_COLUMN_LATITUDE + " real ,"
            + NOTES_CLOUMN_LONGITUDE + " real ,"
            + NOTES_COLUMN_LIKED_BY + " text " + ");";

    private static final String CREATE_TABLE_PLACE = "create table if not exists " + TABLE_PLACE + "("
            + PLACE_COLUMN_ID + " integer primary key autoincrement, "
            + PLACE_COLUMN_ID_ONSERVER + " text, "
            + PLACE_COLUMN_ID_ON_GOOGLE + " text ,"
            + PLACE_COLUMN_COUNTRY + " text ,"
            + PLACE_COLUMN_STATE + " text ,"
            + PLACE_COLUMN_CITY + " text ,"
            + PLACE_COLUMN_CREATED_BY + " text ,"
            + PLACE_COLUMN_CREATED_AT + " integer " + ");";

    private static final String TAG = "<<MySQLiteHelper>>";
    private static final String DATABASE_NAME = "memories.db";
    private static final int DATABASE_VERSION = 2;
    static MySQLiteHelper mInstance = null;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static MySQLiteHelper getInstance(Context context) {
        if (mInstance == null) {
            return new MySQLiteHelper(context);
        }
        return mInstance;
    }

    public static void deleteAll(Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        db.delete(TABLE_AUDIO, null, null);
        db.delete(TABLE_CHECKIN, null, null);
        db.delete(TABLE_CONTACT, null, null);
        db.delete(TABLE_JOURNEY, null, null);
        db.delete(TABLE_MOOD, null, null);
        db.delete(TABLE_NOTES, null, null);
        db.delete(TABLE_PICTURE, null, null);
        db.delete(TABLE_TIMELINE, null, null);
        db.delete(TABLE_VIDEO, null, null);
        db.delete(TABLE_PLACE, null, null);
        db.delete(TABLE_LIKE, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_CONTACT);
        Log.d(TAG, "COntact table created!");
        database.execSQL(CREATE_TABLE_JOURNEY);
        Log.d(TAG, "JOURNEY table created!");
        database.execSQL(CREATE_TABLE_TIMELINE);
        Log.d(TAG, "TIMELINE table created!");
        database.execSQL(CREATE_TABLE_PICTURE);
        Log.d(TAG, "PICTURE table created!");
        database.execSQL(CREATE_TABLE_AUDIO);
        Log.d(TAG, "AUDIO table created!");
        database.execSQL(CREATE_TABLE_VIDEO);
        Log.d(TAG, "VIDEO table created!");
        database.execSQL(CREATE_TABLE_CHECKIN);
        Log.d(TAG, "CHECKIN table created!");
        database.execSQL(CREATE_TABLE_NOTES);
        Log.d(TAG, "NOTES table created!");
        database.execSQL(CREATE_TABLE_MOOD);
        Log.d(TAG, "MOOD table created!");
        database.execSQL(CREATE_TABLE_PLACE);
        Log.d(TAG, "PLACE table created!");
        database.execSQL(CREATE_TABLE_LIKE);
        Log.d(TAG, "LIKE table created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        /*db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNEY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMELINE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE);
        onCreate(db);*/
        if(!tableExists("", null));
    }

    private boolean columnExists(String columnName, String tableName, SQLiteDatabase db){
        Cursor cursor = db.query(tableName, null, null, null, null, null, null, "0");
        boolean exists = cursor.getColumnIndex(columnName) == -1 ? false : true;
        cursor.close();
        db.close();
        return exists;
    }

    private boolean tableExists(String tableName, SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        boolean exists = false;
        if(cursor != null){
            if(cursor.getCount() > 0){
                exists = true;
            }
        }
        cursor.close();
        db.close();
        return exists;
    }

}
