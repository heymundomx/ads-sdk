package com.heymundomx.ads.sdkdemo.activity;

import static com.heymundomx.ads.sdk.util.Constant.ADMOB;
import static com.heymundomx.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.heymundomx.ads.sdk.util.Constant.APPLOVIN;
import static com.heymundomx.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.heymundomx.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.heymundomx.ads.sdk.util.Constant.WORTISE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.heymundomx.ads.sdk.format.AdNetwork;
import com.heymundomx.ads.sdk.format.AppOpenAd;
import com.heymundomx.ads.sdkdemo.BuildConfig;
import com.heymundomx.ads.sdkdemo.R;
import com.heymundomx.ads.sdkdemo.application.MyApplication;
import com.heymundomx.ads.sdkdemo.callback.CallbackConfig;
import com.heymundomx.ads.sdkdemo.data.Constant;
import com.heymundomx.ads.sdkdemo.database.SharedPref;
import com.heymundomx.ads.sdkdemo.rest.RestAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class ActivitySplash extends AppCompatActivity {
    private static final String TAG = "ActivitySplash";
    Call<CallbackConfig> callbackConfigCall = null;
    public static int DELAY_PROGRESS = 1500;
    AdNetwork.Initialize adNetwork;
    AppOpenAd.Builder appOpenAdBuilder;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        getAppTheme();
        setContentView(R.layout.activity_splash);
        initAds();

        if (Constant.AD_STATUS.equals(AD_STATUS_ON) && Constant.OPEN_ADS_ON_START) {
            if (!Constant.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(this::handleAdShow, DELAY_PROGRESS, TimeUnit.MILLISECONDS);
            } else {
                requestConfig();
            }
        } else {
            requestConfig();
        }
    }

    public void getAppTheme() {
        if (sharedPref.getIsDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private void handleAdShow() {
        switch (Constant.AD_NETWORK) {
            case ADMOB:
                if (!Constant.ADMOB_APP_OPEN_AD_ID.equals("0")) {
                    ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::requestConfig);
                } else {
                    requestConfig();
                }
                break;
            case GOOGLE_AD_MANAGER:
                if (!Constant.GOOGLE_AD_MANAGER_APP_OPEN_AD_ID.equals("0")) {
                    ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::requestConfig);
                } else {
                    requestConfig();
                }
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
                if (!Constant.APPLOVIN_APP_OPEN_AP_ID.equals("0")) {
                    ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::requestConfig);
                } else {
                    requestConfig();
                }
                break;
            case WORTISE:
                if (!Constant.WORTISE_APP_OPEN_AD_ID.equals("0")) {
                    ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::requestConfig);
                } else {
                    requestConfig();
                }
                break;
            default:
                requestConfig();
                break;
        }
    }

    private void requestConfig() {
        requestAPI("https://raw.githubusercontent.com/solodroidev/content/uploads/json/android.json");
    }

    private void requestAPI(@SuppressWarnings("SameParameterValue") String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            if (url.contains("https://drive.google.com")) {
                String driveUrl = url.replace("https://", "").replace("http://", "");
                List<String> data = Arrays.asList(driveUrl.split("/"));
                String googleDriveFileId = data.get(3);
                callbackConfigCall = RestAdapter.createApi().getDriveJsonFileId(googleDriveFileId);
            } else {
                callbackConfigCall = RestAdapter.createApi().getJsonUrl(url);
            }
        } else {
            callbackConfigCall = RestAdapter.createApi().getDriveJsonFileId(url);
        }
        callbackConfigCall.enqueue(new Callback<>() {
            public void onResponse(@NonNull Call<CallbackConfig> call, @NonNull Response<CallbackConfig> response) {
                CallbackConfig resp = response.body();
                if (resp != null) {
                    sharedPref.savePostList(resp.android);
                    loadOpenAds();
                    Log.d(TAG, "responses success");
                } else {
                    loadOpenAds();
                    Log.d(TAG, "responses null");
                }
            }

            public void onFailure(@NonNull Call<CallbackConfig> call, @NonNull Throwable th) {
                Log.d(TAG, "responses failed: " + th.getMessage());
                loadOpenAds();
            }
        });
    }

    private void initAds() {
        adNetwork = new AdNetwork.Initialize(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobAppId(null)
                .setStartappAppId(Constant.STARTAPP_APP_ID)
                .setUnityGameId(Constant.UNITY_GAME_ID)
                .setIronSourceAppKey(Constant.IRONSOURCE_APP_KEY)
                .setWortiseAppId(Constant.WORTISE_APP_ID)
                .setDebug(BuildConfig.DEBUG)
                .build();
    }

    private void loadOpenAds() {
        if (Constant.FORCE_TO_SHOW_APP_OPEN_AD_ON_START && Constant.OPEN_ADS_ON_START) {
            appOpenAdBuilder = new AppOpenAd.Builder(this)
                    .setAdStatus(Constant.AD_STATUS)
                    .setAdNetwork(Constant.AD_NETWORK)
                    .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                    .setAdMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                    .setAdManagerAppOpenId(Constant.GOOGLE_AD_MANAGER_APP_OPEN_AD_ID)
                    .setApplovinAppOpenId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                    .setWortiseAppOpenId(Constant.WORTISE_APP_OPEN_AD_ID)
                    .build(this::startMainActivity);
        }
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}