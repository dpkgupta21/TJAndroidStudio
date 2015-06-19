package com.traveljar.memories.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class TJPreferences {

    public static final String PREF_NAME = "TRAVELJAR_PREFERENCES";
    public static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_EMAIL = "EMAIL";
    public static final String KEY_PHONE = "PHONE";
    public static final String KEY_PROFILE_IMG = "PROFILE_IMG";
    public static final String API_KEY = "API_KEY";
    public static final String KEY_CURRENT_JID = "ACTIVE_JOURNEY_ID";
    public static final String KEY_CURRENT_BUDDY_IDS = "ACTIVE_JOURNEY_BUDDY_IDS";
    public static final String KEY_STATUS = "STATUS";
    public static final String USER_PASSWORD = "PASSWORD";
    public static final String KEY_GCM_REG_ID = "GCM_REG_ID";
    public static final String KEY_APP_VERSION = "APP_VERSION";

    public static void setLoggedIn(Context context, Boolean loggedIn) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_LOGGED_IN, loggedIn);
        editor.commit();
    }

    public static Boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(
                IS_LOGGED_IN, false);
    }

    public static void setUserId(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

    public static String getUserId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_USER_ID,
                null);
    }

    public static void setProfileImgPath(Context context, String path) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PROFILE_IMG, path);
        editor.commit();
    }

    public static String getProfileImgPath(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_PROFILE_IMG,
                null);
    }

    public static void setUserName(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_NAME, userId);
        editor.commit();
    }

    public static String getUserName(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_NAME,
                null);
    }

    public static void setEmail(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_EMAIL, userId);
        editor.commit();
    }

    public static String getEmail(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_EMAIL,
                null);
    }

    public static void setPhone(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PHONE, userId);
        editor.commit();
    }

    public static String getPhone(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_PHONE,
                null);
    }

    public static void setApiKey(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(API_KEY, userId);
        editor.commit();
    }

    public static String getApiKey(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(API_KEY,
                null);
    }

    public static void setActiveJourneyId(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_CURRENT_JID, userId);
        editor.commit();
    }

    public static String getActiveJourneyId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(
                KEY_CURRENT_JID, null);
    }

    public static void setActiveBuddyIds(Context context, String buddyIds) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_CURRENT_BUDDY_IDS, buddyIds);
        editor.commit();
    }

    public static String getActiveBuddyIds(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(
                KEY_CURRENT_BUDDY_IDS, null);
    }

    public static void setUserStatus(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_STATUS, userId);
        editor.commit();
    }

    public static String getUserStatus(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_STATUS,
                null);
    }

    public static void setUserPassword(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_PASSWORD, userId);
        editor.commit();
    }

    public static String getUserPassword(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(
                USER_PASSWORD, null);
    }

    public static void setGcmRegId(Context context, String regId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_GCM_REG_ID, regId);
        editor.commit();
    }

    public static String getGcmRegId(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(
                KEY_GCM_REG_ID, null);
    }

    public static void setAppVersion(Context context, int appVersion) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_APP_VERSION, appVersion);
        editor.commit();
    }

    public static Integer getAppVersion(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(
                KEY_APP_VERSION, Integer.MIN_VALUE);
    }

    public static void clearAllPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // User SharedPreferences.Editor.remove() in place of putString()
        editor.putString(KEY_USER_ID, null);
        editor.putString(KEY_NAME, null);
        editor.putString(KEY_EMAIL, null);
        editor.putString(KEY_PHONE, null);
        editor.putString(KEY_CURRENT_JID, null);
        editor.putString(KEY_STATUS, null);
        editor.putString(IS_LOGGED_IN, "false");
        editor.commit();

    }
}
