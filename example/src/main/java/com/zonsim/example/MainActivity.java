package com.zonsim.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zonsim.recylerbanner.RecyclerBanner;
import com.zonsim.recylerbanner.entity.Banner;
import com.zonsim.recylerbanner.indicator.BannerIndicator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private RecyclerBanner mRecyclerBanner;
    private BannerIndicator mIndicator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        List<Banner> banners = new ArrayList<>();
        
        banners.add(new Banner("http://i.imgur.com/P5JLfjk.jpg"));
        banners.add(new Banner("http://i.imgur.com/DvpvklR.jpg"));
        banners.add(new Banner("http://i.imgur.com/u6JF6JZ.jpg"));
        banners.add(new Banner("http://i.imgur.com/P5ZRSvT.jpg"));
        banners.add(new Banner("http://i.imgur.com/zD3gT4Z.jpg"));
    
    
        mRecyclerBanner = (RecyclerBanner) findViewById(R.id.iv_banner);
        mIndicator = (BannerIndicator) findViewById(R.id.indicator);
    
        mRecyclerBanner.bindIndicator(mIndicator);
        
        mRecyclerBanner.replaceBanners(banners);
    }
}
