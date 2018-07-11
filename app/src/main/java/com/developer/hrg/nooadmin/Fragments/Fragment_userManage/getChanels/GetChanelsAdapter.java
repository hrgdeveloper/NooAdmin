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
     public void chanel_long_clicked(int position, View view) ;
     public void comment_clicked(int position, View view);
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
        holder.tv_cm_count.setText(chanel.getCm_count()+"");
        // inja 3 ta halat dare ya ham maseeage khalie ham type ke yani hanooz payami vase kanal ersal nashode
        // ya message khalie ke yani ye file bedone matn ersal shode
        // ya matn dashte akharan message ke matno neshon midim
        if (chanel.getLast_message()==null && chanel.getType()==null) {
            holder.tv_last.setText("بدون پیام ...");
        }else if (chanel.getLast_message()==null) {
            holder.tv_last.setText(chanel.getStringFromType());
        }else {
            holder.tv_last.setText(chanel.getLast_message());
        }
        Glide.with(context).load(Config.CHANEL_THUMB_BASE_OFFILNE+chanel.getThumb()).into(holder.iv_profile);


    }

    @Override
    public int getItemCount() {
        return chanels.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_date , tv_admin_name , tv_last , tv_cm_count  ;
        ImageView iv_profile  ;
        public Holder(View itemView) {
            super(itemView);
            tv_name=(TextView)itemView.findViewById(R.id.tv_custom_chanel_name);
            tv_cm_count=(TextView)itemView.findViewById(R.id.tv_custom_chanel_cm_count);
            tv_date=(TextView)itemView.findViewById(R.id.tv_custom_chanel_date);
            tv_admin_name=(TextView)itemView.findViewById(R.id.tv_custom_chanel_admin);
            iv_profile=(ImageView)itemView.findViewById(R.id.iv_custom_chanel_photo);
            tv_last=(TextView)itemView.findViewById(R.id.tv_custom_chanel_last);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myClickListener.chanel_clicked(getAdapterPosition(),view);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    myClickListener.chanel_long_clicked(getAdapterPosition(),view);
                    return true;
                }
            });
            tv_cm_count.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myClickListener.comment_clicked(getAdapterPosition(),view);
                }
            });

        }
    }


}
