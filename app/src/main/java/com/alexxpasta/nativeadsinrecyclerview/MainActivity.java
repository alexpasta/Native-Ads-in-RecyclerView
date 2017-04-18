package com.alexxpasta.nativeadsinrecyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alexxpasta.nativeadsinrecyclerview.ads.AdsProvider;
import com.alexxpasta.nativeadsinrecyclerview.ads.OnAdsLoadedListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Object> dataSet = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;

    private AdsProvider adsProvider;
    private int lastAdPosition = -1; // -1 means we have never inserted ads

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initAds();
        fetchMoreData();
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new RecyclerViewAdapter(dataSet);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int totalItemCount = adapter.getItemCount();
                if (totalItemCount == layoutManager.findLastVisibleItemPosition() + 1) {
                    fetchMoreData();
                }
            }
        });
    }

    private void initAds() {
        adsProvider = new AdsProvider(this, new OnAdsLoadedListener() {
            @Override
            public void onAdsLoaded() {
                Log.d(TAG, "[onAdsLoaded]");
                insertAds();
            }

            @Override
            public void onAdError() {
                Log.d(TAG, "[onAdError]");
            }
        });
    }

    private void fetchMoreData() {
        List<PostItem> newData = FakeTimelineApi.fetchTimeline();
        int originalSize = adapter.getItemCount();
        int newDataSize = newData.size();
        dataSet.addAll(newData);
        adapter.notifyItemRangeInserted(originalSize, newDataSize);

        if (adsProvider.isLoaded()) {
            insertAds();
        }
    }

    private void insertAds() {
        List<Integer> insertedPositions = adsProvider.insertAds(dataSet, layoutManager.findLastVisibleItemPosition());
        if (insertedPositions != null && !insertedPositions.isEmpty()) {
            for (int position : insertedPositions) {
                adapter.notifyItemInserted(position);
            }
        }
    }
}
