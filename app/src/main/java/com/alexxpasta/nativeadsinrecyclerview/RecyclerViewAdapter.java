package com.alexxpasta.nativeadsinrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexxpasta on 2017/4/15.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();

    private List<Object> dataSet;

    private int POST_TYPE = 1;
    private int AD_FAN_TYPE = 2;
    private int AD_MOPUB_TYPE = 3;

    public RecyclerViewAdapter(List<Object> dataSet) {
        this.dataSet = dataSet;
    }

    public static class PostHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView message;
        private ImageView avatar;
        private ImageView mainImage;

        public PostHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.post_name);
            message = (TextView) view.findViewById(R.id.post_message);
            avatar = (ImageView) view.findViewById(R.id.post_avatar);
            mainImage = (ImageView) view.findViewById(R.id.post_main_image);
        }

        public void bindView(PostItem item) {
            name.setText(item.name);
            message.setText(item.message);
            avatar.setImageResource(item.avatarResId);
            mainImage.setImageResource(item.imageResId);
        }
    }

    public static class FanAdHolder extends RecyclerView.ViewHolder {
        private View adContainer;
        private ImageView adIcon;
        private TextView adTitle;
        private MediaView adMedia;
        private TextView adSocialContext;
        private Button adCallToAction;

        public FanAdHolder(View view) {
            super(view);
            adContainer = view.findViewById(R.id.native_ad_container);
            adIcon = (ImageView) view.findViewById(R.id.native_ad_icon);
            adTitle = (TextView) view.findViewById(R.id.native_ad_title);
            adMedia = (MediaView) view.findViewById(R.id.native_ad_media);
            adSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
            adCallToAction = (Button)view.findViewById(R.id.native_ad_cta);
        }

        public void bindView(com.facebook.ads.NativeAd ad) {
            adTitle.setText(ad.getAdTitle());
            adMedia.setNativeAd(ad);
            adSocialContext.setText(ad.getAdSocialContext());
            adCallToAction.setText(ad.getAdCallToAction());
            NativeAd.Image icon = ad.getAdIcon();
            NativeAd.downloadAndDisplayImage(icon, adIcon);

            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(adMedia);
            clickableViews.add(adCallToAction);
            ad.registerViewForInteraction(adContainer, clickableViews);
        }
    }

    public static class MoPubAdHolder extends RecyclerView.ViewHolder {
        private View adContainer;
        public MoPubAdHolder(View view) {
            super(view);
            adContainer = view.findViewById(R.id.native_ad_container);
        }

        public void bindView(com.mopub.nativeads.NativeAd ad) {
            ad.clear(adContainer);
            ad.renderAdView(adContainer);
            ad.prepare(adContainer);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AD_FAN_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fan, parent, false);
            return new FanAdHolder(v);
        } else if (viewType == AD_MOPUB_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_mopub, parent, false);
            return new MoPubAdHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post, parent, false);
            return new PostHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();
        if (type == AD_FAN_TYPE) {
            ((FanAdHolder) holder).bindView((com.facebook.ads.NativeAd) dataSet.get(position));
        } else if (type == AD_MOPUB_TYPE) {
            ((MoPubAdHolder) holder).bindView((com.mopub.nativeads.NativeAd) dataSet.get(position));
        } else {
            ((PostHolder) holder).bindView((PostItem) dataSet.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = dataSet.get(position);

        if (item instanceof com.facebook.ads.NativeAd) {
            return AD_FAN_TYPE;
        } else if (item instanceof com.mopub.nativeads.NativeAd) {
            return  AD_MOPUB_TYPE;
        } else {
            return POST_TYPE;
        }
    }
}
