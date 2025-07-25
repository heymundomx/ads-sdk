package com.heymundomx.ads.sdkdemo.activity;

import static com.heymundomx.ads.sdk.util.Constant.ADMOB;
import static com.heymundomx.ads.sdk.util.Constant.FAN;
import static com.heymundomx.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.heymundomx.ads.sdk.util.Constant.STARTAPP;
import static com.heymundomx.ads.sdk.util.Constant.WORTISE;
import static com.heymundomx.ads.sdkdemo.data.Constant.STYLE_DEFAULT;
import static com.heymundomx.ads.sdkdemo.data.Constant.STYLE_NEWS;
import static com.heymundomx.ads.sdkdemo.data.Constant.STYLE_RADIO;
import static com.heymundomx.ads.sdkdemo.data.Constant.STYLE_VIDEO_LARGE;
import static com.heymundomx.ads.sdkdemo.data.Constant.STYLE_VIDEO_SMALL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.heymundomx.ads.sdk.format.AdNetwork;
import com.heymundomx.ads.sdk.format.AppOpenAd;
import com.heymundomx.ads.sdk.format.BannerAd;
import com.heymundomx.ads.sdk.format.InterstitialAd;
import com.heymundomx.ads.sdk.format.MediumRectangleAd;
import com.heymundomx.ads.sdk.format.NativeAd;
import com.heymundomx.ads.sdk.format.NativeAdView;
import com.heymundomx.ads.sdk.format.RewardedAd;
import com.heymundomx.ads.sdk.gdpr.GDPR;
import com.heymundomx.ads.sdkdemo.BuildConfig;
import com.heymundomx.ads.sdkdemo.R;
import com.heymundomx.ads.sdkdemo.data.Constant;
import com.heymundomx.ads.sdkdemo.database.SharedPref;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    AdNetwork.Initialize adNetwork;
    GDPR gdpr;
    BannerAd.Builder bannerAd;
    MediumRectangleAd.Builder mediumRectangleAd;
    InterstitialAd.Builder interstitialAd;
    RewardedAd.Builder rewardedAd;
    NativeAd.Builder nativeAd;
    NativeAdView.Builder nativeAdView;
    SwitchMaterial switchMaterial;
    SharedPref sharedPref;
    Button btnInterstitial;
    Button btnRewarded;
    Button btnSelectAds;
    Button btnNativeAdStyle;
    LinearLayout nativeAdViewContainer;
    LinearLayout bannerAdView;
    AppOpenAd.Builder appOpenAdBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        getAppTheme();
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        if (Constant.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (sharedPref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_dark_toolbar));
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        bannerAdView = findViewById(R.id.banner_ad_view);
        bannerAdView.addView(View.inflate(this, com.heymundomx.ads.sdk.R.layout.view_banner_ad, null));

        initAds();
        loadGdpr();
        loadOpenAds();
        loadBannerAd();
        loadInterstitialAd();
        loadRewardedAd();

        nativeAdViewContainer = findViewById(R.id.native_ad);
        setNativeAdStyle(nativeAdViewContainer);
        loadNativeAd();

        btnInterstitial = findViewById(R.id.btn_interstitial);
        btnInterstitial.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SecondActivity.class));
            showInterstitialAd();
            destroyBannerAd();
        });

        btnRewarded = findViewById(R.id.btn_rewarded);
        btnRewarded.setOnClickListener(view -> showRewardedAd());

        btnSelectAds = findViewById(R.id.btn_select_ads);
        btnSelectAds.setOnClickListener(v -> showAdChooser());

        btnNativeAdStyle = findViewById(R.id.btn_native_ad_style);
        btnNativeAdStyle.setOnClickListener(v -> changeNativeAdStyle());

        switchAppTheme();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
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
        if (Constant.OPEN_ADS_ON_RESUME) {
            appOpenAdBuilder = new AppOpenAd.Builder(this)
                    .setAdStatus(Constant.AD_STATUS)
                    .setAdNetwork(Constant.AD_NETWORK)
                    .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                    .setAdMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                    .setAdManagerAppOpenId(Constant.GOOGLE_AD_MANAGER_APP_OPEN_AD_ID)
                    .setApplovinAppOpenId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                    .setWortiseAppOpenId(Constant.WORTISE_APP_OPEN_AD_ID)
                    .build();
        }
    }

    LifecycleObserver lifecycleObserver = new DefaultLifecycleObserver() {
        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStart(owner);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (Constant.OPEN_ADS_ON_RESUME) {
                    if (AppOpenAd.isAppOpenAdLoaded) {
                        appOpenAdBuilder.show();
                    }
                }
            }, 100);
        }
    };

    private void loadBannerAd() {
        bannerAd = new BannerAd.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobBannerId(Constant.ADMOB_BANNER_ID)
                .setGoogleAdManagerBannerId(Constant.GOOGLE_AD_MANAGER_BANNER_ID)
                .setFanBannerId(Constant.FAN_BANNER_ID)
                .setUnityBannerId(Constant.UNITY_BANNER_ID)
                .setAppLovinBannerId(Constant.APPLOVIN_BANNER_ID)
                .setAppLovinBannerZoneId(Constant.APPLOVIN_BANNER_ZONE_ID)
                .setIronSourceBannerId(Constant.IRONSOURCE_BANNER_ID)
                .setWortiseBannerId(Constant.WORTISE_BANNER_ID)
                .setDarkTheme(sharedPref.getIsDarkTheme())
                .build();
    }

    @SuppressWarnings("unused")
    private void loadMediumRectangleAd() {
        mediumRectangleAd = new MediumRectangleAd.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobBannerId(Constant.ADMOB_BANNER_ID)
                .setGoogleAdManagerBannerId(Constant.GOOGLE_AD_MANAGER_BANNER_ID)
                .setFanBannerId(Constant.FAN_BANNER_ID)
                .setUnityBannerId(Constant.UNITY_BANNER_ID)
                .setAppLovinBannerId(Constant.APPLOVIN_BANNER_ID)
                .setAppLovinBannerZoneId(Constant.APPLOVIN_BANNER_ZONE_ID)
                .setIronSourceBannerId(Constant.IRONSOURCE_BANNER_ID)
                .setDarkTheme(sharedPref.getIsDarkTheme())
                .build();
    }

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobInterstitialId(Constant.ADMOB_INTERSTITIAL_ID)
                .setGoogleAdManagerInterstitialId(Constant.GOOGLE_AD_MANAGER_INTERSTITIAL_ID)
                .setFanInterstitialId(Constant.FAN_INTERSTITIAL_ID)
                .setUnityInterstitialId(Constant.UNITY_INTERSTITIAL_ID)
                .setAppLovinInterstitialId(Constant.APPLOVIN_INTERSTITIAL_ID)
                .setAppLovinInterstitialZoneId(Constant.APPLOVIN_INTERSTITIAL_ZONE_ID)
                .setIronSourceInterstitialId(Constant.IRONSOURCE_INTERSTITIAL_ID)
                .setWortiseInterstitialId(Constant.WORTISE_INTERSTITIAL_ID)
                .setInterval(Constant.INTERSTITIAL_AD_INTERVAL)
                .build(() -> Log.d(TAG, "onAdDismissed"));
    }

    private void loadRewardedAd() {
        rewardedAd = new RewardedAd.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setMainAds(Constant.AD_NETWORK)
                .setBackupAds(Constant.BACKUP_AD_NETWORK)
                .setAdMobRewardedId(Constant.ADMOB_REWARDED_ID)
                .setAdManagerRewardedId(Constant.GOOGLE_AD_MANAGER_REWARDED_ID)
                .setFanRewardedId(Constant.FAN_REWARDED_ID)
                .setUnityRewardedId(Constant.UNITY_REWARDED_ID)
                .setApplovinMaxRewardedId(Constant.APPLOVIN_MAX_REWARDED_ID)
                .setApplovinDiscRewardedZoneId(Constant.APPLOVIN_DISC_REWARDED_ZONE_ID)
                .setIronSourceRewardedId(Constant.IRONSOURCE_REWARDED_ID)
                .setWortiseRewardedId(Constant.WORTISE_REWARDED_ID)
                .build(() -> Toast.makeText(getApplicationContext(), "Rewarded complete", Toast.LENGTH_SHORT).show(), () -> {

                });
    }

    private void showRewardedAd() {
        rewardedAd.show(() -> Toast.makeText(getApplicationContext(), "Rewarded complete", Toast.LENGTH_SHORT).show(), () -> {

        }, () -> Toast.makeText(getApplicationContext(), "Rewarded error", Toast.LENGTH_SHORT).show());
    }

    private void showInterstitialAd() {
        interstitialAd.show(() -> Log.d(TAG, "onAdShowed"), () -> Log.d(TAG, "onAdDismissed"));

    }

    private void loadNativeAd() {
        nativeAd = new NativeAd.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobNativeId(Constant.ADMOB_NATIVE_ID)
                .setAdManagerNativeId(Constant.GOOGLE_AD_MANAGER_NATIVE_ID)
                .setFanNativeId(Constant.FAN_NATIVE_ID)
                .setAppLovinNativeId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                .setAppLovinDiscoveryMrecZoneId(Constant.APPLOVIN_BANNER_MREC_ZONE_ID)
                .setWortiseNativeId(Constant.WORTISE_NATIVE_ID)
                .setNativeAdStyle(Constant.NATIVE_STYLE)
                .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                .setPadding(0, 0, 0, 0)
                .setDarkTheme(sharedPref.getIsDarkTheme())
                .build();
    }

    private void loadNativeAdView(View view) {
        nativeAdView = new NativeAdView.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobNativeId(Constant.ADMOB_NATIVE_ID)
                .setAdManagerNativeId(Constant.GOOGLE_AD_MANAGER_NATIVE_ID)
                .setFanNativeId(Constant.FAN_NATIVE_ID)
                .setAppLovinNativeId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                .setAppLovinDiscoveryMrecZoneId(Constant.APPLOVIN_BANNER_MREC_ZONE_ID)
                .setWortiseNativeId(Constant.WORTISE_NATIVE_ID)
                .setNativeAdStyle(Constant.NATIVE_STYLE)
                .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                .setDarkTheme(sharedPref.getIsDarkTheme())
                .setView(view)
                .build();

        nativeAdView.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyBannerAd();
        destroyAppOpenAd();
        Constant.isAppOpen = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getAppTheme() {
        if (sharedPref.getIsDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private void switchAppTheme() {
        switchMaterial = findViewById(R.id.switch_theme);
        switchMaterial.setChecked(sharedPref.getIsDarkTheme());
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsDarkTheme(isChecked);
            recreate();
        });
    }

    private void showAdChooser() {
        final String[] ads = {"AdMob", "Google Ad Manager", "FAN (Waterfall)", "StartApp", "Wortise"};

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.btn_show_choose_ad))
                .setItems(ads, (dialog, which) -> {
                    String selectedItem = ads[which];
                    switch (selectedItem) {
                        case "AdMob":
                            Constant.AD_NETWORK = ADMOB;
                            break;
                        case "Google Ad Manager":
                            Constant.AD_NETWORK = GOOGLE_AD_MANAGER;
                            break;
                        case "FAN (Waterfall)":
                            Constant.AD_NETWORK = FAN;
                            break;
                        case "StartApp":
                            Constant.AD_NETWORK = STARTAPP;
                            break;
                        case "Wortise":
                            Constant.AD_NETWORK = WORTISE;
                            break;
                    }
                    recreate();
                })
                .setCancelable(false)
                .setNegativeButton(getString(R.string.btn_cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setNativeAdStyle(LinearLayout nativeAdView) {
        switch (Constant.NATIVE_STYLE) {
            case "news":
                nativeAdView.addView(View.inflate(this, com.heymundomx.ads.sdk.R.layout.view_native_ad_news, null));
                break;
            case "radio":
                nativeAdView.addView(View.inflate(this, com.heymundomx.ads.sdk.R.layout.view_native_ad_radio, null));
                break;
            case "video_small":
                nativeAdView.addView(View.inflate(this, com.heymundomx.ads.sdk.R.layout.view_native_ad_video_small, null));
                break;
            case "video_large":
                nativeAdView.addView(View.inflate(this, com.heymundomx.ads.sdk.R.layout.view_native_ad_video_large, null));
                break;
            default:
                nativeAdView.addView(View.inflate(this, com.heymundomx.ads.sdk.R.layout.view_native_ad_medium, null));
                break;
        }
    }

    private void changeNativeAdStyle() {
        final String[] styles = {"Default", "News", "Radio", "Video Small", "Video Large"};
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.message_show_choose_style_native_ad))
                .setItems(styles, (dialog, which) -> {
                    String selectedItem = styles[which];
                    switch (selectedItem) {
                        case "Default":
                            Constant.NATIVE_STYLE = STYLE_DEFAULT;
                            break;
                        case "News":
                            Constant.NATIVE_STYLE = STYLE_NEWS;
                            break;
                        case "Radio":
                            Constant.NATIVE_STYLE = STYLE_RADIO;
                            break;
                        case "Video Small":
                            Constant.NATIVE_STYLE = STYLE_VIDEO_SMALL;
                            break;
                        case "Video Large":
                            Constant.NATIVE_STYLE = STYLE_VIDEO_LARGE;
                            break;
                    }
                    recreate();
                })
                .setCancelable(false)
                .setNegativeButton(getString(R.string.btn_cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showExitDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_exit, null);
        LinearLayout nativeAdViewContainer = view.findViewById(R.id.native_ad_view);
        setNativeAdStyle(nativeAdViewContainer);
        loadNativeAdView(view);

        new MaterialAlertDialogBuilder(this)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.btn_ok), (dialogInterface, i) -> {
                    destroyBannerAd();
                    destroyAppOpenAd();
                    Constant.isAppOpen = false;
                    finishAffinity();
                    System.exit(0);
                })
                .setNegativeButton(getString(R.string.btn_cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void destroyBannerAd() {
        bannerAd.destroyAndDetachBanner();
    }

    private void destroyAppOpenAd() {
        if (Constant.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            appOpenAdBuilder.destroyOpenAd();
            ProcessLifecycleOwner.get().getLifecycle().removeObserver(lifecycleObserver);
        }
    }

    private void loadGdpr() {
        gdpr = new GDPR(this);
        gdpr.updateGDPRConsentStatus(Constant.AD_NETWORK, false, false);
    }

}