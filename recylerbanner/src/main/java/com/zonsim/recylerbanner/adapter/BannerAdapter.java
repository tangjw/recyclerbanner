package com.zonsim.recylerbanner.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zonsim.recylerbanner.R;
import com.zonsim.recylerbanner.entity.Banner;

import java.util.List;

/**
 * ^-^
 * Created by tang-jw on 2017/6/26.
 */

public class BannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private List<Banner> mBanners;
    
    public BannerAdapter(@NonNull List<Banner> banners) {
        mBanners = banners;
    }
    
    public void replaceData(@NonNull List<Banner> banners) {
        mBanners = banners;
        notifyDataSetChanged();
    }
    
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        
        return new ItemVH(inflate);
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Banner banner = mBanners.get(holder.getAdapterPosition()%mBanners.size());
        
        ItemVH itemVH = (ItemVH) holder;
    
        
        Glide.with(itemVH.ivBanner.getContext())
                .load(banner.getUrl())
                .centerCrop()
                .crossFade()
                .into(itemVH.ivBanner);
        
    }
    
    @Override
    public int getItemCount() {
        return mBanners.size() < 2 ? mBanners.size() : Integer.MAX_VALUE;
    }
    
    
    private static class ItemVH extends RecyclerView.ViewHolder {
        
        ImageView ivBanner;
        
        ItemVH(View itemView) {
            super(itemView);
            ivBanner = (ImageView) itemView.findViewById(R.id.iv_banner);
        }
    }
}
