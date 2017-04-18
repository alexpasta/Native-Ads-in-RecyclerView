package com.alexxpasta.nativeadsinrecyclerview.ads;

import android.content.Context;
import android.util.Log;

import com.alexxpasta.nativeadsinrecyclerview.constant.Config;
import com.alexxpasta.nativeadsinrecyclerview.constant.LocalConfig;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexxpasta on 2017/4/15.
 */

class FanAdsProvider {
    private static final String TAG = FanAdsProvider.class.getSimpleName();

    private AdsProvider commonAdsProvider;
    private NativeAdsManager adsManager;
    private List<NativeAd> adsPool = new LinkedList<>();
    private boolean isLoading;

    FanAdsProvider(AdsProvider adsProvider, Context context, final OnAdsLoadedListener onAdsLoadedListener) {
        commonAdsProvider = adsProvider;
        initAds(context, onAdsLoadedListener);
    }

    private void initAds(Context context, final OnAdsLoadedListener onAdsLoadedListener) {
        adsManager = new NativeAdsManager(context, LocalConfig.FAN_PLACEMENT_ID_HOME_SCREEN, Config.FAN_POOL_SIZE);
        adsManager.setListener(new NativeAdsManager.Listener() {
            @Override
            public void onAdsLoaded() {
                while(adsPool.size() < Config.FAN_POOL_SIZE) {
                    NativeAd ad = adsManager.nextNativeAd();
                    if (ad == null) {
                        Log.d(TAG, "[onAdsLoaded] ad == null");
                        break;
                    } else {
                        Log.d(TAG, "[onAdsLoaded] ad != null");
                        adsPool.add(ad);
                    }
                }
                isLoading = false;
                onAdsLoadedListener.onAdsLoaded();
            }

            @Override
            public void onAdError(AdError adError) {
                Log.w(TAG, "[onAdError] ErrorCode: " + adError.getErrorCode() + ", ErrorMsg: " + adError.getErrorMessage());
                isLoading = false;
                onAdsLoadedListener.onAdError();
            }
        });

        loadAds();
    }

    NativeAd pollAd() {
        if (adsPool.isEmpty()) {
            loadAds();
            return null;
        }

        NativeAd ad = adsPool.remove(0);
        if (adsPool.isEmpty()) {
            loadAds();
        }
        return ad;
    }

    private void loadAds() {
        if (!isLoading && !commonAdsProvider.shouldStopLoadAd()) {
            Log.d(TAG, "[loadAds] start load FAN ads ...");
            isLoading = true;
            adsManager.loadAds(NativeAd.MediaCacheFlag.ALL);
        }
    }
}
