package com.alexxpasta.nativeadsinrecyclerview.ads;

import android.content.Context;
import android.util.Log;

import com.alexxpasta.nativeadsinrecyclerview.constant.Config;
import com.alexxpasta.nativeadsinrecyclerview.constant.Constant;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexxpasta on 2017/4/15.
 */

public class AdsProvider {
    private static final String TAG = AdsProvider.class.getSimpleName();

    private FanAdsProvider fanAdsProvider;
    private MoPubAdsProvider moPubAdsProvider;
    private boolean isFanLoaded;
    private boolean isMoPubLoaded;
    private int lastAdPosition = Constant.POSITION_NOT_EXIST;

    public AdsProvider(Context context, final OnAdsLoadedListener onAdsLoadedListener) {
        initAds(context, onAdsLoadedListener);
    }

    public boolean isLoaded() {
        return isFanLoaded && isMoPubLoaded;
    }

    private void initAds(Context context, final OnAdsLoadedListener onAdsLoadedListener) {
        fanAdsProvider = new FanAdsProvider(this, context, new OnAdsLoadedListener() {
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

        moPubAdsProvider = new MoPubAdsProvider(this, context, new OnAdsLoadedListener() {
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

    private Object pollAd() {
        Object ad = fanAdsProvider.pollAd();
        if (ad == null) {
            Log.d(TAG, "[pollAd] FAN pool is empty. Poll ad from MoPub instead.");
            ad = moPubAdsProvider.pollAd();
        }
        return ad;
    }

    public List<Integer> insertAds(List<Object> target, int lastVisibleItemPosition) {
        List<Integer> insertPositions = getInsertPositions(target.size(), lastAdPosition, lastVisibleItemPosition);
        List<Integer> successInsertedPositions = new LinkedList<>();

        for (int position : insertPositions) {
            Object ad = pollAd();
            if (ad == null) {
                Log.d(TAG, "[insertAds] All ads are consumed");
                break;
            }
            target.add(position, ad);
            lastAdPosition = position;
            successInsertedPositions.add(position);
            Log.d(TAG, "[insertAds] Insert ad at position: " + position);
        }

        return successInsertedPositions;
    }

    private static List<Integer> getInsertPositions(int targetSize, int lastAdPosition, int lastVisibleItemPosition) {
        List<Integer> positionsForInsert = new LinkedList<>();

        int position;
        if (lastAdPosition == Constant.POSITION_NOT_EXIST) {
            position = Math.max(Config.MIN_AD_POSITION, lastAdPosition + 1);
        } else {
            position = Math.max(lastAdPosition + Config.MIN_DISTANCE_BETWEEN_ADS, lastVisibleItemPosition + 1);
        }

        int maxPosition = Math.min(targetSize, Config.MAX_AD_POSITION);

        while (position <= maxPosition) {
            positionsForInsert.add(position);
            position += Config.MIN_DISTANCE_BETWEEN_ADS;

            targetSize++;
            maxPosition = Math.min(targetSize, Config.MAX_AD_POSITION);
        }

        return positionsForInsert;
    }

    public boolean shouldStopLoadAd() {
        if (lastAdPosition != Constant.POSITION_NOT_EXIST &&
                lastAdPosition + Config.MIN_DISTANCE_BETWEEN_ADS > Config.MAX_AD_POSITION) {
            Log.d(TAG, "[shouldStopLoadAd] Ads are enough, stop loading!");
            return true;
        }

        return false;
    }
}
