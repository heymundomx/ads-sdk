package com.heymundomx.ads.sdk.format;

import static com.heymundomx.ads.sdk.util.Constant.ADMOB;
import static com.heymundomx.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.heymundomx.ads.sdk.util.Constant.FACEBOOK;
import static com.heymundomx.ads.sdk.util.Constant.FAN;
import static com.heymundomx.ads.sdk.util.Constant.FAN_BIDDING_ADMOB;
import static com.heymundomx.ads.sdk.util.Constant.FAN_BIDDING_AD_MANAGER;
import static com.heymundomx.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.heymundomx.ads.sdk.util.Constant.WORTISE;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.heymundomx.ads.sdk.util.OnRewardedAdCompleteListener;
import com.heymundomx.ads.sdk.util.OnRewardedAdErrorListener;
import com.heymundomx.ads.sdk.util.OnRewardedAdLoadedListener;
import com.heymundomx.ads.sdk.util.Tools;
import com.wortise.ads.RevenueData;
import com.wortise.ads.rewarded.models.Reward;

public class RewardedVideoAd {

    public static class Builder {

        private static final String TAG = "SoloRewarded";
        private final Activity activity;
        private RewardedAd adMobRewardedAd;
        private RewardedAd adManagerRewardedAd;
        private com.facebook.ads.RewardedVideoAd fanRewardedVideoAd;
        private com.wortise.ads.rewarded.RewardedAd wortiseRewardedAd;
        private String adStatus = "";
        private String mainAds = "";
        private String backupAds = "";
        private String adMobRewardedId = "";
        private String adManagerRewardedId = "";
        private String fanRewardedId = "";
        private String unityRewardedId = "";
        private String applovinMaxRewardedId = "";
        private String applovinDiscRewardedZoneId = "";
        private String ironSourceRewardedId = "";
        private String wortiseRewardedId = "";
        private int placementStatus = 1;
        private boolean legacyGDPR = false;
        private boolean showRewardedAdIfLoaded = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder build(OnRewardedAdLoadedListener onLoaded, OnRewardedAdCompleteListener onComplete, OnRewardedAdErrorListener onError) {
            loadRewardedAd(onLoaded, onComplete, onError);
            return this;
        }

        public Builder show(OnRewardedAdCompleteListener onRewardedAdCompleteListener, OnRewardedAdErrorListener onRewardedAdErrorListener) {
            showRewardedAd(onRewardedAdCompleteListener, onRewardedAdErrorListener);
            return this;
        }

        public Builder setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Builder setMainAds(String mainAds) {
            this.mainAds = mainAds;
            return this;
        }

        public Builder setBackupAds(String backupAds) {
            this.backupAds = backupAds;
            return this;
        }

        public Builder setAdMobRewardedId(String adMobRewardedId) {
            this.adMobRewardedId = adMobRewardedId;
            return this;
        }

        public Builder setAdManagerRewardedId(String adManagerRewardedId) {
            this.adManagerRewardedId = adManagerRewardedId;
            return this;
        }

        public Builder setFanRewardedId(String fanRewardedId) {
            this.fanRewardedId = fanRewardedId;
            return this;
        }

        public Builder setUnityRewardedId(String unityRewardedId) {
            this.unityRewardedId = unityRewardedId;
            return this;
        }

        public Builder setApplovinMaxRewardedId(String applovinMaxRewardedId) {
            this.applovinMaxRewardedId = applovinMaxRewardedId;
            return this;
        }

        public Builder setApplovinDiscRewardedZoneId(String applovinDiscRewardedZoneId) {
            this.applovinDiscRewardedZoneId = applovinDiscRewardedZoneId;
            return this;
        }

        public Builder setIronSourceRewardedId(String ironSourceRewardedId) {
            this.ironSourceRewardedId = ironSourceRewardedId;
            return this;
        }

        public Builder setWortiseRewardedId(String wortiseRewardedId) {
            this.wortiseRewardedId = wortiseRewardedId;
            return this;
        }

        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public Builder showRewardedAdIfLoaded(boolean showRewardedAdIfLoaded) {
            this.showRewardedAdIfLoaded = showRewardedAdIfLoaded;
            return this;
        }

