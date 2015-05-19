package com.example.memories.utility;

import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HelpMe {
    public static final String PREFS_NAME = "TravelJarPrefs";
    public static final String IS_SIGNED_UP = "isSignedUp";
    // GCM notification type codes
    // don't change them -- are synced with server
    public static final int TYPE_CREATE_MEMORY = 1;
    public static final int TYPE_UPDATE_MEMORY = 2;
    public static final int TYPE_DELETE_MEMORY = 3;
    public static final int TYPE_CREATE_JOURNEY = 4;
    // Type = picture/audio/video/note
    // don't change them -- are synced with server
    public static final String PICTURE_TYPE = "1";
    public static final String AUDIO_TYPE = "2";
    public static final String VIDEO_TYPE = "3";
    public static final String NOTE_TYPE = "4";
    public static final String CHECKIN_TYPE = "5";
    public static final String MOOD_TYPE = "6";

    public static final int TYPE_PICTURE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_NOTE = 4;
    public static final int TYPE_CHECKIN = 5;
    public static final int TYPE_MOOD = 6;
    public static final int TYPE_MAX_COUNT = 6;

    public static final String CONVEYANCE_FLIGHT = "Flight";
    public static final String CONVEYANCE_CAR = "Car";
    public static final String CONVEYANCE_TRAIN = "Train";
    public static final String CONVEYANCE_SHIP = "Ship";
    public static final String TIME_OF_DAY_MORNING = "Morning";
    public static final String TIME_OF_DAY_AFTERNOON = "Afternoon";
    public static final String TIME_OF_DAY_EVENING = "Evening";
    public static final String TIME_OF_DAY_NIGHT = "Night";
    // To fetch dates from getDate()
    public static final int DATE_FULL = 1;
    private static final String TAG = "<HelpMe>";
    public static Context mContext;

    public HelpMe() {
        Calendar rightNow = Calendar.getInstance();
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    // CHeck for Internet connection
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getDate(long timestamp, int type) {
        SimpleDateFormat onlyDate = new SimpleDateFormat("dd");
        SimpleDateFormat fullDate = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat fullTime = new SimpleDateFormat("hh:mm aaa, EEE");
        Date resultdate = new Date(timestamp);

        switch (type) {
            case DATE_FULL:
                return fullDate.format(resultdate).toString();
            default:
                break;
        }
        return null;
    }

    public static long getDateInLongFromString(String dateString) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date.getTime();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    // Used to fetch an image from a given URI
    // SAmpled and decoded and bitmap is created
    public static Bitmap getImageBitmapFromURI(Context mContext, String imageUri) {
        Uri imageURI = Uri.parse(imageUri);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageURI);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (bitmap);
    }

    public static Bitmap decodeSampledBitmapFromURI(Context mContext, String resPath, int reqWidth,
                                                    int reqHeight) throws FileNotFoundException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Log.d(TAG, "decoding from path to fileinputstream");
        String realPath = getRealPathFromURI(Uri.parse(resPath), mContext);
        BitmapFactory.decodeStream(new FileInputStream(realPath), null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(new FileInputStream(realPath), null, options);
    }

    public static Bitmap decodeSampledBitmapFromPath(Context mContext, String resPath,
                                                     int reqWidth, int reqHeight) throws FileNotFoundException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Log.d(TAG, "decoding from path to fileinputstream");
        BitmapFactory.decodeStream(new FileInputStream(resPath), null, options);

        // Calculate inSampleSize
        // options.inSampleSize = 8;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        Log.d(TAG, "In sample size " + options.inSampleSize);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(new FileInputStream(resPath), null, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.d(TAG, "inSampleSize = " + inSampleSize);
        return inSampleSize;
    }

    public static String getRealPathFromURI(Uri contentUri, Context mContext) {
        String[] proj = {MediaColumns.DATA};
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        Log.d(TAG, "getRealPathFromURI = " + cursor.getString(column_index));
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    // pass the actual path of the audio file to be played
    public static void playAudio(String dataURL, Context context) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.android.music",
                "com.android.music.MediaPlaybackActivity");
        intent.setComponent(comp);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(dataURL);
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        context.startActivity(intent);
    }

    // Get video thumbnail
    // pass the path of video saved
    public static Bitmap getVideoThumbnail(String path) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            bitmap = ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MINI_KIND);
            if (bitmap != null) {
                return bitmap;
            }
        }
        return bitmap;
    }

}
