package com.heymundomx.ads.sdkdemo.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.heymundomx.ads.sdk.format.BannerAd;
import com.heymundomx.ads.sdkdemo.R;
import com.heymundomx.ads.sdkdemo.adapter.AdapterPost;
import com.heymundomx.ads.sdkdemo.data.Constant;
import com.heymundomx.ads.sdkdemo.database.SharedPref;
import com.heymundomx.ads.sdkdemo.model.Post;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    SharedPref sharedPref;
    RecyclerView recyclerView;
    AdapterPost adapterPost;
    BannerAd.Builder bannerAd;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        getAppTheme();
        setContentView(R.layout.activity_second);
        initView();
        loadBannerAd();
        initToolbar();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                bannerAd.destroyAndDetachBanner();
                finish();
            }
        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterPost = new AdapterPost(this, new ArrayList<>());
        recyclerView.setAdapter(adapterPost);
        displayData(sharedPref.getPostList());
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (sharedPref.getIsDarkTheme()) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_dark_toolbar));
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    private void displayData(List<Post> posts) {
        if (posts != null && !posts.isEmpty()) {
            adapterPost.setListData(posts, posts.size());
            adapterPost.setOnItemClickListener((view, obj, position) -> Toast.makeText(getApplicationContext(), obj.name, Toast.LENGTH_SHORT).show());
        }
    }

    private void loadBannerAd() {
        bannerAd = new BannerAd.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setAdNetwork(Constant.AD_NETWORK)
                .setBackupAdNetwork(Constant.BACKUP_AD_NETWORK)
                .setAdMobBannerId(Constant.ADMOB_BANNER_ID)
                .setGoogleAdManagerBannerId(Constant.GOOGLE_AD_MANAGER_BANNER_ID)
                .setUnityBannerId(Constant.UNITY_BANNER_ID)
                .setAppLovinBannerId(Constant.APPLOVIN_BANNER_ID)
                .setAppLovinBannerZoneId(Constant.APPLOVIN_BANNER_ZONE_ID)
                .setIronSourceBannerId(Constant.IRONSOURCE_BANNER_ID)
                .setWortiseBannerId(Constant.WORTISE_BANNER_ID)
                .setDarkTheme(false)
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void getAppTheme() {
        if (sharedPref.getIsDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

}