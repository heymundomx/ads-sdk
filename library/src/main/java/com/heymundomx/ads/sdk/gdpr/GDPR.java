package com.heymundomx.ads.sdk.gdpr;

import static com.heymundomx.ads.sdk.util.Constant.ADMOB;
import static com.heymundomx.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.heymundomx.ads.sdk.util.Constant.WORTISE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.wortise.ads.WortiseSdk;
import com.wortise.ads.consent.ConsentManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

import kotlin.Unit;

public class GDPR {

    ConsentInformation consentInformation;
    ConsentDebugSettings debugSettings;
    ConsentRequestParameters params;
    AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    ConsentForm consentForm;
    Activity activity;

    public GDPR(Activity activity) {
        this.activity = activity;
    }

    public void updateGDPRConsentStatus() {
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();
        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(activity, params, () -> {
                    if (consentInformation.isConsentFormAvailable()) {
                        loadForm(activity);
                    }
                },
                formError -> {
                });
        Log.d("GDPR", "AdMob GDPR is selected");
    }

    @SuppressLint("HardwareIds")
    public void updateGDPRConsentStatus(String adType, boolean isDebug, boolean childDirected) {
        switch (adType) {
            case ADMOB:
            case GOOGLE_AD_MANAGER:
                if (isDebug) {
                    String androidId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                    String deviceId = md5(androidId).toUpperCase();
                    debugSettings = new ConsentDebugSettings.Builder(activity).setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                            .addTestDeviceHashedId(deviceId)
                            .build();
                    params = new ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).setTagForUnderAgeOfConsent(childDirected).build();
                } else {
                    params = new ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(childDirected).build();
                }
                consentInformation = UserMessagingPlatform.getConsentInformation(activity);
                consentInformation.requestConsentInfoUpdate(activity, params, () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                                activity, loadAndShowError -> {
                                    if (consentInformation.canRequestAds()) {
                                        initializeMobileAdsSdk();
                                    }
                                }
                        ),
                        requestConsentError -> {
                        });
                if (consentInformation.canRequestAds()) {
                    initializeMobileAdsSdk();
                }
                break;

            case WORTISE:
                WortiseSdk.wait(() -> {
                    ConsentManager.requestIfRequired(activity);
                    return Unit.INSTANCE;
                });
                break;
        }
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }
        MobileAds.initialize(activity);
    }

    public void loadForm(Activity activity) {
        UserMessagingPlatform.loadConsentForm(activity, consentForm -> {
                    this.consentForm = consentForm;
                    if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                        consentForm.show(activity, formError -> loadForm(activity));
                    }
                },
                formError -> {
                }
        );
    }

    public static String md5(final String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & b));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            //Logger.logStackTrace(TAG,e);
        }
        return "";
    }

}