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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.heymundomx.ads.sdk.R;
import com.heymundomx.ads.sdk.util.Tools;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;
import com.wortise.ads.AdError;
import com.wortise.ads.RevenueData;

public class BannerAd {

    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private AdView adView;
        private AdManagerAdView adManagerAdView;
        private com.facebook.ads.AdView fanAdView;
        FrameLayout ironSourceBannerView;
        private com.wortise.ads.banner.BannerAd wortiseBannerAd;
        FrameLayout wortiseBannerView;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobBannerId = "";
        private String googleAdManagerBannerId = "";
        private String fanBannerId = "";
        private String unityBannerId = "";
        private String appLovinBannerId = "";
        private String appLovinBannerZoneId = "";
        private String ironSourceBannerId = "";
        private String wortiseBannerId = "";
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder build() {
            loadBannerAd();
            return this;
        }

        public Builder setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Builder setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public Builder setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        public Builder setAdMobBannerId(String adMobBannerId) {
            this.adMobBannerId = adMobBannerId;
            return this;
        }

        public Builder setGoogleAdManagerBannerId(String googleAdManagerBannerId) {
            this.googleAdManagerBannerId = googleAdManagerBannerId;
            return this;
        }

        public Builder setFanBannerId(String fanBannerId) {
            this.fanBannerId = fanBannerId;
            return this;
        }

        public Builder setUnityBannerId(String unityBannerId) {
            this.unityBannerId = unityBannerId;
            return this;
        }

        public Builder setAppLovinBannerId(String appLovinBannerId) {
            this.appLovinBannerId = appLovinBannerId;
            return this;
        }

        public Builder setAppLovinBannerZoneId(String appLovinBannerZoneId) {
            this.appLovinBannerZoneId = appLovinBannerZoneId;
            return this;
        }

        public Builder setIronSourceBannerId(String ironSourceBannerId) {
            this.ironSourceBannerId = ironSourceBannerId;
            return this;
        }

