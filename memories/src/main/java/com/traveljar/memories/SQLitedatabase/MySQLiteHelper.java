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
    public static final String CONTACT_COLUMN_PROFILE_NAME = "name";
    public static final String CONTACT_COLUMN_PHONEBOOK_NAME = "phoneBookName";
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
    public static final String JOURNEY_COLUMN_IS_USER_ACTIVE = "isUserActive"; //Flag to check whether current user is active or not

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
    public static final String PICTURE_COLUMN_LOCALTHUMBNAILPATH = "thumbnailPath";
    public static final String PICTURE_COLUMN_LATITUDE = "latitude";
    public static final String PICTURE_COLUMN_LONGITUDE = "longitude";
    public static final String PICTURE_COLUMN_IS_DELETED = "isDeleted";
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
    public static final String VOICE_COLUMN_LONGITUDE = "longitude";
    public static final String VOICE_COLUMN_DURATION = "duration";
    public static final String VOICE_COLUMN_IS_DELETED = "isDeleted";

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
    public static final String VIDEO_COLUMN_LOCALTHUMBNAILPATH = "thumbnailPath";
    public static final String VIDEO_COLUMN_LATITUDE = "latitude";
    public static final String VIDEO_COLUMN_LONGITUDE = "longitude";
    public static final String VIDEO_COLUMN_IS_DELETED = "isDeleted";

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
    public static final String MOOD_COLUMN_LONGITUDE = "longitude";
    public static final String MOOD_COLUMN_IS_DELETED = "isDeleted";

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
    public static final String CHECKIN_COLUMN_PIC_LOCAL_URL = "checkInPicLocalURL";
    public static final String CHECKIN_COLUMN_PIC_SERVER_URL = "checkInPicServerURL";
    public static final String CHECKIN_COLUMN_PIC_THUMB_URL = "checkInPicThumbURL";
    public static final String CHECKIN_COLUMN_WITH = "checkInWith";
    public static final String CHECKIN_COLUMN_CREATED_BY = "createdBy";
    public static final String CHECKIN_COLUMN_CREATED_AT = "createdAt";
    public static final String CHECKIN_COLUMN_UPDATED_AT = "updatedAt";
    public static final String CHECKIN_COLUMN_LIKED_BY = "likedBy";
    public static final String CHECKIN_COLUMN_IS_DELETED = "isDeleted";
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
    public static final String NOTES_COLUMN_LONGITUDE = "longitude";
    public static final String NOTES_COLUMN_IS_DELETED = "isDeleted";

    //table place
    public static final String TABLE_PLACE = "PLACE";
    public static final String PLACE_COLUMN_ID = "_id";
    public static final String PLACE_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String PLACE_COLUMN_COUNTRY = "country";
    public static final String PLACE_COLUMN_STATE = "state";
    public static final String PLACE_COLUMN_CITY = "city";
    public static final String PLACE_COLUMN_CREATED_AT = "createdAt";
    public static final String PLACE_COLUMN_LATITUDE = "latitude";
    public static final String PLACE_COLUMN_LONGITUDE = "longitude";

    //Table Likes
    public static final String TABLE_LIKE = "LIKE";
    public static final String LIKE_COLUMN_ID = "_id";
    public static final String LIKE_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String LIKE_COLUMN_MEMORY_SERVERID = "memServerId";
    public static final String LIKE_COLUMN_CREATED_AT = "createdAt";
    public static final String LIKE_COLUMN_UPDATED_AT = "updatedAt";
    public static final String LIKE_COLUMN_JOURNEY_ID = "journeyId";
    public static final String LIKE_COLUMN_MEMORABLE_ID = "memorableId";
    public static final String LIKE_COLUMN_USER_ID = "userId";
    public static final String LIKE_COLUMN_MEM_TYPE = "memType";
    public static final String LIKE_COLUMN_IS_VALID = "isValid";

    //	Table Timecapsule fields
    public static final String TABLE_TIMECAPSULE = "TIMECAPSULE";
    public static final String TIMECAPSULE_COLUMN_ID = "_id";
    public static final String TIMECAPSULE_COLUMN_ID_ONSERVER = "idOnServer";
    public static final String TIMECAPSULE_COLUMN_JID = "journeyId";
    public static final String TIMECAPSULE_COLUMN_CAPTION = "caption";
    public static final String TIMECAPSULE_COLUMN_EXT = "extension";
    public static final String TIMECAPSULE_COLUMN_SIZE = "size";
    public static final String TIMECAPSULE_COLUMN_VIDEOSERVERURL = "dataServerURL";
    public static final String TIMECAPSULE_COLUMN_VIDEOLOCALURL = "dataLocalURL";
    public static final String TIMECAPSULE_COLUMN_CREATED_BY = "createdBy";
    public static final String TIMECAPSULE_COLUMN_CREATED_AT = "createdAt";
    public static final String TIMECAPSULE_COLUMN_UPDATED_AT = "updatedAt";
    public static final String TIMECAPSULE_COLUMN_LOCALTHUMBNAILPATH = "thumbnailPath";

    //Table requests
    public static final String TABLE_REQUEST_QUEUE = "requestQueue";
    public static final String RQ_COLUMN_ID = "_id";
    public static final String RQ_COLUMN_LOCAL_ID = "localId";
    public static final String RQ_COLUMN_JOURNEY_ID = "journeyId";
    public static final String RQ_COLUMN_OPERATION_TYPE = "operationType";
    public static final String RQ_COLUMN_CATEGORY_TYPE = "categorytype";
    public static final String RQ_COLUMN_REQUEST_STATUS = "requestStatus";
    public static final String RQ_COLUMN_NO_OF_ATTEMPTS = "noOfAttempts";


    //Table lap
    public static final String TABLE_LAP = "LAP";
    public static final String LAP_COLUMN_ID = "id";
    public static final String LAP_COLUMN_ID_ON_SERVER = "idOnServer";
    public static final String LAP_COLUMN_JOURNEY_ID = "journeyId";
    public static final String LAP_COLUMN_SOURCE_CITY = "sourceCity";
    public static final String LAP_COLUMN_SOURCE_STATE = "sourceState";
    public static final String LAP_COLUMN_SOURCE_COUNTRY = "sourceCountry";
    public static final String LAP_COLUMN_DESTINATION_CITY = "destinationCity";
    public static final String LAP_COLUMN_DESTINATION_STATE = "destinationState";
    public static final String LAP_COLUMN_DESTINATION_COUNTRY = "destinationCountry";
    public static final String LAP_COLUMN_CONVEYANCE_MODE = "conveyanceMode";
    public static final String LAP_COLUMN_START_DATE = "startDate";

    //original LAP table
    public static final String TABLE_LAPS = "LAPS";
    public static final String LAPS_COLUMN_ID = "id";
    public static final String LAPS_COLUMN_ID_ON_SERVER = "idOnServer";
    public static final String LAPS_COLUMN_JOURNEY_ID = "journeyId";
    public static final String LAPS_COLUMN_SOURCE_PLACE_ID = "sourcePlaceId";
    public static final String LAPS_COLUMN_DESTINATION_PLACE_ID = "destinationPlaceId";
    public static final String LAPS_COLUMN_CONVEYANCE_MODE = "conveyanceMode";
    public static final String LAPS_COLUMN_START_DATE = "startDate";

