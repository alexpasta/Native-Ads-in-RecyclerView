package com.alexxpasta.nativeadsinrecyclerview.ads;

import android.content.Context;
import android.util.Log;

import com.alexxpasta.nativeadsinrecyclerview.R;
import com.alexxpasta.nativeadsinrecyclerview.constant.LocalConfig;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexxpasta on 2017/4/16.
 */

public class MoPubAdsProvider {
    private static final String TAG = MoPubAdsProvider.class.getSimpleName();
    private static final MoPubAdsProvider instance = new MoPubAdsProvider();
    private MoPubNative moPubNative;
    private List<NativeAd> adsPool = new LinkedList<>();
    private boolean isLoading;

    public static MoPubAdsProvider getInstance() {
        return instance;
    }

    private MoPubAdsProvider() {
    }

    public void initAds(Context context, final OnAdsLoadedListener onAdsLoadedListener) {
        moPubNative = new MoPubNative(context, LocalConfig.MOPUB_AD_UNIT_ID, new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(NativeAd nativeAd) {
                Log.d(TAG, "[onNativeLoad]");
                adsPool.add(nativeAd);
                isLoading = false;
                onAdsLoadedListener.onAdsLoaded();
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                Log.w(TAG, "[onNativeFail] ErrorMessage: " + errorCode.toString());
                isLoading = false;
                onAdsLoadedListener.onAdError();
            }
        });

        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.list_item_mopub)
            .mainImageId(R.id.native_main_image)
            .iconImageId(R.id.native_icon_image)
            .titleId(R.id.native_title)
            .textId(R.id.native_text)
            .callToActionId(R.id.native_cta)
            .build();

        moPubNative.registerAdRenderer(new MoPubStaticNativeAdRenderer(viewBinder));
        loadAds();
    }

    public NativeAd pollAd() {
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
        if (!isLoading && !AdsProvider.getInstance().shouldStopLoadAd()) {
            Log.d(TAG, "[loadAds] start load MoPub ad ...");
            isLoading = true;

            EnumSet<RequestParameters.NativeAdAsset> assets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT
            );

            RequestParameters requestParameters = new RequestParameters.Builder()
            .desiredAssets(assets)
            .build();

            moPubNative.makeRequest(requestParameters);
        }
    }
}
