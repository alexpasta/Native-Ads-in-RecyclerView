package com.alexxpasta.nativeadsinrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.ads.MediaView;

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
        private TextView tvName;
        private TextView tvMessage;

        public PostHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvMessage = (TextView) view.findViewById(R.id.tv_message);
        }

        public void bindView(PostItem item) {
            tvName.setText(item.name);
            tvMessage.setText(item.message);
        }
    }

    public static class FanAdHolder extends RecyclerView.ViewHolder {
        private View adContainer;
        private MediaView adMedia;
        private TextView adSocialContext;
        private Button adCallToAction;

        public FanAdHolder(View view) {
            super(view);
            adContainer = view.findViewById(R.id.ad_container);
            adMedia = (MediaView) view.findViewById(R.id.native_ad_media);
            adSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
            adCallToAction = (Button)view.findViewById(R.id.native_ad_call_to_action);
        }

        public void bindView(com.facebook.ads.NativeAd ad) {
            if (ad == null) {
                adContainer.setVisibility(View.GONE);
            } else {
                adSocialContext.setText(ad.getAdSocialContext());
                adCallToAction.setText(ad.getAdCallToAction());
                adMedia.setNativeAd(ad);

                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(adMedia);
                clickableViews.add(adCallToAction);
                ad.registerViewForInteraction(adContainer, clickableViews);
            }
        }
    }

    public static class MoPubAdHolder extends RecyclerView.ViewHolder {
        private View adContainer;
        public MoPubAdHolder(View view) {
            super(view);
            adContainer = view.findViewById(R.id.native_outer_view);
        }

        public void bindView(com.mopub.nativeads.NativeAd ad) {
            if (ad == null) {
                adContainer.setVisibility(View.GONE);
            } else {
                ad.clear(adContainer);
                ad.renderAdView(adContainer);
                ad.prepare(adContainer);
            }
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
