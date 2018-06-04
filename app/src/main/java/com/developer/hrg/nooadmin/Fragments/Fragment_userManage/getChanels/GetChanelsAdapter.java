package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.getChanels;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.hrg.nooadmin.Helper.Config;
import com.developer.hrg.nooadmin.Models.Chanel;
import com.developer.hrg.nooadmin.R;

import java.util.ArrayList;

public class GetChanelsAdapter extends RecyclerView.Adapter<GetChanelsAdapter.Holder> {

    Context context ;
    ArrayList<Chanel> chanels;
    MyClickListener myClickListener;
        public GetChanelsAdapter(Context context , ArrayList<Chanel> chanels) {
            this.context=context;
            this.chanels=chanels;
        }
 public interface MyClickListener{
     public void chanel_clicked(int position, View view) ;

 }

 public void setMyClickListener(MyClickListener myClickListener) {
     this.myClickListener=myClickListener;
 }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.custom_chanel,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Chanel chanel = chanels.get(position);
        holder.tv_name.setText(chanel.getName());
        holder.tv_admin_name.setText(chanel.getUsername());
        holder.tv_date.setText(chanel.getUpdated_at());
        Glide.with(context).load(Config.CHANEL_THUMB_BASE+chanel.getThumb()).into(holder.iv_profile);


    }

    @Override
    public int getItemCount() {
        return chanels.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_date , tv_admin_name  ;
        ImageView iv_profile  ;
        public Holder(View itemView) {
            super(itemView);
            tv_name=(TextView)itemView.findViewById(R.id.tv_custom_chanel_name);
            tv_date=(TextView)itemView.findViewById(R.id.tv_custom_chanel_date);
            tv_admin_name=(TextView)itemView.findViewById(R.id.tv_custom_chanel_admin);
            iv_profile=(ImageView)itemView.findViewById(R.id.iv_custom_chanel_photo);

        }
    }


}
