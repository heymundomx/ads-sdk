package com.heymundomx.ads.sdk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.heymundomx.ads.sdk.R;

public class BannerAdView extends LinearLayout {
    private Context mContext;

    public BannerAdView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public BannerAdView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public BannerAdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    public BannerAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView() {
        inflate(mContext, R.layout.view_banner_ad, this);
    }

}