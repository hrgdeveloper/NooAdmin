package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.profile;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.hrg.nooadmin.Helper.Config;
import com.developer.hrg.nooadmin.Models.Profile;
import com.developer.hrg.nooadmin.R;

import java.util.ArrayList;

/**
 * Created by hamid on 7/26/2018.
 */

public class Adapter_Profile extends PagerAdapter {
    Context context;
    ArrayList<Profile> profiles ;
    public Adapter_Profile(Context context ,ArrayList<Profile> profiles) {
        this.context=context;
        this.profiles=profiles;
    }

    @Override
    public int getCount() {
        return profiles.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_profile_viewpager,container,false);
        FrameLayout frameLayout = (FrameLayout)view.findViewById(R.id.frame_profile);
        TextView tv_delete=(TextView)view.findViewById(R.id.tv_delete_profile);
        ImageView iv_pic =(ImageView)view.findViewById(R.id.iv_profile);
        Glide.with(context).load(Config.CHANEL_PIC_BASE_ONLLINE_FINAL+profiles.get(position).getPhoto()).into(iv_pic);
        frameLayout.setBackgroundColor(ContextCompat.getColor(context,android.R.color.black));
        container.addView(view);
        return view;

    }
    public void removeItem(int position) {
        profiles.remove(position);
        notifyDataSetChanged();

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view== ((FrameLayout)object);
    }
}