//    Table JOURNEY_CONTACT_MAPPING
    public static final String TABLE_CONTACT_JOURNEY_MAP = "CONTACT_JOURNEY_MAPPING";
    public static final String MAPPING_COLUMN_JOURNEY_ID = "journeyId";
    public static final String MAPPING_COLUMN_CONTACT_ID = "contactId";
    public static final String MAPPING_COLUMN_IS_USER_ACTIVE = "isUserActive";

    private static final String CREATE_TABLE_CONTACT_JOURNEY_MAP = "create table " + TABLE_CONTACT_JOURNEY_MAP + "("
            + MAPPING_COLUMN_JOURNEY_ID + " text , "
            + MAPPING_COLUMN_CONTACT_ID + " text , "
            + MAPPING_COLUMN_IS_USER_ACTIVE + " integer , "
            + "PRIMARY KEY (" + MAPPING_COLUMN_JOURNEY_ID + ", " + MAPPING_COLUMN_CONTACT_ID + " )" + ");";

    private static final String CREATE_TABLE_LAP = "create table " + TABLE_LAP + "("
            + LAP_COLUMN_ID + " integer primary key autoincrement, "
            + LAP_COLUMN_ID_ON_SERVER + " text , "
            + LAP_COLUMN_JOURNEY_ID + " text ,"
            + LAP_COLUMN_SOURCE_CITY + " text,"
            + LAP_COLUMN_SOURCE_STATE + " text ,"
            + LAP_COLUMN_SOURCE_COUNTRY + " text ,"
            + LAP_COLUMN_DESTINATION_CITY + " text,"
            + LAP_COLUMN_DESTINATION_STATE + " text ,"
            + LAP_COLUMN_DESTINATION_COUNTRY + " text ,"
            + LAP_COLUMN_START_DATE + " integer ,"
            + LAP_COLUMN_CONVEYANCE_MODE + " integer " + ");";

    private static final String CREATE_TABLE_LAPS = "create table " + TABLE_LAPS + "("
            + LAPS_COLUMN_ID + " integer primary key autoincrement, "
            + LAPS_COLUMN_ID_ON_SERVER + " text , "
            + LAPS_COLUMN_JOURNEY_ID + " text ,"
            + LAPS_COLUMN_SOURCE_PLACE_ID + " text,"
            + LAPS_COLUMN_DESTINATION_PLACE_ID + " text ,"
            + LAPS_COLUMN_START_DATE + " integer ,"
            + LAPS_COLUMN_CONVEYANCE_MODE + " integer " + ");";

    private static final String CREATE_TABLE_LIKE = "create table " + TABLE_LIKE + "("
            + LIKE_COLUMN_ID + " integer primary key autoincrement, "
            + LIKE_COLUMN_ID_ONSERVER + " text , "
            + LIKE_COLUMN_JOURNEY_ID + " text ,"
            + LIKE_COLUMN_MEMORY_SERVERID + " text,"
            + LIKE_COLUMN_CREATED_AT + " integer ,"
            + LIKE_COLUMN_UPDATED_AT + " integer ,"
            + LIKE_COLUMN_MEMORABLE_ID + " text,"
            + LIKE_COLUMN_USER_ID + " text ,"
            + LIKE_COLUMN_IS_VALID + " integer ,"
            + LIKE_COLUMN_MEM_TYPE + " text " + ");";

    private static final String CREATE_TABLE_CONTACT = "create table " + TABLE_CONTACT + "("
            + CONTACT_COLUMN_ID_ONSERVER + " text primary key, "
            + CONTACT_COLUMN_PROFILE_NAME + " text ,"
            + CONTACT_COLUMN_PHONEBOOK_NAME + " text ,"
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
            + JOURNEY_COLUMN_IS_USER_ACTIVE + " integer ,"
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
            + PICTURE_COLUMN_LOCALTHUMBNAILPATH + " text ,"
            + PICTURE_COLUMN_LATITUDE + " text ,"
            + PICTURE_COLUMN_LONGITUDE + " real ,"
            + PICTURE_COLUMN_IS_DELETED + " integer DEFAULT 0,"
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
            + VOICE_COLUMN_LONGITUDE + " real ,"
            + VOICE_COLUMN_DURATION + " integer ,"
            + VOICE_COLUMN_IS_DELETED + " integer DEFAULT 0,"
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
            + VIDEO_COLUMN_LOCALTHUMBNAILPATH + " text ,"
            + VIDEO_COLUMN_LATITUDE + " real ,"
            + VIDEO_COLUMN_LONGITUDE + " real ,"
            + VIDEO_COLUMN_IS_DELETED + " integer DEFAULT 0,"
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
            + MOOD_COLUMN_LONGITUDE + " real ,"
            + MOOD_COLUMN_IS_DELETED + " integer DEFAULT 0,"
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
            + CHECKIN_COLUMN_PIC_LOCAL_URL + " text ,"
            + CHECKIN_COLUMN_PIC_THUMB_URL + " text ,"
            + CHECKIN_COLUMN_PIC_SERVER_URL + " text ,"
            + CHECKIN_COLUMN_WITH + " text ,"
            + CHECKIN_COLUMN_CREATED_BY + " text ,"
            + CHECKIN_COLUMN_CREATED_AT + " text ,"
            + CHECKIN_COLUMN_UPDATED_AT + " text ,"
            + CHECKIN_COLUMN_IS_DELETED + " integer DEFAULT 0,"
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
            + NOTES_COLUMN_LONGITUDE + " real ,"
            + NOTES_COLUMN_IS_DELETED + " integer DEFAULT 0,"
            + NOTES_COLUMN_LIKED_BY + " text " + ");";

    private static final String CREATE_TABLE_PLACE = "create table if not exists " + TABLE_PLACE + "("
            + PLACE_COLUMN_ID + " integer primary key autoincrement, "
            + PLACE_COLUMN_ID_ONSERVER + " text, "
            + PLACE_COLUMN_COUNTRY + " text ,"
            + PLACE_COLUMN_STATE + " text ,"
            + PLACE_COLUMN_CITY + " text ,"
            + PLACE_COLUMN_LATITUDE + " text ,"
            + PLACE_COLUMN_LONGITUDE + " text ,"
            + PLACE_COLUMN_CREATED_AT + " integer " + ");";

    private static final String CREATE_TABLE_TIMECAPSULE = "create table if not exists " + TABLE_TIMECAPSULE + "("
            + TIMECAPSULE_COLUMN_ID + " integer primary key autoincrement, "
            + TIMECAPSULE_COLUMN_ID_ONSERVER + " text, "
            + TIMECAPSULE_COLUMN_JID + " text,"
            + TIMECAPSULE_COLUMN_CAPTION + " text,"
            + TIMECAPSULE_COLUMN_EXT + " text,"
            + TIMECAPSULE_COLUMN_SIZE + " integer,"
            + TIMECAPSULE_COLUMN_VIDEOSERVERURL + " text ,"
            + TIMECAPSULE_COLUMN_VIDEOLOCALURL + " text ,"
            + TIMECAPSULE_COLUMN_CREATED_BY + " text ,"
            + TIMECAPSULE_COLUMN_CREATED_AT + " integer ,"
            + TIMECAPSULE_COLUMN_UPDATED_AT + " integer ,"
            + TIMECAPSULE_COLUMN_LOCALTHUMBNAILPATH + " text " + ");";

    private static final String CREATE_TABLE_REQUEST_QUEUE = "CREATE TABLE IF NOT EXISTS " + TABLE_REQUEST_QUEUE + "("
            + RQ_COLUMN_ID + " integer primary key autoincrement, "
            + RQ_COLUMN_LOCAL_ID + " text , "
            + RQ_COLUMN_JOURNEY_ID + " text ,"
            + RQ_COLUMN_OPERATION_TYPE + " integer,"
            + RQ_COLUMN_CATEGORY_TYPE + " integer ,"
            + RQ_COLUMN_NO_OF_ATTEMPTS + " integer default 0,"
            + RQ_COLUMN_REQUEST_STATUS + " integer " + ");";

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
        db.delete(TABLE_TIMECAPSULE, null, null);
        db.delete(TABLE_REQUEST_QUEUE, null, null);
        db.delete(TABLE_LAP, null, null);
        db.delete(TABLE_LAPS, null, null);
        db.delete(TABLE_CONTACT_JOURNEY_MAP, null, null);
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
        database.execSQL(CREATE_TABLE_TIMECAPSULE);
        Log.d(TAG, "TIMECAPSULE table created!");
        database.execSQL(CREATE_TABLE_REQUEST_QUEUE);
        Log.d(TAG, "REQUEST_QUEUE table created!");
        database.execSQL(CREATE_TABLE_LAP);
        Log.d(TAG, "LAP table created!");
        database.execSQL(CREATE_TABLE_LAPS);
        Log.d(TAG, "LAPS table created!");
        database.execSQL(CREATE_TABLE_CONTACT_JOURNEY_MAP);
        Log.d(TAG, "TABLE CONTACT JOURNEY MAP created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        if (!tableExists(TABLE_REQUEST_QUEUE, this.getReadableDatabase())) {
            this.getWritableDatabase().execSQL(CREATE_TABLE_REQUEST_QUEUE);
        }
    }

    private boolean columnExists(String columnName, String tableName, SQLiteDatabase db) {
        Cursor cursor = db.query(tableName, null, null, null, null, null, null, "0");
        boolean exists = cursor.getColumnIndex(columnName) == -1;
        cursor.close();
        db.close();
        return exists;
    }

    private boolean tableExists(String tableName, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        boolean exists = false;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                exists = true;
            }
        }
        cursor.close();
        db.close();
        return exists;
    }

}