        public Builder setWortiseBannerId(String wortiseBannerId) {
            this.wortiseBannerId = wortiseBannerId;
            return this;
        }

        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public Builder setDarkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadBannerAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        FrameLayout adContainerView = activity.findViewById(R.id.admob_banner_view_container);
                        adContainerView.post(() -> {
                            adView = new AdView(activity);
                            adView.setAdUnitId(adMobBannerId);
                            adContainerView.removeAllViews();
                            adContainerView.addView(adView);
                            adView.setAdSize(Tools.getAdSize(activity));
                            adView.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    adContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                    // Code to be executed when an ad request fails.
                                    adContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when an ad opens an overlay that
                                    // covers the screen.
                                }

                                @Override
                                public void onAdClicked() {
                                    // Code to be executed when the user clicks on an ad.
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when the user is about to return
                                    // to the app after tapping on an ad.
                                }
                            });
                        });
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + adMobBannerId);
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        FrameLayout googleAdContainerView = activity.findViewById(R.id.google_ad_banner_view_container);
                        googleAdContainerView.post(() -> {
                            adManagerAdView = new AdManagerAdView(activity);
                            adManagerAdView.setAdUnitId(googleAdManagerBannerId);
                            googleAdContainerView.removeAllViews();
                            googleAdContainerView.addView(adManagerAdView);
                            adManagerAdView.setAdSize(Tools.getAdSize(activity));
                            adManagerAdView.loadAd(Tools.getGoogleAdManagerRequest());
                            adManagerAdView.setAdListener(new AdListener() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);
                                    googleAdContainerView.setVisibility(View.GONE);
                                    loadBackupBannerAd();
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();
                                    googleAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                }
                            });
                        });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanAdView = new com.facebook.ads.AdView(activity, fanBannerId, AdSize.BANNER_HEIGHT_50);
                        RelativeLayout fanAdViewContainer = activity.findViewById(R.id.fan_banner_view_container);
                        fanAdViewContainer.addView(fanAdView);
                        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                            @Override
                            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                fanAdViewContainer.setVisibility(View.GONE);
                                loadBackupBannerAd();
                                Log.d(TAG, "Error load FAN : " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                fanAdViewContainer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        };
                        com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = fanAdView.buildLoadAdConfig().withAdListener(adListener).build();
                        fanAdView.loadAd(loadAdConfig);
                        break;

                    case STARTAPP:
                        RelativeLayout startAppAdView = activity.findViewById(R.id.startapp_banner_view_container);
                        Banner banner = new Banner(activity, new BannerListener() {
                            @Override
                            public void onReceiveAd(View banner) {
                                startAppAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailedToReceiveAd(View banner) {
                                startAppAdView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                                Log.d(TAG, adNetwork + " failed load startapp banner ad : ");
                            }

                            @Override
                            public void onImpression(View view) {

                            }

                            @Override
                            public void onClick(View banner) {
                            }
                        });
                        startAppAdView.addView(banner);
                        break;

                    case WORTISE:
                        wortiseBannerAd = new com.wortise.ads.banner.BannerAd(activity);
                        wortiseBannerAd.setAdSize(Tools.getWortiseAdSize(activity));
                        wortiseBannerAd.setAdUnitId(wortiseBannerId);
                        wortiseBannerView = activity.findViewById(R.id.wortise_banner_view_container);
                        wortiseBannerView.addView(wortiseBannerAd);
                        wortiseBannerAd.loadAd();
                        wortiseBannerAd.setListener(new com.wortise.ads.banner.BannerAd.Listener() {
                            @Override
                            public void onBannerRevenuePaid(@NonNull com.wortise.ads.banner.BannerAd bannerAd, @NonNull RevenueData revenueData) {

                            }

                            @Override
                            public void onBannerImpression(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {

                            }

                            @Override
                            public void onBannerFailedToLoad(@NonNull com.wortise.ads.banner.BannerAd bannerAd, @NonNull AdError adError) {
                                wortiseBannerView.setVisibility(View.GONE);
                                loadBackupBannerAd();
                                Log.d(TAG, "failed to load Wortise banner: " + adError);
                            }

                            @Override
                            public void onBannerClicked(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {

                            }

                            @Override
                            public void onBannerLoaded(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {
                                wortiseBannerView.setVisibility(View.VISIBLE);
                                Log.d(TAG, "Wortise banner loaded");
                            }
                        });
                        break;
                }
                Log.d(TAG, "Banner Ad is enabled");
            } else {
                Log.d(TAG, "Banner Ad is disabled");
            }
        }

        public void loadBackupBannerAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        FrameLayout adContainerView = activity.findViewById(R.id.admob_banner_view_container);
                        adContainerView.post(() -> {
                            adView = new AdView(activity);
                            adView.setAdUnitId(adMobBannerId);
                            adContainerView.removeAllViews();
                            adContainerView.addView(adView);
                            adView.setAdSize(Tools.getAdSize(activity));
                            adView.loadAd(Tools.getAdRequest(activity, legacyGDPR));
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    adContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                    // Code to be executed when an ad request fails.
                                    adContainerView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when an ad opens an overlay that
                                    // covers the screen.
                                }

                                @Override
                                public void onAdClicked() {
                                    // Code to be executed when the user clicks on an ad.
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when the user is about to return
                                    // to the app after tapping on an ad.
                                }
                            });
                        });
                        Log.d(TAG, adNetwork + " Banner Ad unit Id : " + adMobBannerId);
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        FrameLayout googleAdContainerView = activity.findViewById(R.id.google_ad_banner_view_container);
                        googleAdContainerView.post(() -> {
                            adManagerAdView = new AdManagerAdView(activity);
                            adManagerAdView.setAdUnitId(googleAdManagerBannerId);
                            googleAdContainerView.removeAllViews();
                            googleAdContainerView.addView(adManagerAdView);
                            adManagerAdView.setAdSize(Tools.getAdSize(activity));
                            adManagerAdView.loadAd(Tools.getGoogleAdManagerRequest());
                            adManagerAdView.setAdListener(new AdListener() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);
                                    googleAdContainerView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();
                                    googleAdContainerView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                }
                            });
                        });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanAdView = new com.facebook.ads.AdView(activity, fanBannerId, AdSize.BANNER_HEIGHT_50);
                        RelativeLayout fanAdViewContainer = activity.findViewById(R.id.fan_banner_view_container);
                        fanAdViewContainer.addView(fanAdView);
                        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                            @Override
                            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                                fanAdViewContainer.setVisibility(View.GONE);
                                Log.d(TAG, "Error load FAN : " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                fanAdViewContainer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        };
                        com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = fanAdView.buildLoadAdConfig().withAdListener(adListener).build();
                        fanAdView.loadAd(loadAdConfig);
                        break;

                    case STARTAPP:
                        RelativeLayout startAppAdView = activity.findViewById(R.id.startapp_banner_view_container);
                        Banner banner = new Banner(activity, new BannerListener() {
                            @Override
                            public void onReceiveAd(View banner) {
                                startAppAdView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailedToReceiveAd(View banner) {
                                startAppAdView.setVisibility(View.GONE);
                                Log.d(TAG, adNetwork + " failed load startapp banner ad : ");
                            }

                            @Override
                            public void onImpression(View view) {

                            }

                            @Override
                            public void onClick(View banner) {
                            }
                        });
                        startAppAdView.addView(banner);
                        break;

                    case WORTISE:
                        wortiseBannerAd = new com.wortise.ads.banner.BannerAd(activity);
                        wortiseBannerAd.setAdSize(Tools.getWortiseAdSize(activity));
                        wortiseBannerAd.setAdUnitId(wortiseBannerId);
                        wortiseBannerView = activity.findViewById(R.id.wortise_banner_view_container);
                        wortiseBannerView.addView(wortiseBannerAd);
                        wortiseBannerAd.loadAd();
                        wortiseBannerAd.setListener(new com.wortise.ads.banner.BannerAd.Listener() {
                            @Override
                            public void onBannerRevenuePaid(@NonNull com.wortise.ads.banner.BannerAd bannerAd, @NonNull RevenueData revenueData) {

                            }

                            @Override
                            public void onBannerImpression(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {

                            }

                            @Override
                            public void onBannerFailedToLoad(@NonNull com.wortise.ads.banner.BannerAd bannerAd, @NonNull AdError adError) {
                                wortiseBannerView.setVisibility(View.GONE);
                                Log.d(TAG, " [backup] failed to load Wortise banner: " + adError);
                            }

                            @Override
                            public void onBannerClicked(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {

                            }

                            @Override
                            public void onBannerLoaded(@NonNull com.wortise.ads.banner.BannerAd bannerAd) {
                                wortiseBannerView.setVisibility(View.VISIBLE);
                                Log.d(TAG, " [backup] Wortise banner loaded");
                            }
                        });
                        break;

                    case NONE:
                        //do nothing
                        break;
                }
                Log.d(TAG, "Banner Ad is enabled");
            } else {
                Log.d(TAG, "Banner Ad is disabled");
            }
        }

        public void destroyAndDetachBanner() {
        }

    }

}