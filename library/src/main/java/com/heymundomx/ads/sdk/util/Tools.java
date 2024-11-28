package com.heymundomx.ads.sdk.util;

import static com.heymundomx.ads.sdk.util.Constant.TOKEN;
import static com.heymundomx.ads.sdk.util.Constant.VALUE;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.heymundomx.ads.sdk.gdpr.LegacyGDPR;

import java.nio.charset.StandardCharsets;

public class Tools {

    public static AdSize getAdSize(Activity activity) {
        int adWidth = getScreenWidthInDp(activity);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public static com.wortise.ads.AdSize getWortiseAdSize(Activity activity) {
        int adWidth = getScreenWidthInDp(activity);
        return com.wortise.ads.AdSize.getAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public static int getScreenWidthInDp(Activity activity) {
        float widthPixels;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics metrics = activity.getWindowManager().getCurrentWindowMetrics();
            widthPixels = metrics.getBounds().width();
        } else {
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            widthPixels = metrics.widthPixels;
        }
        float density = activity.getResources().getDisplayMetrics().density;
        return (int) (widthPixels / density);
    }

    public static AdSize getAdSizeMREC() {
        return AdSize.MEDIUM_RECTANGLE;
    }

    public static AdRequest getAdRequest(Activity activity, Boolean legacyGDPR) {
        //Bundle extras = new FacebookExtras().setNativeBanner(true).build();
        if (legacyGDPR) {
            return new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, LegacyGDPR.getBundleAd(activity))
                    //.addNetworkExtrasBundle(FacebookAdapter.class, extras)
                    .build();
        } else {
            return new AdRequest.Builder()
                    //.addNetworkExtrasBundle(FacebookAdapter.class, extras)
                    .build();
        }
    }

    @SuppressLint("VisibleForTests")
    public static AdManagerAdRequest getGoogleAdManagerRequest() {
        return new AdManagerAdRequest.Builder()
                .build();
    }

    public static String decode(String code) {
        return decodeBase64(decodeBase64(decodeBase64(code)));
    }

    public static String decodeBase64(String code) {
        byte[] valueDecoded = Base64.decode(code.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

    public static String jsonDecode(String code) {
        String data = code.replace(TOKEN, VALUE);
        byte[] valueDecoded = Base64.decode(data.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

}