package com.heymundomx.ads.sdkdemo.adapter;

import static com.heymundomx.ads.sdk.util.Constant.ADMOB;
import static com.heymundomx.ads.sdk.util.Constant.APPLOVIN;
import static com.heymundomx.ads.sdk.util.Constant.APPLOVIN_DISCOVERY;
import static com.heymundomx.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.heymundomx.ads.sdk.util.Constant.FACEBOOK;
import static com.heymundomx.ads.sdk.util.Constant.FAN;
import static com.heymundomx.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.heymundomx.ads.sdk.util.Constant.STARTAPP;
import static com.heymundomx.ads.sdk.util.Constant.WORTISE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.heymundomx.ads.sdk.format.NativeAdViewHolder;
import com.heymundomx.ads.sdkdemo.R;
import com.heymundomx.ads.sdkdemo.data.Constant;
import com.heymundomx.ads.sdkdemo.database.SharedPref;
import com.heymundomx.ads.sdkdemo.model.Post;

import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class AdapterPost extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private List<Post> posts;
    private OnItemClickListener onItemClickListener;
    private final int VIEW_ITEM = 1;
    private final int VIEW_AD = 2;
    SharedPref sharedPref;

    public interface OnItemClickListener {
        void onItemClick(View view, Post obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.onItemClickListener = mItemClickListener;
    }

    public AdapterPost(Context context, List<Post> posts) {
        this.posts = posts;
        this.context = context;
        this.sharedPref = new SharedPref(context);
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;
        public LinearLayout lytParent;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            image = v.findViewById(R.id.image);
            lytParent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            vh = new OriginalViewHolder(v);
        } else if (viewType == VIEW_AD) {
            View v = switch (Constant.NATIVE_STYLE) {
                case "news" ->
                        LayoutInflater.from(parent.getContext()).inflate(com.heymundomx.ads.sdk.R.layout.view_native_ad_news, parent, false);
                case "radio" ->
                        LayoutInflater.from(parent.getContext()).inflate(com.heymundomx.ads.sdk.R.layout.view_native_ad_radio, parent, false);
                case "video_small" ->
                        LayoutInflater.from(parent.getContext()).inflate(com.heymundomx.ads.sdk.R.layout.view_native_ad_video_small, parent, false);
                case "video_large" ->
                        LayoutInflater.from(parent.getContext()).inflate(com.heymundomx.ads.sdk.R.layout.view_native_ad_video_large, parent, false);
                default ->
                        LayoutInflater.from(parent.getContext()).inflate(com.heymundomx.ads.sdk.R.layout.view_native_ad_medium, parent, false);
            };
            vh = new NativeAdViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            vh = new OriginalViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder vItem) {
            final Post p = posts.get(position);

            vItem.name.setText(p.name);
            RequestBuilder<Drawable> requestBuilder= Glide.with(holder.itemView.getContext())
                    .asDrawable().sizeMultiplier(0.1f);
            Glide.with(context)
                    .load(p.image.replace(" ", "%20"))
                    .thumbnail(requestBuilder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(vItem.image);

            vItem.lytParent.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, p, position);
                }
            });

        } else if (holder instanceof NativeAdViewHolder vItem) {
            vItem.loadNativeAd(context,
                    Constant.AD_STATUS,
                    1,
                    Constant.AD_NETWORK,
                    Constant.BACKUP_AD_NETWORK,
                    Constant.ADMOB_NATIVE_ID,
                    Constant.GOOGLE_AD_MANAGER_NATIVE_ID,
                    Constant.FAN_NATIVE_ID,
                    Constant.APPLOVIN_NATIVE_MANUAL_ID,
                    Constant.APPLOVIN_BANNER_MREC_ZONE_ID,
                    Constant.WORTISE_NATIVE_ID,
                    sharedPref.getIsDarkTheme(),
                    false,
                    Constant.NATIVE_STYLE,
                    R.color.colorNativeBackgroundLight,
                    R.color.colorNativeBackgroundDark
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        Post post = posts.get(position);
        if (post != null) {
            if (post.name == null || post.name.isEmpty()) {
                return VIEW_AD;
            } else {
                return VIEW_ITEM;
            }
        } else {
            return VIEW_ITEM;
        }
    }

    public void setListData(List<Post> posts, int totalPosts) {
        this.posts = posts;
        insertNativeAd(posts, totalPosts);
        notifyDataSetChanged();
    }

    private void insertNativeAd(List<Post> posts, int totalPosts) {
        switch (Constant.AD_NETWORK) {
            case ADMOB:
            case GOOGLE_AD_MANAGER:
            case FAN:
            case FACEBOOK:
            case APPLOVIN:
            case APPLOVIN_MAX:
            case APPLOVIN_DISCOVERY:
            case STARTAPP:
            case WORTISE:
                int maxNumberNativeAd;
                if (totalPosts >= Constant.NATIVE_AD_INTERVAL) {
                    maxNumberNativeAd = (totalPosts / Constant.NATIVE_AD_INTERVAL);
                } else {
                    maxNumberNativeAd = 1;
                }
                int limitNativeAd = (maxNumberNativeAd * Constant.NATIVE_AD_INTERVAL) + Constant.NATIVE_AD_INDEX;
                if (posts.size() >= Constant.NATIVE_AD_INDEX) {
                    for (int i = Constant.NATIVE_AD_INDEX; i < limitNativeAd; i += Constant.NATIVE_AD_INTERVAL) {
                        posts.add(i, new Post());
                    }
                }
                break;
            default:
                //none
                break;
        }
    }

    @SuppressWarnings("unused")
    public void resetListData() {
        this.posts.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}