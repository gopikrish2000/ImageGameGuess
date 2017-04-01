package com.myntra.gopi.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myntra.gopi.R;
import com.myntra.gopi.application.MemoryGameApplication;
import com.myntra.gopi.domains.GameItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import rx.subscriptions.CompositeSubscription;

import static android.text.format.DateFormat.format;

/**
 * Created by gopikrishna on 26/11/16.
 */

public class CommonUtils {

    private static ColorDrawable[] vibrantLightColorList = {
            new ColorDrawable(Color.parseColor("#9ACCCD")), new ColorDrawable(Color.parseColor("#8FD8A0")),
            new ColorDrawable(Color.parseColor("#CBD890")), new ColorDrawable(Color.parseColor("#DACC8F")),
            new ColorDrawable(Color.parseColor("#D9A790")), new ColorDrawable(Color.parseColor("#D18FD9")),
            new ColorDrawable(Color.parseColor("#FF6772")), new ColorDrawable(Color.parseColor("#DDFB5C"))
    };

    public static boolean isNullOrEmpty(@Nullable CharSequence str) {
        return (str == null || str.length() == 0 || str.toString().trim().length() == 0);
    }

    public static boolean isNullOrEmpty(@Nullable List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNullOrEmpty(String[] split) {
        return split == null || split.length == 0;
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNonNull(Object object) {
        return object != null;
    }

    public static String getFormattedDate(Date date) {
        if (date == null) {
            return "";
        }
        return format("MMM dd", date).toString();
    }

    public static String getIntentValue(Intent intent, String key) {
        if (intent == null || intent.getExtras() == null) {
            return "";
        }
        Object object = intent.getExtras().get(key);
        if (object == null) {
            return "";
        }
        String result = String.valueOf(object);
        if (isNullOrEmpty(result)) {
            return "";
        }
        return result;
    }

    public static int getIntegerIntentValue(Intent intent, String key) {
        String strValue = getIntentValue(intent, key);
        try {
            return Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void doUnsubscribe(CompositeSubscription lifeCycle) {
        if (lifeCycle == null || lifeCycle.isUnsubscribed()) {
            return;
        }
        lifeCycle.unsubscribe();
    }

    public static String convertJsonPToJson(String jsonp) {
        if (isNullOrEmpty(jsonp)) {
            return "";
        }
        if ((!jsonp.contains("(") || !jsonp.contains(")"))) {
            return jsonp;
        }
        return jsonp.substring(jsonp.indexOf("(") + 1, jsonp.lastIndexOf(")"));
    }

    public static String convertResponseToString(ResponseBody response) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.byteStream()));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }

            // Prints the correct String representation of body.
            System.out.println(out);
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getRandomNumberInRange(int min, int max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            throw new IllegalArgumentException("max should be greater than min.");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void loadImageWithGlide(Context context, GameItem gameItem, ImageView imageView) {
        if (!gameItem.isDrawableNull()) {
            Glide.with(context).load(gameItem.getdrawableResourceId()).into(imageView);
            return;
        }
        if (isNullOrEmpty(gameItem.getBigImageUrl())) {
            Glide.with(context).load(R.drawable.default_img);
            return;
        }
        Glide.with(context).load(gameItem.getBigImageUrl()).error(R.drawable.default_img).diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(getRandomDrawbleColor()).crossFade().priority(Priority.HIGH).into(imageView);
    }

    public static ColorDrawable getRandomDrawbleColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) MemoryGameApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showToast(String message) {
        Toast.makeText(MemoryGameApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void dismissDialog(ProgressDialog dialog) {
        if (dialog == null) {
            return;
        }
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getDefaultImageResourceId(int item) {
        try {
            return MemoryGameApplication.getInstance().getResources().getIdentifier("default_" + item, "drawable", MemoryGameApplication.getInstance().getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.drawable.default_img;
    }

    public static int stringToInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getCurrentDateTimeForDb() {
        Calendar.getInstance().setTimeZone(TimeZone.getTimeZone("IST"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();
        return dateFormat.format(date);
    }

    public static Date getDateTimeFromString(String dateString) {
        Calendar.getInstance().setTimeZone(TimeZone.getTimeZone("IST"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void stopMediaPlayer(MediaPlayer mediaPlayer) {
        try {
            mediaPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
