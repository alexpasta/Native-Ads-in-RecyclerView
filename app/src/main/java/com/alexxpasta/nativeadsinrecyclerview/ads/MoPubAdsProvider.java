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

class MoPubAdsProvider {
    private static final String TAG = MoPubAdsProvider.class.getSimpleName();

    private AdsProvider commonAdsProvider;
    private MoPubNative moPubNative;
    private List<NativeAd> adsPool = new LinkedList<>();
    private boolean isLoading;

    MoPubAdsProvider(AdsProvider adsProvider, Context context, final OnAdsLoadedListener onAdsLoadedListener) {
        commonAdsProvider = adsProvider;
        initAds(context, onAdsLoadedListener);
    }

    private void initAds(Context context, final OnAdsLoadedListener onAdsLoadedListener) {
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
            .mainImageId(R.id.native_ad_main_image)
            .iconImageId(R.id.native_ad_icon)
            .titleId(R.id.native_ad_title)
            .textId(R.id.native_ad_social_context)
            .callToActionId(R.id.native_ad_cta)
            .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
            .build();

        moPubNative.registerAdRenderer(new MoPubStaticNativeAdRenderer(viewBinder));
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
