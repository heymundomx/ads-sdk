package com.heymundomx.ads.sdk.format;

import static com.heymundomx.ads.sdk.util.Constant.ADMOB;
import static com.heymundomx.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.heymundomx.ads.sdk.util.Constant.FACEBOOK;
import static com.heymundomx.ads.sdk.util.Constant.FAN;
import static com.heymundomx.ads.sdk.util.Constant.FAN_BIDDING_ADMOB;
import static com.heymundomx.ads.sdk.util.Constant.FAN_BIDDING_AD_MANAGER;
import static com.heymundomx.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.heymundomx.ads.sdk.util.Constant.STARTAPP;
import static com.heymundomx.ads.sdk.util.Constant.WORTISE;
import static com.heymundomx.ads.sdk.util.Constant.NONE;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.heymundomx.ads.sdk.helper.AudienceNetworkInitializeHelper;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.wortise.ads.AdSettings;
import com.wortise.ads.WortiseSdk;

import java.util.Map;

import kotlin.Unit;

public class AdNetwork {

    public static class Initialize {

        private static final String TAG = "AdNetwork";
        Activity activity;
        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobAppId = "";
        private String startappAppId = "0";
        private String unityGameId = "";
        private String ironSourceAppKey = "";
        private String wortiseAppId = "";
        private boolean debug = true;

        public Initialize(Activity activity) {
            this.activity = activity;
        }

        public Initialize build() {
            initAds();
            initBackupAds();
            return this;
        }

        public Initialize setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Initialize setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public Initialize setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        public Initialize setAdMobAppId(String adMobAppId) {
            this.adMobAppId = adMobAppId;
            return this;
        }

        public Initialize setStartappAppId(String startappAppId) {
            this.startappAppId = startappAppId;
            return this;
        }

        public Initialize setUnityGameId(String unityGameId) {
            this.unityGameId = unityGameId;
            return this;
        }

        public Initialize setAppLovinSdkKey(String appLovinSdkKey) {
            return this;
        }

        public Initialize setMopubBannerId(String mopubBannerId) {
            return this;
        }

        public Initialize setIronSourceAppKey(String ironSourceAppKey) {
            this.ironSourceAppKey = ironSourceAppKey;
            return this;
        }

        public Initialize setWortiseAppId(String wortiseAppId) {
            this.wortiseAppId = wortiseAppId;
            return this;
        }

        public Initialize setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public void initAds() {
            if (adStatus.equals(AD_STATUS_ON)) {
                switch (adNetwork) {
                    case ADMOB:
                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_ADMOB:
                    case FAN_BIDDING_AD_MANAGER:
                        MobileAds.initialize(activity, initializationStatus -> {
                            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                            for (String adapterClass : statusMap.keySet()) {
                                AdapterStatus adapterStatus = statusMap.get(adapterClass);
                                assert adapterStatus != null;
                                Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, adapterStatus.getDescription(), adapterStatus.getLatency()));
                            }
                        });
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;

                    case FAN:
                    case FACEBOOK:
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;

                    case STARTAPP:
                        StartAppSDK.init(activity, startappAppId, false);
                        StartAppSDK.setTestAdsEnabled(debug);
                        StartAppAd.disableSplash();
                        break;

                    case WORTISE:
                        WortiseSdk.initialize(activity, wortiseAppId, () -> Unit.INSTANCE);
                        AdSettings.setTestEnabled(debug);
                        break;
                }
                Log.d(TAG, "[" + adNetwork + "] is selected as Primary Ads");
            }
        }

        public void initBackupAds() {
            if (adStatus.equals(AD_STATUS_ON)) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_ADMOB:
                    case FAN_BIDDING_AD_MANAGER:
                        MobileAds.initialize(activity, initializationStatus -> {
                            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                            for (String adapterClass : statusMap.keySet()) {
                                AdapterStatus adapterStatus = statusMap.get(adapterClass);
                                assert adapterStatus != null;
                                Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, adapterStatus.getDescription(), adapterStatus.getLatency()));
                            }
                        });
                        AudienceNetworkInitializeHelper.initialize(activity);
                        break;

                    case FAN:
                    case FACEBOOK:
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;

                    case STARTAPP:
                        StartAppSDK.init(activity, startappAppId, false);
                        StartAppSDK.setTestAdsEnabled(debug);
                        StartAppAd.disableSplash();
                        break;

                    case WORTISE:
                        WortiseSdk.initialize(activity, wortiseAppId, () -> Unit.INSTANCE);
                        AdSettings.setTestEnabled(debug);
                        break;

                    case NONE:
                        //do nothing
                        break;
                }
                Log.d(TAG, "[" + backupAdNetwork + "] is selected as Backup Ads");
            }
        }

    }

}