        public void loadRewardedAd(OnRewardedAdLoadedListener onLoaded, OnRewardedAdCompleteListener onComplete, OnRewardedAdErrorListener onError) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (mainAds) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        RewardedAd.load(activity, adMobRewardedId, Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.d(TAG, loadAdError.toString());
                                adMobRewardedAd = null;
                                loadRewardedBackupAd(onLoaded, onComplete, onError);
                                Log.d(TAG, "[" + mainAds + "] " + "failed to load rewarded ad: " + loadAdError.getMessage() + ", try to load backup ad: " + backupAds);
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd ad) {
                                adMobRewardedAd = ad;
                                adMobRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        adMobRewardedAd = null;
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                        adMobRewardedAd = null;
                                    }
                                });
                                if (showRewardedAdIfLoaded) {
                                    showRewardedAd(onComplete, onError);
                                } else {
                                    onLoaded.onRewardedAdLoaded();
                                }
                                Log.d(TAG, "[" + mainAds + "] " + "rewarded ad loaded");
                            }
                        });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        RewardedAd.load(activity, adManagerRewardedId, Tools.getGoogleAdManagerRequest(), new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.d(TAG, loadAdError.toString());
                                adManagerRewardedAd = null;
                                loadRewardedBackupAd(onLoaded, onComplete, onError);
                                Log.d(TAG, "[" + mainAds + "] " + "failed to load rewarded ad: " + loadAdError.getMessage() + ", try to load backup ad: " + backupAds);
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd ad) {
                                adManagerRewardedAd = ad;
                                adManagerRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        adManagerRewardedAd = null;
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                        adManagerRewardedAd = null;
                                    }
                                });
                                if (showRewardedAdIfLoaded) {
                                    showRewardedAd(onComplete, onError);
                                } else {
                                    onLoaded.onRewardedAdLoaded();
                                }
                                Log.d(TAG, "[" + mainAds + "] " + "rewarded ad loaded");
                            }
                        });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                        fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
                                .withAdListener(new RewardedVideoAdListener() {
                                    @Override
                                    public void onRewardedVideoCompleted() {
                                        onComplete.onRewardedAdComplete();
                                        Log.d(TAG, "[" + mainAds + "] " + "rewarded ad complete");
                                    }

                                    @Override
                                    public void onRewardedVideoClosed() {
                                        Log.d(TAG, "[" + mainAds + "] " + "rewarded ad closed");
                                    }

                                    @Override
                                    public void onError(Ad ad, AdError adError) {
                                        loadRewardedBackupAd(onLoaded, onComplete, onError);
                                        Log.d(TAG, "[" + mainAds + "] " + "failed to load rewarded ad: " + fanRewardedId + ", try to load backup ad: " + backupAds);
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        if (showRewardedAdIfLoaded) {
                                            showRewardedAd(onComplete, onError);
                                        } else {
                                            onLoaded.onRewardedAdLoaded();
                                        }
                                        Log.d(TAG, "[" + mainAds + "] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {

                                    }
                                })
                                .build());
                        break;

                    case WORTISE:
                        wortiseRewardedAd = new com.wortise.ads.rewarded.RewardedAd(activity, wortiseRewardedId);
                        wortiseRewardedAd.setListener(new com.wortise.ads.rewarded.RewardedAd.Listener() {
                            @Override
                            public void onRewardedRevenuePaid(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd, @NonNull RevenueData revenueData) {

                            }

                            @Override
                            public void onRewardedImpression(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {

                            }

                            @Override
                            public void onRewardedFailedToShow(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd, @NonNull com.wortise.ads.AdError adError) {
                                loadRewardedBackupAd(onLoaded, onComplete, onError);
                                Log.d(TAG, "[" + mainAds + "] " + "failed to load rewarded ad: " + adError + ", try to load backup ad: " + backupAds);
                            }

                            @Override
                            public void onRewardedFailedToLoad(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd, @NonNull com.wortise.ads.AdError adError) {
                                loadRewardedBackupAd(onLoaded, onComplete, onError);
                                Log.d(TAG, "[" + mainAds + "] " + "failed to load rewarded ad: " + adError + ", try to load backup ad: " + backupAds);
                            }

                            @Override
                            public void onRewardedClicked(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {

                            }

                            @Override
                            public void onRewardedCompleted(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd, @NonNull Reward reward) {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "[" + mainAds + "] " + "rewarded ad complete");
                            }

                            @Override
                            public void onRewardedDismissed(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {
                                Log.d(TAG, "[" + mainAds + "] " + "rewarded ad dismissed");
                            }

                            @Override
                            public void onRewardedLoaded(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {
                                if (showRewardedAdIfLoaded) {
                                    showRewardedAd(onComplete, onError);
                                } else {
                                    onLoaded.onRewardedAdLoaded();
                                }
                                Log.d(TAG, "[" + mainAds + "] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onRewardedShown(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {

                            }
                        });
                        wortiseRewardedAd.loadAd();
                        break;
                }
            }
        }

        public void loadRewardedBackupAd(OnRewardedAdLoadedListener onLoaded, OnRewardedAdCompleteListener onComplete, OnRewardedAdErrorListener onError) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAds) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        RewardedAd.load(activity, adMobRewardedId, Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.d(TAG, loadAdError.toString());
                                adMobRewardedAd = null;
                                Log.d(TAG, "[" + backupAds + "] [backup] " + "failed to load rewarded ad: " + loadAdError.getMessage() + ", try to load backup ad: " + backupAds);
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd ad) {
                                adMobRewardedAd = ad;
                                adMobRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        adMobRewardedAd = null;
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                        adMobRewardedAd = null;
                                    }
                                });
                                if (showRewardedAdIfLoaded) {
                                    showRewardedBackupAd(onComplete, onError);
                                } else {
                                    onLoaded.onRewardedAdLoaded();
                                }
                                Log.d(TAG, "[" + backupAds + "] [backup] " + "rewarded ad loaded");
                            }
                        });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        RewardedAd.load(activity, adManagerRewardedId, Tools.getGoogleAdManagerRequest(), new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.d(TAG, loadAdError.toString());
                                adManagerRewardedAd = null;
                                Log.d(TAG, "[" + backupAds + "] [backup] " + "failed to load rewarded ad: " + loadAdError.getMessage() + ", try to load backup ad: " + backupAds);
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd ad) {
                                adManagerRewardedAd = ad;
                                adManagerRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        adManagerRewardedAd = null;
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                        adManagerRewardedAd = null;
                                    }
                                });
                                if (showRewardedAdIfLoaded) {
                                    showRewardedBackupAd(onComplete, onError);
                                } else {
                                    onLoaded.onRewardedAdLoaded();
                                }
                                Log.d(TAG, "[" + backupAds + "] [backup] " + "rewarded ad loaded");
                            }
                        });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                        fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
                                .withAdListener(new RewardedVideoAdListener() {
                                    @Override
                                    public void onRewardedVideoCompleted() {
                                        onComplete.onRewardedAdComplete();
                                        Log.d(TAG, "[" + backupAds + "] [backup] " + "rewarded ad complete");
                                    }

                                    @Override
                                    public void onRewardedVideoClosed() {
                                        Log.d(TAG, "[" + backupAds + "] [backup] " + "rewarded ad closed");
                                    }

                                    @Override
                                    public void onError(Ad ad, AdError adError) {
                                        Log.d(TAG, "[" + backupAds + "] [backup] " + "failed to load rewarded ad: " + adError.getErrorMessage() + ", try to load backup ad: " + backupAds);
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        if (showRewardedAdIfLoaded) {
                                            showRewardedBackupAd(onComplete, onError);
                                        } else {
                                            onLoaded.onRewardedAdLoaded();
                                        }
                                        Log.d(TAG, "[" + backupAds + "] [backup] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {

                                    }
                                })
                                .build());
                        break;

                    case WORTISE:
                        wortiseRewardedAd = new com.wortise.ads.rewarded.RewardedAd(activity, wortiseRewardedId);
                        wortiseRewardedAd.setListener(new com.wortise.ads.rewarded.RewardedAd.Listener() {
                            @Override
                            public void onRewardedRevenuePaid(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd, @NonNull RevenueData revenueData) {

                            }

                            @Override
                            public void onRewardedImpression(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {

                            }

                            @Override
                            public void onRewardedFailedToShow(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd, @NonNull com.wortise.ads.AdError adError) {
                                Log.d(TAG, "[" + backupAds + "] [backup] " + "failed to load rewarded ad: " + adError + ", try to load backup ad: " + backupAds);
                            }

                            @Override
                            public void onRewardedFailedToLoad(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd, @NonNull com.wortise.ads.AdError adError) {
                                Log.d(TAG, "[" + backupAds + "] [backup] " + "failed to load rewarded ad: " + adError + ", try to load backup ad: " + backupAds);
                            }

                            @Override
                            public void onRewardedClicked(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {

                            }

                            @Override
                            public void onRewardedCompleted(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd, @NonNull Reward reward) {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "[" + backupAds + "] [backup] " + "rewarded ad complete");
                            }

                            @Override
                            public void onRewardedDismissed(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {
                                Log.d(TAG, "[" + backupAds + "] [backup] " + "rewarded ad dismissed");
                            }

                            @Override
                            public void onRewardedLoaded(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {
                                if (showRewardedAdIfLoaded) {
                                    showRewardedAd(onComplete, onError);
                                } else {
                                    onLoaded.onRewardedAdLoaded();
                                }
                                Log.d(TAG, "[" + backupAds + "] [backup]" + "rewarded ad loaded");
                            }

                            @Override
                            public void onRewardedShown(@NonNull com.wortise.ads.rewarded.RewardedAd rewardedAd) {

                            }
                        });
                        wortiseRewardedAd.loadAd();
                        break;
                }
            }
        }

        public void showRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdErrorListener onError) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (mainAds) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        if (adMobRewardedAd != null) {
                            adMobRewardedAd.show(activity, rewardItem -> {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "The user earned the reward.");
                            });
                        } else {
                            showRewardedBackupAd(onComplete, onError);
                        }
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        if (adManagerRewardedAd != null) {
                            adManagerRewardedAd.show(activity, rewardItem -> {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "The user earned the reward.");
                            });
                        } else {
                            showRewardedBackupAd(onComplete, onError);
                        }
                        break;

                    case FAN:
                    case FACEBOOK:
                        if (fanRewardedVideoAd != null && fanRewardedVideoAd.isAdLoaded()) {
                            fanRewardedVideoAd.show();
                        } else {
                            showRewardedBackupAd(onComplete, onError);
                        }
                        break;

                    case WORTISE:
                        if (wortiseRewardedAd != null && wortiseRewardedAd.isAvailable()) {
                            wortiseRewardedAd.showAd();
                        } else {
                            showRewardedBackupAd(onComplete, onError);
                        }
                        break;

                    default:
                        onError.onRewardedAdError();
                        break;
                }
            }

        }

        public void showRewardedBackupAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdErrorListener onError) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAds) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        if (adMobRewardedAd != null) {
                            adMobRewardedAd.show(activity, rewardItem -> {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "The user earned the reward.");
                            });
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        if (adManagerRewardedAd != null) {
                            adManagerRewardedAd.show(activity, rewardItem -> {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "The user earned the reward.");
                            });
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    case FAN:
                    case FACEBOOK:
                        if (fanRewardedVideoAd != null && fanRewardedVideoAd.isAdLoaded()) {
                            fanRewardedVideoAd.show();
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    case WORTISE:
                        if (wortiseRewardedAd != null && wortiseRewardedAd.isAvailable()) {
                            wortiseRewardedAd.showAd();
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    default:
                        onError.onRewardedAdError();
                        break;
                }
            }

        }

        public void destroyRewardedAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (mainAds) {
                    case FAN:
                    case FACEBOOK:
                        if (fanRewardedVideoAd != null) {
                            fanRewardedVideoAd.destroy();
                            fanRewardedVideoAd = null;
                        }
                        break;
                }

                switch (backupAds) {
                    case FAN:
                    case FACEBOOK:
                        if (fanRewardedVideoAd != null) {
                            fanRewardedVideoAd.destroy();
                            fanRewardedVideoAd = null;
                        }
                        break;
                }
            }
        }

    }

}