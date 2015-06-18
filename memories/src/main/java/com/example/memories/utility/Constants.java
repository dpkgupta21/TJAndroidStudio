package com.example.memories.utility;

import android.os.Environment;

public class Constants {

    // SERVER API URLS ==========================================

    // BASE DOMAIN API
    public final static String URL_TJ_DOMAIN = "https://www.traveljar.in/";
//    public final static String URL_TJ_DOMAIN = "http://192.168.1.10:3000/";

    // 1. USER APIS
    public static final String URL_SIGN_IN = URL_TJ_DOMAIN + "api/v1/users/login";
    public static final String URL_SIGN_UP = URL_TJ_DOMAIN + "api/v1/users";
    public static final String URL_UPDATE_USER_DETAILS = URL_TJ_DOMAIN + "api/v1/users/";
    public static final String URL_CHECK_TJ_CONTACTS = URL_TJ_DOMAIN + "api/v1/users/contacts";
    public static final String URL_USER_SHOW_DETAILS = URL_TJ_DOMAIN + "api/v1/users";

    // 2. JOURNEY APIS
    public static final String URL_CREATE_JOURNEY = URL_TJ_DOMAIN + "api/v1/journeys";

    // 3. MEMORIES APIS
    public static final String URL_JOURNIES_FETCH_ALL = URL_TJ_DOMAIN + "api/v1/journeys/";
    public static final String URL_MEMORY_UPLOAD = URL_TJ_DOMAIN + "api/v1/journeys/";
    public static final String URL_MEMORY_UPDATE = URL_TJ_DOMAIN + "api/v1/journeys/";

    // =================================================================================

    // GOOGLE APP INFO
    public static final String GOOGLE_PROJECT_NUMBER = "1027896810712";
    public static final String GOOGLE_ANDROID_API_KEY = "AIzaSyB6fK93rmDmyrBEjzxYYRwzDYHkDZ-4R8Y";

    // FOURSQUARE APP INFO
    public static final String FOURSQUARE_CLIENT_ID = "C11EN1D5NBT5VS44GXFQHMC5PUZ5OKRL3QAMY0AUWR01GFMK";
    public static final String FOURSQUARE_CLIENT_SECRET = "B0W3L1BJ5MEAYZJN3HSUXDCAVRNNDXT1VVKTO0SMLLRR3L3N";
    public static final String v = "20141201";
    public static final String URL_FS_VENUE_EXPLORE = "https://api.foursquare.com/v2/venues/search";

    // WEATHER API
    // OPEN WEATHER MAP
    public static final String URL_WEATHER_OWM_GET_CURRENT_WEATHER = "http://api.openweathermap.org/data/2.5/weather";

    // FOLDER STRUCTURES & FILE NAMES
    public static final String TRAVELJAR_FOLDER_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TJar";
    public static final String TRAVELJAR_FOLDER_PROFILE = TRAVELJAR_FOLDER_ROOT + "/Profile/";
    public static final String TRAVELJAR_FOLDER_BUDDY_PROFILES = TRAVELJAR_FOLDER_ROOT + "/buddy profiles/";
    public static final String GUMNAAM_IMAGE_URL = TRAVELJAR_FOLDER_BUDDY_PROFILES + "gumnaam_profile_image.jpg";
    public static final String TRAVELJAR_FOLDER_VIDEO = TRAVELJAR_FOLDER_ROOT + "/videos/";
    public static final String TRAVELJAR_FOLDER_AUDIO = TRAVELJAR_FOLDER_ROOT + "/audio/";
    public static final String TRAVELJAR_FOLDER_PICTURE = TRAVELJAR_FOLDER_ROOT + "/Pictures/";

    public static final String JOURNEY_STATUS_ACTIVE = "ACTIVE";
    public static final String JOURNEY_STATUS_PENDING = "PENDING";
    public static final String JOURNEY_STATUS_FINISHED = "FINISHED";

    public static final String URL_IMAGE = "http://api.androidhive.info/volley/volley-image.jpg";


}
