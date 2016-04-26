package com.example.desy.myandroid.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by desy on 4/20/16.
 */
public class BaryonManager extends BaseAPIManager{

    private final static String TAG = "BaryonManager";

    private static final Object mLock = new Object();
    private static BaryonManager mInstance;

    public final String PTYPE_EVENT = "insnap-Event";
    public final String PTYPE_METADATA = "insnap-Metadata";
    public final String PTYPE_APPLIST = "insnap-AppList";
    public final String PTYPE_REALTIME = "insnap-Realtime";
    public final String PTYPE_EVENT_ENDPOINT = "/eventlog";
    public final String PTYPE_METADATA_ENDPOINT = "/metadata";
    public final String PTYPE_APPLIST_ENDPOINT = "/appinfo";
    public final String PTYPE_REALTIME_ENDPOINT = "/realtime";
    public final String DTYPE_EVENT = "event";
    public final String DTYPE_METADATA = "assets";
    public final String DTYPE_APPLIST = "apps";

    private ThreadPoolExecutor threadPoolExecutor = null;
    private ThreadPoolExecutor realtimeThreadPoolExecutor = null;

    private boolean hasNetworkPermission = false;
    private JSONObject commonPayload = null;
    private String sessionID = null;
    private int chunkCounter = 0;
    private int expectedNumberOfChunks = 0;


    SharedPreferences mSharedPreferences;

    public static BaryonManager getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new BaryonManager(context.getApplicationContext());
            }
            return mInstance;
        }
    }


    private BaryonManager(Context context) {
        mAppContext = context;
        mSharedPreferences = mAppContext.getSharedPreferences("insnap", Context.MODE_PRIVATE);
    }



    @Override
    public void addPermissionChecks() {
        String[] requiredPermissions = new String[]{"android.permission.ACCESS_NETWORK_STATE", "android.permission.INTERNET"};

        for (String requiredPermission : requiredPermissions) {
            if (mAppContext.checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Baryon", requiredPermission + " was PERMISSION_GRANTED");
                this.hasNetworkPermission = true;
            } else {
                Log.i("Baryon", requiredPermission + " was PERMISSION_DENIED");
                this.hasNetworkPermission = false;
                break;
            }
        }
    }




    public void initCommonPayload() {
        JSONObject common = new JSONObject();

        JSONObject device = new JSONObject();
        JSONObject environment = new JSONObject();
        JSONObject timezone = new JSONObject();
        JSONObject user = new JSONObject();

        try {
            device.put("model", Build.MODEL);
            device.put("systemVersion", Build.VERSION.RELEASE);
            device.put("device", Build.DEVICE);
            device.put("brand", Build.BRAND);
            device.put("product", Build.PRODUCT);
            device.put("type", Build.TYPE);
            device.put("user", Build.USER);
            device.put("manufacturer", Build.MANUFACTURER);
            device.put("time", Build.TIME);
            device.put("display", Build.DISPLAY);

            // TODO
            device.put("systemName", "Android");
            device.put("localizedModel", Build.MODEL);
            device.put("name", "insnap-test-user");
            device.put("userInterfaceIdiom", "Phone");


            device.put("platform", "Android");

            Locale locale = Locale.getDefault();
            device.put("countryCode", locale.getCountry());
            device.put("country", locale.getDisplayCountry());

            JSONArray locales = new JSONArray();
            locales.put(locale.getLanguage());
            device.put("locale", locales);

            JSONArray languages = new JSONArray();
            languages.put(locale.getDisplayLanguage());
            device.put("language", languages);

            try {
                TelephonyManager manager = (TelephonyManager) mAppContext.getSystemService(Context.TELEPHONY_SERVICE);
                String carrierName = manager.getNetworkOperatorName();
                environment.put("carrier", carrierName);
            }
            catch(Exception exception) {
                Log.e("Baryon", exception.getMessage());
            }

            common.put("environment", environment);

//            common.put("timestamp", Utils.Date.getCurrentUTCTimestamp());

            common.put("device", device);


            // timezone object
//            DateTimeZone dateTimeZone = DateTimeZone.getDefault();
//            timezone.put("name", dateTimeZone.getID());
//            timezone.put("abbreviation", dateTimeZone.getShortName(DateTime.now().getMillis()));
//            int offsetFromUtc = dateTimeZone.getOffset(DateTime.now()) / 1000;
//            timezone.put("secondsFromGMT", String.format("%d", offsetFromUtc));

            common.put("timezone", timezone);

            SharedPreferences sharedPreferences = mAppContext.getSharedPreferences("insnap", Context.MODE_PRIVATE);
            String userID = sharedPreferences.getString("userID", null);
            if(userID == null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                userID = UUID.randomUUID().toString();
                editor.putString("userID", userID);
                editor.apply();
            }

            user.put("uuid", userID);
            common.put("user", user);

            this.commonPayload = common;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getCommonPayload() {
        JSONObject common = this.commonPayload;

        JSONObject session = new JSONObject();

        try {
            session.put("id", this.sessionID);
            common.put("session", session);
        } catch (JSONException exception) {
            Log.e("Baryon", exception.getMessage());
        }

        return common;
    }



}
