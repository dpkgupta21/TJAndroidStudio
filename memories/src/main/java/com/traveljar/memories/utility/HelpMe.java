package com.traveljar.memories.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.models.Journey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelpMe {
    // GCM notification type codes
    // don't change them -- are synced with server
    public static final int TYPE_CREATE_MEMORY = 1;
    public static final int TYPE_UPDATE_MEMORY = 2;
    public static final int TYPE_DELETE_MEMORY = 3;
    public static final int TYPE_CREATE_JOURNEY = 4;
    public static final int TYPE_PROFILE_UPDATE = 5;
    public static final int TYPE_LIKE_MEMORY = 6;
    public static final int TYPE_UNLIKE_MEMORY = 7;
    public static final int TYPE_ADD_BUDDY = 8;
    public static final int TYPE_REMOVE_BUDDY = 9;

    // Type = picture/audio/video/note
    // don't change them -- are synced with server
    public static final String PICTURE_TYPE = "5";
    public static final String AUDIO_TYPE = "1";
    public static final String VIDEO_TYPE = "6";
    public static final String NOTE_TYPE = "4";
    public static final String CHECKIN_TYPE = "2";
    public static final String MOOD_TYPE = "3";

    public static final int SERVER_PICTURE_TYPE = 5;
    public static final int SERVER_AUDIO_TYPE = 1;
    public static final int SERVER_VIDEO_TYPE = 6;
    public static final int SERVER_NOTE_TYPE = 4;
    public static final int SERVER_CHECKIN_TYPE = 2;
    public static final int SERVER_MOOD_TYPE = 3;

    public static final int TYPE_PICTURE = 5;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 6;
    public static final int TYPE_NOTE = 4;
    public static final int TYPE_CHECKIN = 2;
    public static final int TYPE_MOOD = 3;
    public static final int TYPE_MAX_COUNT = 6;

    public static final int CONVEYANCE_FLIGHT = 1; // "Flight";
    public static final int CONVEYANCE_CAR = 2; //"Car";
    public static final int CONVEYANCE_TRAIN = 3; // "Train";
    public static final int CONVEYANCE_SHIP = 4; //"Ship";
    public static final int CONVEYANCE_BUS = 6; //"Bus";
    public static final int CONVEYANCE_WALK = 5; //"Walk";
    public static final int CONVEYANCE_BIKE = 7; //"Bike";
    public static final int CONVEYANCE_CARPET = 8; //"Carpet";

    // To fetch dates from getDate()
    public static final int DATE_FULL = 1;
    public static final int DATE_ONLY = 2;
    public static final int TIME_ONLY = 3;
    public static final int ONLY_DAY = 4;
    private static final String TAG = "<HelpMe>";
    public static Context mContext;

    // get name of transport from codes
    public static String getConveyanceMode(int c) {
        switch (c) {
            case 1:
                return "Flight";
            case 2:
                return "Car";
            case 3:
                return "Train";
            case 4:
                return "Ship";
            case 5:
                return "Walking";
            case 6:
                return "Bus";
            case 7:
                return "Bike/Cycling";
            case 8:
                return "Magical Carpet";
        }
        return null;
    }

    // get mode of transport code from name
    public static int getConveyanceModeCode(String mode) {
        switch (mode){
            case "Flight" :
                return 1;
            case "Car" :
                return 2;
            case "Train" :
                return 3;
            case "Ship" :
                return 4;
            case "Walking" :
                return 5;
            case "Bus" :
                return 6;
            case "Bike/Cycling" :
                return 7;
            default:
                return 8;
        }
    }

    public HelpMe() {
        Calendar rightNow = Calendar.getInstance();
    }

    public static long getCurrentTime() {
        return (System.currentTimeMillis()/1000);
    }

    // CHeck for Internet connection
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Check for valid Email address
    public static boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        return check;
    }

    // Check for valid mobile number of 10 digits
    public static boolean isValidMobile(String phone) {
        if (phone.length() != 10) {
            return false;
        } else {
            return true;
        }
    }

    public static String getDate(long timestamp, int type) {
        timestamp = timestamp * 1000;
        SimpleDateFormat onlyDate = new SimpleDateFormat("dd");
        SimpleDateFormat fullDate = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat fullTime = new SimpleDateFormat("hh:mm aaa, EEE");
        SimpleDateFormat onlyDay = new SimpleDateFormat("EEE");
        Date resultDate = new Date(timestamp);

        switch (type) {
            case DATE_FULL:
                return fullDate.format(resultDate).toString();
            case DATE_ONLY:
                return onlyDate.format(resultDate).toString();
            case TIME_ONLY:
                return fullTime.format(resultDate).toString();
            case ONLY_DAY:
                return onlyDay.format(resultDate).toString();
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

        //Log.d(TAG, "local file path for image is " + resPath);

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Log.d(TAG, "decoding from path to fileinputstream");
        BitmapFactory.decodeStream(new FileInputStream(resPath), null, options);

        // Calculate inSampleSize
        // options.inSampleSize = 8;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        //Log.d(TAG, "In sample size " + options.inSampleSize);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(new FileInputStream(resPath), null, options);
    }

    /*returns the bitmap from the path without downscaling the image */
    public static Bitmap getBitmapFromPath(String filePath){
        File imageFile = new File(filePath);
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), new BitmapFactory.Options());
        return bitmap;
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

    // CHecks if a default profile image is present or not
    // if not copies it from the drawable folder
    public static void createImageIfNotExist(Context context) {
        if (!(new File(Constants.GUMNAAM_IMAGE_URL)).exists()) {
            //check whether the dir exists
            File dir = new File(Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES);
            if (!dir.exists()) {
                Log.d(TAG, "made new directory with name = " + Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES);
                dir.mkdirs();
            }
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.gumnaam_profile_image);
            File file = new File(Constants.GUMNAAM_IMAGE_URL);
            FileOutputStream outStream;
            try {
                Log.d(TAG, bm + "====" + file);
                outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isAdmin(Context context) {
        Journey journey = JourneyDataSource.getJourneyById(context, TJPreferences.getActiveJourneyId(context));
        Log.d(TAG, "" + TJPreferences.getUserId(context) + journey.getCreatedBy());
        return TJPreferences.getUserId(context).equals(journey.getCreatedBy());
    }

    //Parsing date from 2015-05-08T12:38:49.777Z (UTC format) to yyyy-MM-dd kk:mm:ss and return the timestamp
    public long getTimeStampFromDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        // date coming from server is in the format 2015-05-08T12:38:49.777Z and we want to convert it to above format
        // so first remove T and everything after '.' from the string we are getting
        dateStr = dateStr.split("\\.")[0];
        dateStr.replace("T", " ");
        Date date = null;
        try {
            date = format.parse(dateStr);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static int getWindowHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static int getWindowWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static String getContactNameFromNumber(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

}
