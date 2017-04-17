package com.alexxpasta.nativeadsinrecyclerview.ads;

import android.content.Context;
import android.util.Log;

import com.alexxpasta.nativeadsinrecyclerview.constant.Config;
import com.alexxpasta.nativeadsinrecyclerview.constant.Constant;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexxpasta on 2017/4/15.
 */

public class AdsProvider {
    private static final String TAG = AdsProvider.class.getSimpleName();
    private static final AdsProvider instance = new AdsProvider();
    private FanAdsProvider fanAdsProvider;
    private MoPubAdsProvider moPubAdsProvider;
    private boolean isFanLoaded;
    private boolean isMoPubLoaded;
    private int lastAdPosition = Constant.POSITION_NOT_EXIST;

    public static AdsProvider getInstance() {
        return instance;
    }

    private AdsProvider() {
    }

    public boolean isLoaded() {
        return isFanLoaded && isMoPubLoaded;
    }

    public void initAds(Context context, final OnAdsLoadedListener onAdsLoadedListener) {
        lastAdPosition = Constant.POSITION_NOT_EXIST;
        fanAdsProvider = FanAdsProvider.getInstance();
        fanAdsProvider.initAds(context, new OnAdsLoadedListener() {
            @Override
            public void onAdsLoaded() {
                isFanLoaded = true;
                onAdsLoadedListener.onAdsLoaded();
            }

            @Override
            public void onAdError() {
                isFanLoaded = true;
            }
        });
        moPubAdsProvider = MoPubAdsProvider.getInstance();
        moPubAdsProvider.initAds(context, new OnAdsLoadedListener() {
            @Override
            public void onAdsLoaded() {
                isMoPubLoaded = true;

                if (isFanLoaded) {
                    onAdsLoadedListener.onAdsLoaded();
                }
            }

            @Override
            public void onAdError() {
                isMoPubLoaded = true;
            }
        });
    }

    public boolean shouldStopLoadAd() {
        if (lastAdPosition != Constant.POSITION_NOT_EXIST &&
                lastAdPosition + Config.MIN_DISTANCE_BETWEEN_ADS > Config.MAX_AD_POSITION) {
            Log.d(TAG, "[shouldStopLoadAd] Ads are enough, stop loading!");
            return true;
        }

        return false;
    }

    public Object pollAd() {
        Object ad = fanAdsProvider.pollAd();
        if (ad == null) {
            Log.d(TAG, "[pollAd] FAN pool is empty. Poll ad from MoPub instead.");
            ad = moPubAdsProvider.pollAd();
        }
        return ad;
    }

    public List<Integer> insertAds(List<Object> target, int lastVisibleItemPosition) {
        int numAdsRequired = getNumAdsRequired(target.size(), lastAdPosition, lastVisibleItemPosition);
        if (numAdsRequired <= 0) {
            return null;
        }

        List<Integer> insertedPositions = new ArrayList<>(numAdsRequired);
        while (numAdsRequired > 0) {
            int index = getInsertPosition(target.size(), lastAdPosition, lastVisibleItemPosition);
            Object ad = pollAd();
            if (ad == null) {
                Log.d(TAG, "[insertAds] All ads are consumed");
                break;
            }
            target.add(index, ad);
            lastAdPosition = index;
            insertedPositions.add(index);
            numAdsRequired--;
            Log.d(TAG, "[insertAds] Insert ad at position: " + index);
        }

        return insertedPositions;
    }

    public static int getInsertPosition(int targetSize, int lastAdPosition, int lastVisibleItemPosition) {
        int position;
        if (lastAdPosition == Constant.POSITION_NOT_EXIST) {
            position = Math.max(Config.MIN_AD_POSITION, lastAdPosition + 1);
        } else {
            position = Math.max(lastAdPosition + Config.MIN_DISTANCE_BETWEEN_ADS, lastVisibleItemPosition + 1);
        }

        int maxPosition = Math.min(targetSize, Config.MAX_AD_POSITION);

        if (position > maxPosition) {
            return Constant.POSITION_OVER_MAX;
        }

        return position;
    }

    public static int getNumAdsRequired(int targetSize, int lastAdPosition, int lastVisibleItemPosition) {
        int firstPosition = getInsertPosition(targetSize, lastAdPosition, lastVisibleItemPosition);
        if (firstPosition == Constant.POSITION_OVER_MAX) {
            return 0;

        }

        int maxPosition = Math.min(targetSize, Config.MAX_AD_POSITION); // FIXME: targetSize will change during inserting
        return (maxPosition - firstPosition) / Config.MIN_DISTANCE_BETWEEN_ADS + 1;
    }
}
