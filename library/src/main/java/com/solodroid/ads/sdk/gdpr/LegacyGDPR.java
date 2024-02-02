package com.solodroid.ads.sdk.gdpr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class LegacyGDPR {

    private final Activity activity;

    public LegacyGDPR(Activity activity) {
        this.activity = activity;
    }

    public static Bundle getBundleAd(Activity activity) {
        Bundle extras = new Bundle();
        ConsentInformation consentInformation = ConsentInformation.getInstance(activity);
        ConsentStatus consentStatus = consentInformation.getConsentStatus();
        if (consentStatus != null && consentStatus.equals(ConsentStatus.NON_PERSONALIZED)) {
            extras.putString("npa", "1");
        }
        return extras;
    }

    public void updateLegacyGDPRConsentStatus(String adMobPublisherId, String privacyPolicyUrl) {
        if (activity == null) {
            // Manejar caso en que activity es nulo
            return;
        }

        ConsentInformation consentInformation = ConsentInformation.getInstance(activity);
        consentInformation.requestConsentInfoUpdate(new String[]{adMobPublisherId}, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated. Display the consentForm if Consent Status is UNKNOWN
                if (consentStatus == ConsentStatus.UNKNOWN) {
                    new GDPRForm(activity).displayConsentForm(privacyPolicyUrl);
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // Consent consentForm error.
                Log.e("GDPR", errorDescription);
            }
        });
        Log.d("GDPR", "Legacy GDPR is selected");
    }

    private static class GDPRForm {

        private ConsentForm consentForm;
        private final Activity activity;

        private GDPRForm(Activity activity) {
            this.activity = activity;
        }

        private void displayConsentForm(String privacyPolicyUrl) {
            if (activity == null) {
                // Manejar caso en que activity es nulo
                return;
            }

            URL privacyPolicyURL = getUrlPrivacyPolicy(privacyPolicyUrl);
            if (privacyPolicyURL == null) {
                // Manejar caso en que la URL de la política de privacidad es nula
                return;
            }

            ConsentForm.Builder builder = new ConsentForm.Builder(activity, privacyPolicyURL);
            builder.withPersonalizedAdsOption();
            builder.withNonPersonalizedAdsOption();
            builder.withListener(new ConsentFormListener() {
                @Override
                public void onConsentFormLoaded() {
                    // Consent consentForm loaded successfully.
                    consentForm.show();
                }

                @Override
                public void onConsentFormOpened() {
                    // Consent consentForm was displayed.
                }

                @Override
                public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                    // Consent consentForm was closed.
                    Log.e("GDPR", "Status : " + consentStatus);
                }

                @Override
                public void onConsentFormError(String errorDescription) {
                    // Consent consentForm error.
                    Log.e("GDPR", errorDescription);
                }
            });
            consentForm = builder.build();
            consentForm.load();
        }

        private URL getUrlPrivacyPolicy(String privacyPolicyUrl) {
            if (privacyPolicyUrl == null) {
                // Manejar caso en que privacyPolicyUrl es nulo
                return null;
            }

            URL mUrl = null;
            try {
                mUrl = new URL(privacyPolicyUrl);
            } catch (MalformedURLException e) {
                Log.e("GDPR", Objects.requireNonNull(e.getMessage()));
            }
            return mUrl;
        }
    }
}
