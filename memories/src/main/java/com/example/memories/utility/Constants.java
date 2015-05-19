package com.example.memories.utility;

import android.os.Environment;

public class Constants {
    public static final String TAG_LAPS_FRAGMENT = "TAG_LAPS_FRAGMENT";
    public static final String TAG_ADD_LAPS_FRAGMENT = "TAG_ADD_LAPS_FRAGMENT";

    // GPS requiremets
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    // also the sender id for GCM registration process
    public static final String GOOGLE_PROJECT_NUMBER = "1027896810712";

    public static final String TRAVELJAR_FOLDER_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TravelJar";
    public static final String TRAVELJAR_FOLDER_PROFILE = TRAVELJAR_FOLDER_ROOT + "/Profile/";
    public static final String TRAVELJAR_FOLDER_BUDDY_PROFILES = TRAVELJAR_FOLDER_ROOT + "/buddy profiles/";
    public static final String GUMNAAM_IMAGE_URL = TRAVELJAR_FOLDER_BUDDY_PROFILES + "gumnaam.jpg";
    public static final String TRAVELJAR_FOLDER_VIDEO = TRAVELJAR_FOLDER_ROOT + "/videos/";
    public static final String TRAVELJAR_FOLDER_AUDIO = TRAVELJAR_FOLDER_ROOT + "/audio/";
    public static final String TRAVELJAR_FOLDER_PICTURE = TRAVELJAR_FOLDER_ROOT + "/Pictures/";

    public static final String TRAVELJAR_API_BASE_URL = "https://www.traveljar.in/api/v1";

    public static final String JOURNEY_STATUS_ACTIVE = "ACTIVE";
    public static final String JOURNEY_STATUS_PENDING = "PENDING";
    public static final String JOURNEY_STATUS_FINISHED = "FINISHED";

}
